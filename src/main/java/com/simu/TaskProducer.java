package com.simu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.impl.client.CloseableHttpClient;

import com.execute.SiteAnalyze;
import com.execute.SiteEstimater;
import com.execute.SiteTypeJudger;
import com.helper.SiteHelper;
import com.init.State;
import com.mysql.fabric.xmlrpc.base.Array;
import com.poi.ConnectManager;
import com.pojo.BikeArea;
import com.pojo.BikePos;
import com.pojo.Dispatcher;
import com.pojo.GaodePath;
import com.pojo.Lnglat;
import com.pojo.LoadTask;
import com.pojo.Route;
import com.pojo.SimuTask;
import com.pojo.Site;
import com.pojo.SitePlan;
import com.pojo.WaitTask;
import com.service.DispatcherServ;
import com.service.RouteServ;
import com.service.SiteServ;
import com.util.ApplicationContextProvider;
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MathUtil;
import com.util.SiteUtil;

public class TaskProducer {

	private List<Map<String, Object>> dayFiles;
	private List<Site> sites;
	private SiteServ siteServ;
	private DispatcherServ dispatcherServ;
	private RouteServ routeServ;
	private SiteAnalyze analyze;
	private CloseableHttpClient client;
	private SiteEstimater estimate;
	private SiteHelper siteHelper;
	private SiteTypeJudger judger;
	private Map<Integer, Map<String, Object>> ests;
	private Map<Integer, Set<Integer>> visitedSites;
	private Date time;
	private int startHour;
	private int endHour;

	// site(site),area(BikeArea),bikes(List),est(int),fake(List)
	private Map<Integer, Map<String, Object>> siteInfos;

	public TaskProducer() {
		dayFiles = new ArrayList<>();
		siteServ = ApplicationContextProvider.getBean(SiteServ.class);
		dispatcherServ = ApplicationContextProvider.getBean(DispatcherServ.class);
		routeServ = ApplicationContextProvider.getBean(RouteServ.class);
		analyze = new SiteAnalyze();
		estimate = ApplicationContextProvider.getBean(SiteEstimater.class);
		siteHelper = ApplicationContextProvider.getBean(SiteHelper.class);
		judger = ApplicationContextProvider.getBean(SiteTypeJudger.class);
	}

	public void init(Date time, int nowHour, int endHour) {
		this.time = time;
		this.startHour = nowHour;
		this.endHour = endHour;
		dayFiles = FilesUtil.ListFilesInDay(time);
		sites = siteServ.getAllSites();
		siteInfos = initSiteInfos();
		ests = estimate.estimateAllSites(nowHour, time);
		visitedSites = new HashMap<Integer, Set<Integer>>();
	}

	public List<SimuTask> getStartJobs(List<Dispatcher> dispatchers, int startHour) {
		List<SimuTask> startJobs = new ArrayList<>();
		for (Dispatcher dispatcher : dispatchers) {

			SimuTask task = assignTask(startHour, 0, dispatcher, null, 1);
			startJobs.add(task);
		}
		return startJobs;
	}

	private Map<Integer, Map<String, Object>> initSiteInfos() {
		Map<Integer, Map<String, Object>> sitesInfos = new HashMap<>();
		for (Site s : sites) {

			Lnglat lnglat = new Lnglat(s.getLng(), s.getLat());
			BikeArea area = CoordsUtil.getCenterArea(lnglat);

			Map<String, Object> info = new HashMap<>();
			info.put("site", s);
			info.put("area", area);

			List<Integer> list = new ArrayList<>(24);
			for (int i = 0; i < 24; i++) {
				list.add(0);
			}
			info.put("bikes", list);
			double[] changes = analyze.analyzeSiteChange(s.getId());
			info.put("changes", changes);
			int type = judger.getSiteType(changes);
			info.put("type", type);

			sitesInfos.put(s.getId(), info);
		}
		int hourCount = 0;
		for (Map<String, Object> hourFile : dayFiles) {
			List<BikePos> bikes = (List<BikePos>) hourFile.get("bikes");

			for (BikePos bike : bikes) {
				for (Integer i : sitesInfos.keySet()) {
					Map<String, Object> info = sitesInfos.get(i);
					BikeArea area = (BikeArea) info.get("area");
					if (CoordsUtil.isInArea(area, bike.getLng(), bike.getLat())) {
						List<Integer> list = (List<Integer>) info.get("bikes");
						list.set(hourCount, list.get(hourCount) + 1);
						break;
					}
				}
			}
			hourCount++;
		}
		for (Integer i : sitesInfos.keySet()) {
			Map<String, Object> info = sitesInfos.get(i);
			List<Integer> list = (List<Integer>) info.get("bikes");

			List<Integer> fake = new ArrayList<>();
			for (Integer item : list) {
				fake.add(item);
			}
			info.put("fake", fake);
			// change代表记录由于调度带来的每一次单车站点的变化
			List<Integer> dispChange = new ArrayList<>();
			info.put("change", dispChange);
		}
		return sitesInfos;
	}

	private int guessNowSiteBikes(int now, int next, int seconds) {
		if (seconds > 3600) {
			throw new RuntimeException("时间超出了");
		}
		double ratio = ((double) seconds) / 3600;
		int lag = Math.abs((now - next));
		int result = 0;
		if (now <= next) {
			result = (int) (ratio * lag) + now;
		} else {
			result = now - (int) (ratio * lag);
		}
		return result;
	}

	/**
	 * 根据装载量的多少和站点的距离对站点进行打分，返回得分最高的站点
	 * 
	 * @param siteNeeds
	 * @param dispatcher
	 * @param putOrGet
	 * @return
	 */
	public int scoreSitesByDispatcher(Map<Integer, SitePlan> siteNeeds, Dispatcher dispatcher, double loadRatio) {

		client = ConnectManager.getClient();
		SiteUtil sUtil = new SiteUtil();

		List<Integer> loadCountList = new ArrayList<>();
		List<Integer> loadDistList = new ArrayList<>();
		List<Integer> loadIDList = new ArrayList<>();

		List<Integer> unloadCountList = new ArrayList<>();
		List<Integer> unloadDistList = new ArrayList<>();
		List<Integer> unloadIDList = new ArrayList<>();

		for (Integer i : siteNeeds.keySet()) {
			SitePlan plan = siteNeeds.get(i);
			Site site = (Site) siteInfos.get(i).get("site");
			if (plan.getLoadType() == State.LOAD) {
				int count = plan.getLoadCount();
				if (count == 0) {
					continue;
				}
				GaodePath gaodePath = sUtil.getPath(dispatcher, site, client);
				if (gaodePath == null) {
					continue;
				}
				int dist = gaodePath.getDistance();
				loadCountList.add(count);
				loadDistList.add(dist);
				loadIDList.add(i);
			} else if (plan.getLoadType() == State.UNLOAD) {
				int count = plan.getLoadCount();
				if (count == 0) {
					continue;
				}
				GaodePath gaodePath = sUtil.getPath(dispatcher, site, client);
				if (gaodePath == null) {
					continue;
				}
				int dist = gaodePath.getDistance();
				unloadCountList.add(count);
				unloadDistList.add(dist);
				unloadIDList.add(i);
			}
		}
		System.out.println("***********************");
		if (loadCountList.size() > 0) {
			double[] loadIndexScore = MathUtil.calcuMaxSco(loadCountList, loadDistList, true, false,
					State.SITE_CHOOSE_RATIO, State.SITE_CHOOSE_BEST);
			if (unloadCountList.size() > 0) {
				double[] unloadIndexScore = MathUtil.calcuMaxSco(unloadCountList, unloadDistList, true, false,
						State.SITE_CHOOSE_RATIO, State.SITE_CHOOSE_BEST);
				System.out.println(Arrays.toString(loadIndexScore));
				System.out.println(Arrays.toString(unloadIndexScore));
				if (loadIndexScore[1] >= 0 && unloadIndexScore[1] >= 0) {

					double loadScore = loadIndexScore[1] * loadRatio;
					double unloadScore = unloadIndexScore[1] * (1 - loadRatio);
					if (loadScore > unloadScore) {
						return loadIDList.get((int) loadIndexScore[0]);
					} else {
						return unloadIDList.get((int) unloadIndexScore[0]);
					}
				}
			} else {
				return loadIDList.get((int) loadIndexScore[0]);
			}
		} else if (unloadCountList.size() > 0) {
			double[] unloadIndexScore = MathUtil.calcuMaxSco(unloadCountList, unloadDistList, true, false,
					State.SITE_CHOOSE_RATIO, State.SITE_CHOOSE_BEST);
			return unloadIDList.get((int) unloadIndexScore[0]);
		} else {
			return -1;
		}
		return -1;
	}

	public int scoreSitesBySite(Map<Integer, SitePlan> siteNeeds, Site fromSite, double loadRatio) {
		List<Integer> loadCountList = new ArrayList<>();
		List<Integer> loadDistList = new ArrayList<>();
		List<Integer> loadIDList = new ArrayList<>();

		List<Integer> unloadCountList = new ArrayList<>();
		List<Integer> unloadDistList = new ArrayList<>();
		List<Integer> unloadIDList = new ArrayList<>();

		for (Integer i : siteNeeds.keySet()) {
			SitePlan plan = siteNeeds.get(i);
			Site site = (Site) siteInfos.get(i).get("site");

			if (plan.getLoadCount() <= 0) {
				continue;
			}
			if (plan.getLoadType() == State.LOAD) {
				int count = plan.getLoadCount();
				Route route = routeServ.getRoute(fromSite.getId(), site.getId());
				GaodePath gaodePath = routeServ.turnRoute2Path(route);
				int dist = gaodePath.getDistance();
				loadCountList.add(count);
				loadDistList.add(dist);
				loadIDList.add(i);
			} else if (plan.getLoadType() == State.UNLOAD) {
				int count = plan.getLoadCount();
				Route route = routeServ.getRoute(fromSite.getId(), site.getId());
				GaodePath gaodePath = routeServ.turnRoute2Path(route);
				int dist = gaodePath.getDistance();
				unloadCountList.add(count);
				unloadDistList.add(dist);
				unloadIDList.add(i);
			}
		}
		if (loadCountList.size() == 0 && unloadCountList.size() != 0) {
			double[] unloadIndexScore = MathUtil.calcuxScore(unloadCountList, unloadDistList, true, false,
					State.SITE_CHOOSE_RATIO);
			return unloadIDList.get((int) unloadIndexScore[0]);
		} else if (unloadCountList.size() == 0 && loadCountList.size() != 0) {
			double[] loadIndexScore = MathUtil.calcuxScore(loadCountList, loadDistList, true, false,
					State.SITE_CHOOSE_RATIO);
			return loadIDList.get((int) loadIndexScore[0]);
		} else if (unloadCountList.size() != 0 && loadCountList.size() != 0) {
			double[] loadIndexScore = MathUtil.calcuxScore(loadCountList, loadDistList, true, false,
					State.SITE_CHOOSE_RATIO);
			double[] unloadIndexScore = MathUtil.calcuxScore(unloadCountList, unloadDistList, true, false,
					State.SITE_CHOOSE_RATIO);

			if (loadIndexScore[1] >= 0 && unloadIndexScore[1] >= 0) {
				double loadScore = loadIndexScore[1] * loadRatio;
				double unloadScore = unloadIndexScore[1] * (1 - loadRatio);
				if (loadScore > unloadScore) {
					return loadIDList.get((int) loadIndexScore[0]);
				} else {
					return unloadIDList.get((int) unloadIndexScore[0]);
				}
			}
		}
		return -1;
	}

	private LoadTask arrangeLoadTask(Site site, Dispatcher dispatcher, SitePlan needPlan) {
		LoadTask loadTask = new LoadTask();
		loadTask.setTargetSite(site);

		int loadCount = needPlan.getLoadCount();
		loadTask.setLoadType(needPlan.getLoadType());
		loadTask.setLoadNum(loadCount);
		loadTask.setDispatcher(dispatcher);

		int loadTime = loadCount * State.LOAD_UNIT_TIME;

		loadTask.setTaskType(State.LOAD_TASK);
		loadTask.setLoadTime(loadTime);

		client = ConnectManager.getClient();
		SiteUtil siteUtil = new SiteUtil();

		GaodePath path = siteUtil.getPath(dispatcher, site, client);

		loadTask.setPath(path);
		List<Lnglat> paths = path.getPaths();
		loadTask.setStart(paths.get(0));
		loadTask.setEnd(new Lnglat(site.getLng(), site.getLat()));
		int distace = path.getDistance();
		int moveTime = calcuTimeSpan(distace, dispatcher.getType());
		loadTask.setMoveTime(moveTime);

		loadTask.setWorkTime(moveTime + loadTime);
		return loadTask;
	}

	private LoadTask arrangeLoadTaskBySite(Site site, Site fromSite, Dispatcher dispatcher, SitePlan needPlan) {
		LoadTask loadTask = new LoadTask();
		loadTask.setTargetSite(site);

		int loadCount = needPlan.getLoadCount();
		loadTask.setLoadType(needPlan.getLoadType());
		loadTask.setLoadNum(loadCount);
		loadTask.setDispatcher(dispatcher);

		int loadTime = loadCount * State.LOAD_UNIT_TIME;

		loadTask.setTaskType(State.LOAD_TASK);
		loadTask.setLoadTime(loadTime);

		Route route = routeServ.getRoute(fromSite.getId(), site.getId());
		GaodePath path = routeServ.turnRoute2Path(route);

		loadTask.setPath(path);
		List<Lnglat> paths = path.getPaths();
		loadTask.setStart(paths.get(0));
		loadTask.setEnd(new Lnglat(site.getLng(), site.getLat()));
		int distace = path.getDistance();
		int moveTime = calcuTimeSpan(distace, dispatcher.getType());
		loadTask.setMoveTime(moveTime);
		System.out.println("load " + loadTime);
		System.out.println("move " + moveTime);
		loadTask.setWorkTime(loadTime + moveTime);
		System.out.println("work " + loadTask.getWorkTime());

		return loadTask;
	}

	/**
	 * 每当一个装卸货事件完成，模拟中每个被模拟改动过的站点的单车数都和 原来的不同，所以，要对这些站点进行数量更新。
	 * 
	 * @param nowHour
	 * @param nowSeconds
	 * @param loadTask
	 */
	public void refreshSites(int nowHour, int pastNowSeconds, LoadTask loadTask) {

		int type = loadTask.getLoadType();
		Site loadSite = loadTask.getTargetSite();
		int siteID = 0;
		// 对所有的站点都进行更新
		for (Site site : sites) {
			Map<String, Object> siteInfo = siteInfos.get(site.getId());
//			// bikes表示没有调度，原始的变化量，用于计算变化量
//			List<Integer> bikes = (List<Integer>) siteInfo.get("bikes");
//
//			// 计算如果没有调度，这一时间点的单车数量
//			int nowCount = bikes.get(nowHour);
//			int nextCount = bikes.get(nowHour + 1);
//			int nowGuess = guessNowSiteBikes(nowCount, nextCount, pastNowSeconds);
			List<Integer> change = (List<Integer>) siteInfo.get("change");

			// 如果某一站是本次调度任务的目标站
			if (siteID == loadSite.getId()) {
				int count = 0;
				// 再加上此刻调度的结果
				if (type == State.LOAD) {
					count = -loadTask.getLoadNum();
				} else {
					count = loadTask.getLoadNum();
				}
				change.add(count);
			}
//
//			for (int i = 0; i < change.size(); i++) {
//				nowGuess += change.get(i);
//			}
//			System.out.println(nowGuess);
		}

	}

	public int calcuTimeSpan(int distance, int type) {
		int seconds = 0;
		double secondSpeed = 0;
		if (type == State.TRUCK_TYPE) {
			secondSpeed = State.TRUCK_SPEED / 3.6;
			seconds = (int) (distance / secondSpeed);
		} else if (type == State.TRICYCLE_TYPE) {
			secondSpeed = State.TRICYCLE_SPEED / 3.6;
			seconds = (int) (distance / secondSpeed);
		} else if (type == State.MAN_TYPE) {
			secondSpeed = State.MAN_TYPE / 3.6;
			seconds = (int) (distance / secondSpeed);
		}
		seconds = (int) (seconds * State.WASTETIME_MOVE_RATIO) + State.WASTETIME_LOAD;
		return seconds;
	}

	private Site findMostNearSite(Map<Integer, Map<String, Object>> siteInfos, TreeMap<Double, Integer> inMap,
			Site site, Double minDouble) {
		SiteUtil sUtil = new SiteUtil();
		int minDist = 0;
		Site minSite = null;

		for (Double val : inMap.keySet()) {
			Integer id = inMap.get(val);
			Site inSite = (Site) siteInfos.get(id).get("site");
			GaodePath path = sUtil.getPath(inSite, site, client);
			if (minDist == 0) {
				minDist = path.getDistance();
				minSite = inSite;
				minDouble = val;
			} else {
				if (minDist < path.getDistance()) {
					minDist = path.getDistance();
					minSite = inSite;
					minDouble = val;
				}
			}
		}
		return minSite;
	}

	/**
	 * 根据实际变化的数据和模拟调度的数据叠加起来，形成这一时刻的数据
	 * 
	 * @param bikes
	 * @param nowHour
	 * @param siteInfo
	 * @param pastSeconds
	 * @return
	 */
	public int guessNowBikes(List<Integer> bikes, int nowHour, Map<String, Object> siteInfo, int pastSeconds) {
		// 计算如果没有调度，这一时间点的单车数量
		int nowCount = bikes.get(nowHour);
		int nextCount = bikes.get(nowHour + 1);
		int nowGuess = guessNowSiteBikes(nowCount, nextCount, pastSeconds);
		List<Integer> change = (List<Integer>) siteInfo.get("change");
		// 将调度所产生的变化叠加上去，这样的结果表示调度的单车完全没有利用
		for (int i = 0; i < change.size(); i++) {
			nowGuess += change.get(i);
		}
		return nowGuess;
	}

	/**
	 * 通过预测下次的站点数量来分析所有站 分析包括：基于fake单车数据（基于模拟的数据）进行下一时段的预测
	 * 
	 * 在给定的站点中，选择出
	 * 
	 * @param sites
	 * @param ests
	 * @param siteInfos
	 * @param nowHour
	 */
	public Map<Integer, SitePlan> collectSites(List<Site> sites, Map<Integer, Map<String, Object>> ests,
			Map<Integer, Map<String, Object>> siteInfos, int nowHour, int pastSeconds, Dispatcher dispatcher) {

		Map<Integer, SitePlan> siteScores = new HashMap<>();

		// 调度目前装的数量
		int storage = dispatcher.getStorage();
		// 调度车可以装的数量
		int loadLimit = 0;
		if (dispatcher.getType() == State.TRUCK_TYPE) {
			loadLimit = State.TRUCK_CAPACITY;
		} else {
			loadLimit = State.TRICYCLE_CAPACITY;
		}
		int canLoad = loadLimit - storage;
		int dispID = dispatcher.getId();
		Set<Integer> visits = null;
		if (visitedSites.containsKey(dispID)) {
			visits = visitedSites.get(dispID);

		} else {
			visitedSites.get(dispID);
			visits = new HashSet<>();
			visitedSites.put(dispID, visits);
		}

		for (Site site : sites) {
			// 每个调度车只去每个站点一次
			if (visits.contains(site.getId())) {
				continue;
			}
			int volume = site.getVolume();
			Map<String, Object> siteInfo = siteInfos.get(site.getId());
			// bikes表示没有调度，原始的变化量，用于计算变化量
			List<Integer> bikes = (List<Integer>) siteInfo.get("bikes");
			int nowCount = guessNowBikes(bikes, nowHour, siteInfo, pastSeconds);
			// trend(增还是减),rate(变化的大小),confidence(自信程度)
			Map<String, Object> estInfo = ests.get(site.getId());
			int trend = (int) estInfo.get("trend");
			double rate = (double) estInfo.get("rate");
			int nextEst = 0;
			if (trend == State.GROW_TREND) {
				nextEst = (int) (nowCount * (1 + rate));
			} else {
				nextEst = (int) (nowCount * (1 - rate));
			}
			// 当预测的趋势和需要的趋势相同时，考虑该站点
			SitePlan plan = analyzeSiteNeed(nowCount, nextEst, volume, trend, storage, canLoad);
			plan.setNextEst(nextEst);
			if (plan.getLoadCount() < 0) {
				System.out.println("----------------");
				System.out.println(nowCount);
				System.out.println(nextEst);
				System.out.println(volume);
				System.out.println(trend);
				System.out.println(storage);
				System.out.println(canLoad);
			}
//			System.out.println(site.getId() + " ----  " + nowCount + " ----  " + nextEst + " ----  " + trend);
			siteScores.put(site.getId(), plan);

		}
		return siteScores;
	}

	public static void main(String[] args) {
		TaskProducer taskProducer = new TaskProducer();
		int nowCount = 12;
		int nextEst = 15;
		int volume = 20;
		int trend = 1;
		int carHave = 10;
		int canLoad = 10;
		SitePlan plan = taskProducer.analyzeSiteNeed(nowCount, nextEst, volume, trend, carHave, canLoad);
		System.out.println(plan.getLoadCount());
	}

	/**
	 * 通过容量、预测、当前值来总结该站点的供需关系
	 * 
	 * @param nowCount
	 * @param nextEst
	 * @param volume
	 * @param trend
	 * @return
	 */
	public SitePlan analyzeSiteNeed(int nowCount, int nextEst, int volume, int trend, int carHave, int canLoad) {
		SitePlan plan = new SitePlan();
		// 当前站点的数量大于限制
		if (nowCount > volume) {
			// 未来会增加
			if (trend == State.GROW_TREND) {
				plan.setLoadType(State.LOAD);
				plan.setLoadCount((nextEst - volume));
			} else {
				// 未来会减少
				// 如果减少到容量限制以下
				if (nextEst <= volume) {
					plan.setLoadType(State.IGNORE);
				} else {
					// 未来减少的量没有到容量之下
					plan.setLoadType(State.LOAD);
					plan.setLoadCount((nextEst - volume));
				}
			}
		} else {// 当前库存小于限制
			// 未来会增加
			if (trend == State.GROW_TREND) {
				if (nextEst <= volume) {
					plan.setLoadType(State.IGNORE);
				} else {
					// 未来增加的量超出了容量
					plan.setLoadType(State.LOAD);
					plan.setLoadCount((nextEst - volume));
				}
			} else {
				// 未来会减少
				plan.setLoadType(State.UNLOAD);
				plan.setLoadCount(nowCount - nextEst);
			}
		}

		int needLoadNum = plan.getLoadCount();
		int readLoadNum = 0;
		if (plan.getLoadType() == State.LOAD) {
			// 如果需要装的量大于调度车目前可以装的量,就只装能装的
			if (needLoadNum >= canLoad) {
				readLoadNum = canLoad;
			} else {
				readLoadNum = needLoadNum;
			}

		} else if (plan.getLoadType() == State.UNLOAD) {
			// 如果需要卸载的量大于调度车目前的存量,就卸掉所有的，否则，卸掉它需要的
			if (needLoadNum >= carHave) {
				readLoadNum = carHave;
			} else {
				readLoadNum = needLoadNum;
			}
		}
		plan.setLoadCount(readLoadNum);
		return plan;
	}

	public int estimate(Site site, int theHour, SitePlan sitePlan) {

		List<Integer> HourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour);
		List<Integer> nextHourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour + 1);
		Map<String, Object> sInfo = siteInfos.get(site.getId());
		List<Integer> list = (List<Integer>) sInfo.get("bikes");

//		System.out.println(HourHistory);
//		System.out.println(nextHourHistory);
//		System.out.println(list);
		int x = HourHistory.get(HourHistory.size() - 1);
		int y = nextHourHistory.get(nextHourHistory.size() - 1);

		int next = 0;
		double ratio = 0;
		// 下一时间单车会加少
		if (x > y) {
			ratio = ((double) (x - y)) / x;
			next = (int) (list.get(theHour) * ratio);
			sitePlan.setLoadType(State.UNLOAD);

		} else {
			// 下一时间单车会增加
			ratio = (double) (y) / (y - x);
			next = (int) (list.get(theHour) * ratio);
			sitePlan.setLoadType(State.LOAD);
		}
		sitePlan.setLoadCount(next);
		return next;
	}

	public void estimate(int theHour) {
		for (Site site : sites) {
			List<Integer> HourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour);
			List<Integer> nextHourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour + 1);
			Map<String, Object> sInfo = siteInfos.get(site.getId());
			List<Integer> list = (List<Integer>) sInfo.get("bikes");

			int x = HourHistory.get(HourHistory.size() - 1);
			int y = nextHourHistory.get(nextHourHistory.size() - 1);

			int next = 0;
			double ratio = 0;
			if (x > y) {
				ratio = ((double) (x - y)) / x;
				next = (int) (list.get(theHour) * ratio);
			} else {
				ratio = ((double) (y - x)) / x + 1;
				next = (int) (list.get(theHour) * ratio);
			}
			sInfo.put("est", next);
		}
	}

	public Site findSite(Lnglat fromPoint, int nowHour, int needType) {

		SiteUtil siteUtil = new SiteUtil();

		Map<Integer, int[]> siteScores = new HashMap<>();
		for (Integer siteID : siteInfos.keySet()) {
			Map<String, Object> siteInfo = siteInfos.get(siteID);

			Site site = (Site) siteInfo.get("site");

			int lineDist = CoordsUtil.calcuDist(fromPoint.getLng(), fromPoint.getLat(), site.getLng(), site.getLat());
			if (lineDist > State.NEAR_SITE_DIST) {
				continue;
			}

			GaodePath path = siteUtil.getPath(fromPoint, site, client);
			if (path == null) {
				continue;
			}

			int distance = path.getDistance();
			SitePlan plan = new SitePlan();
			int need = estimate(site, nowHour, plan);
			// 如果这个站需要我们进行卸载，即该站的单车呈下降趋势
			if (plan.getLoadType() == needType) {
				siteScores.put(siteID, new int[] { distance, need });
			}

		}
		int siteID = calcuSitesScore(siteScores);
		Map<String, Object> siteNeed = siteInfos.get(siteID);
		Site need = (Site) siteNeed.get("site");
		return need;
	}

	/**
	 * 根据各个站点距离和需求度，求出综合评分最高的站点id
	 * 
	 * @param scores
	 * @return
	 */
	private int calcuSitesScore(Map<Integer, int[]> scores) {
		double maxScore = 0;
		int resultID = 0;
		int maxDist = 0;
		int maxNeed = 0;
		for (Integer i : scores.keySet()) {
			int[] params = scores.get(i);
			if (params[0] > maxDist) {
				maxDist = params[0];
			}
			if (params[1] > maxNeed) {
				maxNeed = params[1];
			}
		}
		for (Integer i : scores.keySet()) {
			int[] params = scores.get(i);
			double distSco = ((double) (maxDist - params[0])) / (maxDist);
			double needSco = ((double) (maxNeed - params[1])) / (maxNeed);
			double itemSco = distSco * 0.5 + needSco * 0.5;
			if (itemSco > maxScore) {
				maxScore = itemSco;
				resultID = i;
			}
		}

		return resultID;
	}

	private WaitTask createWaitTask(Dispatcher dispatcher) {
		WaitTask task = new WaitTask();
		task.setDispatcher(dispatcher);
		task.setTaskType(State.WAIT_TASK);
		task.setWorkTime(State.WAIT_TIME);
		return task;
	}

	public SimuTask assignTask(int nowHour, int pastHourSeconds, Dispatcher dispatcher, SimuTask lastTask,
			int distRange) {

		List<Site> nearSites = null;

		if (distRange > 3) {
			return createWaitTask(dispatcher);
		}
		// 通过距离要素过滤一部分站点
		nearSites = findNearSitesByPos(dispatcher.getLng(), dispatcher.getLat(), distRange);

//		nearSites = filterSites(nearSites, State.MOUNTAIN);

		// 根据调度车目前的装载情况得到装车的比例
		double loadRatio = decideTrend(dispatcher);

		// 根据一些限制条件，来获取参与评分的站点
		Map<Integer, SitePlan> siteNeeds = collectSites(nearSites, ests, siteInfos, nowHour, pastHourSeconds,
				dispatcher);
		// 根据计算的条件综合对站点进行评分，选取最高分的站点id
		int siteID = -1;
		if (lastTask == null) {
			siteID = scoreSitesByDispatcher(siteNeeds, dispatcher, loadRatio);
		} else if (lastTask.getTaskType() == State.LOAD_TASK) {
			LoadTask lastLoad = (LoadTask) lastTask;
			Site fromSite = lastLoad.getTargetSite();
			siteID = scoreSitesBySite(siteNeeds, fromSite, loadRatio);
		}
		if (siteNeeds.containsKey(siteID)) {
			Set<Integer> visits = visitedSites.get(dispatcher.getId());

			if (!visits.contains(siteID)) {
				visits.add(siteID);
			}
			// 得到要去的那个站点，然后安排任务
			SitePlan needPlan = siteNeeds.get(siteID);
			Site site = (Site) siteInfos.get(siteID).get("site");
			LoadTask task = null;
			if (lastTask == null) {
				task = arrangeLoadTask(site, dispatcher, needPlan);
			} else if (lastTask.getTaskType() == State.LOAD_TASK) {
				LoadTask lastLoad = (LoadTask) lastTask;
				task = arrangeLoadTaskBySite(site, lastLoad.getTargetSite(), dispatcher, needPlan);
			}
			return task;
		} else {
			return assignTask(nowHour, pastHourSeconds, dispatcher, lastTask, distRange + 1);
		}

	}

	private double decideTrend(Dispatcher dispatcher) {
		int storage = dispatcher.getStorage();
		// 调度车可以装的数量
		int loadLimit = 0;
		if (dispatcher.getType() == State.TRUCK_TYPE) {
			loadLimit = State.TRUCK_CAPACITY;
		} else if (dispatcher.getType() == State.TRICYCLE_TYPE) {
			loadLimit = State.TRICYCLE_CAPACITY;
		}
		double loadRatio = 1 - (double) storage / loadLimit;
		return loadRatio;
	}

	/**
	 * 根据调度者类型，计算还能拉多少货
	 * 
	 * @param dispatcher
	 * @param nowHold
	 * @return
	 */
	private int calcuCanLoad(Dispatcher dispatcher, int nowHold) {
		int result = 0;
		if (dispatcher.getType() == State.TRUCK_TYPE) {
			result = State.TRUCK_CAPACITY - nowHold;
		} else if (dispatcher.getType() == State.TRICYCLE_TYPE) {
			result = State.TRICYCLE_CAPACITY - nowHold;
		} else {
			result = State.MAN_CAPACITY - nowHold;
		}
		return result;
	}

	/**
	 * 从所有站点中找到距离某一站点一定距离要求下的站点
	 * 
	 * @param from
	 * @param nowHour
	 * @param needType
	 * @return
	 */
	private List<Site> findNearSitesBySite(Site from) {
		List<Site> nearSites = new ArrayList<>();
		for (Site site : sites) {
			if (site.getId() == from.getId()) {
				continue;
			}
			Route route = routeServ.getRoute(from.getId(), site.getId());
			if (route == null) {
				continue;
			}
			List<Lnglat> path = siteHelper.readRoute(route);
			if (path == null) {
				continue;
			}
			int distance = route.getDistance();
			if (distance > State.NEAR_SITE_DIST) {
				continue;
			}
			nearSites.add(site);

		}
		return nearSites;
	}

	/**
	 * 找到离某一坐标点直线距离最短的站点
	 * 
	 * @param lng
	 * @param lat
	 * @return
	 */
	private Site findDirectNearSites(double lng, double lat) {
		int directDist = 0;
		int dist = 0;
		Site temp = null;
		Site nearSite = null;
		for (int i = 0; i < sites.size(); i++) {
			temp = sites.get(i);
			if (i == 0) {
				directDist = CoordsUtil.calcuDist(lng, lat, temp.getLng(), temp.getLat());
				nearSite = temp;
			} else {
				dist = CoordsUtil.calcuDist(lng, lat, temp.getLng(), temp.getLat());
				if (dist < directDist) {
					directDist = dist;
					nearSite = temp;
				}
			}
		}
		return nearSite;

	}

	/**
	 * 根据调度车找到距离该车最近的一个站点
	 * 
	 * @param dispatcher
	 * @return
	 */
	private Site findSiteByDispatcher(Dispatcher dispatcher) {
		double lng = dispatcher.getLng();
		double lat = dispatcher.getLat();
		return findDirectNearSites(lng, lat);

	}

	private List<Site> filterSites(List<Site> sites, int mountType) {
		List<Site> result = new ArrayList<>();
		for (Site site : sites) {
			Map<String, Object> siteInfo = siteInfos.get(site.getId());
			// double[] changes =(double[]) siteInfo.get("changes");
			int type = (int) siteInfo.get("type");
			if (type == mountType) {
				result.add(site);
			}
		}

		return result;
	}

	/**
	 * 通过坐标找到附近的点
	 * 
	 * @param lng
	 * @param lat
	 * @return
	 */
	private List<Site> findNearSitesByPos(double lng, double lat, int distRange) {
		Site nearMe = findDirectNearSites(lng, lat);
		List<Site> nearSites = new ArrayList<>();
		for (Site site : sites) {
			Route route = routeServ.getRoute(nearMe.getId(), site.getId());
			if (route == null) {
				continue;
			}
			List<Lnglat> path = siteHelper.readRoute(route);
			if (path == null) {
				continue;
			}
			int distance = route.getDistance();
			if (distance > State.NEAR_SITE_DIST * distRange) {
				continue;
			}
			nearSites.add(site);

		}
		return nearSites;
	}

	private List<Site> findNearSites(SimuTask task) {
		List<Site> nearSites = null;
		if (task.getTaskType() == State.LOAD_TASK) {
			LoadTask loadTask = (LoadTask) task;
			Site site = loadTask.getTargetSite();
			nearSites = findNearSitesBySite(site);
		}
		return nearSites;
	}

	/**
	 * 将按小时的参数更新一个小时
	 * 
	 * @param theHour
	 */
	public void changeNextHour(int theHour) {
		ests = estimate.estimateAllSites(theHour);
	}
}
