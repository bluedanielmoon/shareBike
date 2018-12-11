package com.simu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.impl.client.CloseableHttpClient;

import com.execute.SiteAnalyze;
<<<<<<< HEAD
import com.execute.SiteEstimater;
import com.helper.SiteHelper;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.init.State;
import com.poi.ConnectManager;
import com.pojo.BikeArea;
import com.pojo.BikePos;
import com.pojo.Dispatcher;
import com.pojo.GaodePath;
import com.pojo.Lnglat;
<<<<<<< HEAD
import com.pojo.LoadPlan;
import com.pojo.LoadTask;
import com.pojo.MoveTask;
import com.pojo.Route;
import com.pojo.SimuTask;
import com.pojo.Site;
import com.service.DispatcherServ;
import com.service.RouteServ;
=======
import com.pojo.LoadTask;
import com.pojo.MoveTask;
import com.pojo.SimuTask;
import com.pojo.Site;
import com.service.DispatcherServ;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.service.SiteServ;
import com.util.ApplicationContextProvider;
import com.util.CoordsUtil;
import com.util.FilesUtil;
<<<<<<< HEAD
import com.util.MathUtil;
import com.util.PathUtil;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.util.SiteUtil;

public class TaskProducer {

	private List<Map<String, Object>> dayFiles;
	private List<Site> sites;
	private SiteServ siteServ;
	private DispatcherServ dispatcherServ;
<<<<<<< HEAD
	private RouteServ routeServ;
	private SiteAnalyze analyze;
	private CloseableHttpClient client;
	private Set<Integer> movedSites;
	private SiteEstimater estimate;
	private SiteHelper siteHelper;
	
	private int NEAR_SITE_DIST=3000;
=======
	private SiteAnalyze analyze;
	private CloseableHttpClient client;
	private Set<Integer> movedSites;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

	// site(site),area(BikeArea),bikes(List),est(int),fake(List)
	private Map<Integer, Map<String, Object>> siteInfos;

	public TaskProducer() {
		dayFiles = new ArrayList<>();
		siteServ = ApplicationContextProvider.getBean(SiteServ.class);
		dispatcherServ = ApplicationContextProvider.getBean(DispatcherServ.class);
<<<<<<< HEAD
		routeServ=ApplicationContextProvider.getBean(RouteServ.class);
		analyze = new SiteAnalyze();
		movedSites = new HashSet<>();
		estimate = ApplicationContextProvider.getBean(SiteEstimater.class);
		siteHelper=ApplicationContextProvider.getBean(SiteHelper.class);
	}
	
	public List<Site> findSitesWithinDist(Site site,int dist){
		List<Site> list=new ArrayList<>();
		for(Site toSite:sites) {
			Route rout=routeServ.getRoute(site.getId(), toSite.getId());
			if(rout!=null) {
				if(rout.getDistance()<dist) {
					list.add(toSite);
				}
			}
		}
		return list;
		
	}

	public LoadTask assignLoadTask(int nowHour, int nowSeconds, Dispatcher dispatcher, MoveTask moveTask) {
		LoadTask loadTask = new LoadTask();
		loadTask.setSite(moveTask.getTarget());
		Site site = moveTask.getTarget();
		LoadPlan lPlan = new LoadPlan();

		// 根据现在的时间，站点，该站点的调度车,分析出该车辆在该站点该如何装卸
		analyzeLoad(nowHour, nowSeconds, site, dispatcher, lPlan);

		int loadCount=lPlan.getLoadCount();
		loadTask.setType(lPlan.getLoadType());
		loadTask.setLoadNum(loadCount);
		loadTask.setDispatcher(dispatcher);
		
		int workTime=getLoadTime(loadCount);
		loadTask.setWorkTime(workTime);
		loadTask.setTaskType(State.LOAD_TASK);
		movedSites.add(moveTask.getTarget().getId());

		return loadTask;
	}
	
	private int getLoadTime(int loadCount) {
		return loadCount*State.LOAD_UNIT_TIME;
	}

	public void analyzeLoad(int nowHour, int nowSeconds, Site site, Dispatcher dispatcher, LoadPlan lPlan) {
		Map<String, Object> siteInfo = siteInfos.get(site.getId());
		List<Integer> ls = (List<Integer>) siteInfo.get("bikes");
		int nowCount = ls.get(nowHour);
		int nextCount = ls.get(nowHour + 1);

		int nowGuess = guessNowSiteBikes(nowCount, nextCount, nowSeconds);
		LoadPlan plan = new LoadPlan();
		int nextEst = estimate(site, nowHour, plan);
		int volume = site.getVolume();
		int storage = dispatcher.getStorage();
		int loadLimit = 0;

		boolean moveAway = true;
		int result = 0;
		if (dispatcher.getType() == State.TRUCK_TYPE) {
			loadLimit = State.TRUCK_CAPACITY;
		} else {
			loadLimit = State.TRICYCLE_CAPACITY;
		}
		int canLoad = loadLimit - storage;

		// 目前的站点数量>站点允许数量,要进行转移
		if (nowGuess > volume) {
			moveAway = true;
			// 超出站点承受力的数量
			int overSize = nowGuess - volume;
			// 超出承受力>可以装载量，就把卡车装满
			if (overSize > canLoad) {
				result = canLoad;
			} else {
				// 否则，只把多出承载能力的数量搬走
				result = overSize;
			}
		} else {
			// 如果当前数量少于站点允许数
			// 如果当前数量少于站点的最小单车数，就不用搬了
			if (nowGuess < State.MIN_SITE_BIKE_COUNT) {
				result = 0;

				// 如果预测未来单车数增长
			} else if (nextEst > nowGuess) {
				moveAway = true;
				if (nextEst > volume) {
					result = nextEst - volume;
				} else {
					result = 0;
				}
				// 如果预测未来单车数减少
			} else {
				moveAway = false;
				// 如果调度车有单车
				if (storage > 0) {
					int need = nowGuess - nextEst;
					// 如果库存比需要的大，就给该站点补充需要的
					if (storage > need) {
						result = need;
					} else {
						// 否则，就全部卸载库存
						result = storage;
					}
				} else {
					result = 0;
				}
			}
		}
		if (moveAway) {
			lPlan.setLoadType(State.LOAD);
		} else {
			lPlan.setLoadType(State.UNLOAD);
		}
		lPlan.setLoadCount(result);

//		System.out.println("nowHour" + nowHour);
//		System.out.println("nowCount" + nowCount);
//		System.out.println("nextCount" + nextCount);
//		System.out.println("nowGuess" + nowGuess);
//		System.out.println("nextEst" + nextEst);
	}

	public int guessNowSiteBikes(int now, int next, int seconds) {
		int left = 0;
		while (seconds >= 3600) {
			seconds -= 3600;
		}
		left = seconds;
		double ratio = ((double) left) / 3600;
		int result = (int) (ratio * (next - now) + now);
		return result;
	}

	public void init(Date time) {
		dayFiles = FilesUtil.ListFilesInDay(time);
		sites = siteServ.getAllSites();
=======
		analyze = new SiteAnalyze();
		movedSites= new HashSet<>();
	}

	public LoadTask assignLoadTask(int nowHour,int nowSeconds,Dispatcher dispatcher, MoveTask moveTask) {
		LoadTask loadTask = new LoadTask();
		loadTask.setSite(moveTask.getTarget());
		Site site=moveTask.getTarget();
		LoadPlan lPlan=new LoadPlan();
		
		//根据现在的时间，站点，该站点的调度车,分析出该车辆在该站点该如何装卸
		analyzeLoad(nowHour,nowSeconds,site,dispatcher,lPlan);
		
		loadTask.setType(lPlan.moveType);
		loadTask.setLoadNum(lPlan.moveCount);
		movedSites.add(moveTask.getTarget().getId());
		
		return loadTask;
	}
	

	
	public void analyzeLoad(int nowHour,int nowSeconds,Site site,Dispatcher dispatcher,LoadPlan lPlan) {
		Map<String, Object> siteInfo=siteInfos.get(site.getId());
		List<Integer> ls= (List<Integer>) siteInfo.get("bikes");
		int nowCount=ls.get(nowHour);
		int nextCount=ls.get(nowHour+1);
		
		
		int nowGuess=guessNowSiteBikes(nowCount,nextCount,nowSeconds);
		LoadPlan plan=new LoadPlan();
		int nextEst=estimate(site,nowHour,plan);
		int volume=site.getVolume();
		int storage=dispatcher.getStorage();
		int loadLimit=0;
		
		boolean moveAway=true;
		int result=0;
		if(dispatcher.getType()==State.TRUCK_TYPE) {
			loadLimit=State.TRUCK_CAPACITY;
		}else {
			loadLimit=State.TRICYCLE_CAPACITY;
		}
		int canLoad=loadLimit-storage;
		
		//目前的站点数量>站点允许数量,要进行转移
		if(nowGuess>volume) {
			moveAway=true;
			//超出站点承受力的数量
			int overSize=nowGuess-volume;
			//超出承受力>可以装载量，就把卡车装满
			if(overSize>canLoad) {
				result=canLoad;
			}else {
				//否则，只把多出承载能力的数量搬走
				result=overSize;
			}
		}else {
			//如果当前数量少于站点允许数
			//如果当前数量少于站点的最小单车数，就不用搬了
			if(nowGuess<State.MIN_SITE_BIKE_COUNT) {
				result=0;
				
				//如果预测未来单车数增长
			}else if(nextEst>nowGuess) {
				moveAway=true;
				if(nextEst>volume) {
					result=nextEst-volume;
				}else {
					result=0;
				}
				//如果预测未来单车数减少
			}else{
				moveAway=false;
				// 如果调度车有单车
				if(storage>0) {
					int need=nowGuess-nextEst;
					//如果库存比需要的大，就给该站点补充需要的
					if(storage>need) {
						result=need;
					}else {
						//否则，就全部卸载库存
						result=storage;
					}
				}else {
					result=0;
				}
			}
		}
		if(moveAway) {
			lPlan.setMoveType(State.LOAD);
		}else {
			lPlan.setMoveType(State.UNLOAD);
		}
		lPlan.setMoveCount(result);
		
		System.out.println("nowHour"+nowHour);
		System.out.println("nowCount"+nowCount);
		System.out.println("nextCount"+nextCount);
		System.out.println("nowGuess"+nowGuess);
		System.out.println("nextEst"+nextEst);
	}
	class LoadPlan{
		private int moveType;
		private int moveCount;
		public int getMoveType() {
			return moveType;
		}
		public void setMoveType(int moveType) {
			this.moveType = moveType;
		}
		public int getMoveCount() {
			return moveCount;
		}
		public void setMoveCount(int moveCount) {
			this.moveCount = moveCount;
		}
		
	}
	
	public int guessNowSiteBikes(int now,int next,int seconds) {
		int left=0;
		while(seconds>=3600) {
			seconds-=3600;
		}
		left=seconds;
		double ratio=((double)left)/3600;
		int result=(int) (ratio*(next-now)+now);
		return result;
	}

	public void initJobs(Date time) {
		dayFiles = FilesUtil.ListFilesInDay(time);
		sites= siteServ.getAllSites();
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		// 初始化siteInfos
		siteInfos = initSiteInfos();
	}

<<<<<<< HEAD
	public List<SimuTask> produceStartJobs(int nowHour,List<Dispatcher> dispatchers) {
		// 预测
		Map<Integer, Map<String, Object>> ests = estimate.estimateAllSites(nowHour);
		
		//根据预测数分析站点需求情况　
		Map<Integer, LoadPlan> siteNeeds = anayLizeSiteNeed(sites, ests, siteInfos, nowHour);
	
		// 根据评分，求出离各个站点最近的调度车并给出他们向该站点靠拢的命令
		List<SimuTask> startJobs = initMoveJobs(siteNeeds,dispatchers);
		return startJobs;
	}

	

	public List<SimuTask> initMoveJobs(Map<Integer, LoadPlan> siteNeeds,List<Dispatcher> dispatchers) {
		client = ConnectManager.getClient();
		List<SimuTask> tasks = new ArrayList<>();
		SiteUtil siteUtil = new SiteUtil();

		for (int i = 0; i < dispatchers.size(); i++) {
			Dispatcher disp = dispatchers.get(i);
			int siteID = findNeedSite(siteNeeds, disp, State.LOAD);
			Site site = (Site) siteInfos.get(siteID).get("site");
			GaodePath path = siteUtil.getPath(disp, site, client);
			MoveTask task = new MoveTask();
			task.setDispatcher(disp);
			task.setPath(path);
			task.setTarget(site);

			List<Lnglat> paths = path.getPaths();
			task.setStart(paths.get(0));
			task.setEnd(paths.get(paths.size() - 1));
			int distace = task.getPath().getDistance();
			int seconds = calcuTimeSpan(distace, task.getDispatcher().getType());
			task.setWorkTime(seconds);
			task.setTaskType(State.MOVE_TASK);
			tasks.add(task);
		}

		return tasks;
	}
	
	public int findNeedSite(Map<Integer, LoadPlan> siteNeeds, Dispatcher dispatcher, int putOrGet) {

		double ratio = 0.1;
		SiteUtil sUtil = new SiteUtil();

		List<Integer> countList = new ArrayList<>();
		List<Integer> distList = new ArrayList<>();
		List<Integer> siteList = new ArrayList<>();

		for (Integer i : siteNeeds.keySet()) {
			LoadPlan plan = siteNeeds.get(i);
			Site site = (Site) siteInfos.get(i).get("site");
			if (plan.getLoadType() == putOrGet) {
				int count = plan.getLoadCount();

				GaodePath gaodePath = sUtil.getPath(dispatcher, site, client);
				int dist = gaodePath.getDistance();
				countList.add(count);
				distList.add(dist);
				siteList.add(i);
			}
		}
		int SiteIndex = MathUtil.calcuMaxScore(countList, distList, true, false, ratio);
		return siteList.get(SiteIndex);
	}

	/**
	 * 每当一个装卸货事件完成，模拟中每个被模拟改动过的站点的单车数都和 原来的不同，所以，要对这些站点进行数量更新。
	 * 
=======
	public List<SimuTask> produceStartJobs(int nowHour) {
		// 预测下一时段的单车数量,并放入siteInfos
		estimate(nowHour);
		// 根据预测的值，求出每个站点需要搬出或者搬入的评分
		TreeMap<Double, Integer> outMap = new TreeMap<>();
		TreeMap<Double, Integer> inMap = new TreeMap<>();
		anayLizeMove(sites, siteInfos, outMap, inMap,nowHour);

		// 根据评分，求出离各个站点最近的调度车并给出他们向该站点靠拢的命令
		List<SimuTask> startJobs = initJobs(sites, siteInfos, outMap, inMap);
		return startJobs;
	}


	/**
	 * 
	 * @param sites
	 * @param siteInfos
	 * @param outMap
	 * @param inMap
	 */
	public List<SimuTask> initJobs(List<Site> sites, Map<Integer, Map<String, Object>> siteInfos,
			TreeMap<Double, Integer> outMap, TreeMap<Double, Integer> inMap) {

		client = ConnectManager.getClient();
		List<Dispatcher> dispatchers = dispatcherServ.getAllDispatchers();
		initDispatchers(dispatchers);
		List<SimuTask> tasks = new ArrayList<>();
		while (dispatchers.size() > 0) {
			Entry<Double, Integer> big = outMap.lastEntry();
			Map<String, Object> info = siteInfos.get(big.getValue());
			Site needOut = (Site) info.get("site");
			MoveTask task = decideInitMoveTasks(needOut, dispatchers);
			outMap.remove(big.getKey());
			tasks.add(task);

		}
		return tasks;
	}

	public MoveTask decideInitMoveTasks(Site site, List<Dispatcher> dispatchers) {

		GaodePath path = new GaodePath();
		Dispatcher dispatcher = findMostNearDispatcher(dispatchers, site, path, State.TRUCK_TYPE, State.TRICYCLE_TYPE);
		dispatchers.remove(dispatcher);

		MoveTask task = new MoveTask();
		task.setDispatcher(dispatcher);
		task.setPath(path);
		task.setTarget(site);

		List<Lnglat> paths = path.getPaths();
		task.setStart(paths.get(0));
		task.setEnd(paths.get(paths.size() - 1));
		int distace = task.getPath().getDistance();
		int seconds = calcuTimeSpan(distace, task.getDispatcher().getType());
		task.setWorkTime(seconds);
		task.setType(State.MOVE_TASK);
		return task;
	}
	
	/**
	 * 每当一个装卸货事件完成，模拟中每个被模拟改动过的站点的单车数都和
	 * 原来的不同，所以，要对这些站点进行数量更新。
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
	 * @param nowHour
	 * @param nowSeconds
	 * @param loadTask
	 */
<<<<<<< HEAD
	public void refreshSites(int nowHour, int nowSeconds, LoadTask loadTask) {

		Iterator<Integer> moved = movedSites.iterator();
		int type = loadTask.getType();
		Site site = loadTask.getSite();
		int siteID = 0;
		while (moved.hasNext()) {
			siteID = moved.next();
			Map<String, Object> siteInfo = siteInfos.get(siteID);
			List<Integer> fakeList = (List<Integer>) siteInfo.get("fake");
			List<Integer> bikes = (List<Integer>) siteInfo.get("bikes");
			int original = fakeList.get(nowHour);

			// 计算如果没有调度，这一时间点的单车数量
			int nowCount = bikes.get(nowHour);
			int nextCount = bikes.get(nowHour + 1);
			int nowGuess = guessNowSiteBikes(nowCount, nextCount, nowSeconds);
			int nowChange = nowGuess - nowCount;

			// 将变化量添加到有调度的列表中
			if (nowChange > 0) {
				original += nowChange;
			} else {
				original -= nowChange;
			}

			if (siteID == site.getId()) {
				// 再加上此刻调度的结果
				if (type == State.LOAD) {
					original -= loadTask.getLoadNum();
				} else {
					original += loadTask.getLoadNum();
				}
			}
			fakeList.set(nowHour, original);
		}

	}

	public  int calcuTimeSpan(int distance, int type) {
		int seconds = 0;
		double secondSpeed=0;
		if (type == State.TRUCK_TYPE) {
			secondSpeed=State.TRUCK_SPEED/3.6;
			seconds = (int) (distance / secondSpeed);
		} else if (type == State.TRICYCLE_TYPE) {
			secondSpeed=State.TRICYCLE_SPEED/3.6;
			seconds = (int) (distance / secondSpeed);
		} else if (type == State.MAN_TYPE) {
			secondSpeed=State.MAN_TYPE/3.6;
			seconds = (int) (distance / secondSpeed);
=======
	public void refreshSites(int nowHour,int nowSeconds,LoadTask loadTask) {
		
		Iterator<Integer> moved=movedSites.iterator();
		int type=loadTask.getType();
		Site site=loadTask.getSite();
		int siteID=0;
		while(moved.hasNext()) {
			siteID=moved.next();
			Map<String, Object> siteInfo=siteInfos.get(siteID);
			List<Integer> fakeList=(List<Integer>) siteInfo.get("fake");
			List<Integer> bikes=(List<Integer>) siteInfo.get("bikes");
			int original=fakeList.get(nowHour);
			
			//计算如果没有调度，这一时间点的单车数量
			int nowCount=bikes.get(nowHour);
			int nextCount=bikes.get(nowCount+1);
			int nowGuess=guessNowSiteBikes(nowCount, nextCount, nowSeconds);
			int nowChange=nowGuess-nowCount;
			
			//将变化量添加到有调度的列表中
			if(nowChange>0) {
				original+=nowChange;
			}else {
				original-=nowChange;
			}
			
			if(siteID==site.getId()) {
				//再加上此刻调度的结果
				if(type==State.LOAD) {
					original-=loadTask.getLoadNum();
				}else {
					original+=loadTask.getLoadNum();
				}				
			}
			fakeList.set(nowHour, original);
		}
		
	}

	private int calcuTimeSpan(int distance, int type) {
		int seconds = 0;
		if (type == State.TRUCK_TYPE) {
			seconds = distance / State.TRUCK_SPEED;
		} else if (type == State.TRICYCLE_TYPE) {
			seconds = distance / State.TRICYCLE_SPEED;
		} else if (type == State.MAN_TYPE) {
			seconds = distance / State.MAN_SPEED;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		}
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

	private Dispatcher findMostNearDispatcher(List<Dispatcher> dispatchers, Site site, GaodePath targetPath,
			int... types) {
		SiteUtil sUtil = new SiteUtil();
		int minDist = 0;
		Dispatcher minDispatcher = null;

		boolean needCheck = true;
		for (Dispatcher dispatcher : dispatchers) {
			for (int i = 0; i < types.length; i++) {
				if (dispatcher.getType() == types[i]) {
					needCheck = true;
					break;
				}
				if (i == types.length - 1) {
					needCheck = false;
				}
			}
			if (!needCheck) {
				continue;
			}
			if (dispatcher.getType() == 1 || dispatcher.getType() == 2) {
				GaodePath path = sUtil.getPath(dispatcher, site, client);
				if (minDist == 0 || minDist < path.getDistance()) {
					minDist = path.getDistance();
					minDispatcher = dispatcher;
					targetPath.setDistance(path.getDistance());
					targetPath.setDuration(path.getDuration());
					targetPath.setPaths(path.getPaths());
				}
			}

		}
		return minDispatcher;
	}

<<<<<<< HEAD
	/**
	 * 通过预测下次的站点数量来分析所有站
	 * 
	 * @param sites
	 * @param ests
	 * @param siteInfos
	 * @param nowHour
	 */
	public Map<Integer, LoadPlan> anayLizeSiteNeed(List<Site> sites, Map<Integer, Map<String, Object>> ests,
			Map<Integer, Map<String, Object>> siteInfos, int nowHour) {

		Map<Integer, LoadPlan> siteScores = new HashMap<>();
		for (Site site : sites) {
			int volume = site.getVolume();
			Map<String, Object> sInfo = siteInfos.get(site.getId());
			List<Integer> list = (List<Integer>) sInfo.get("bikes");
			int nowCount = list.get(nowHour);

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
			LoadPlan plan = analyzeSiteNeed(nowCount, nextEst, volume, trend);

			siteScores.put(site.getId(), plan);
		}
		return siteScores;
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
	public LoadPlan analyzeSiteNeed(int nowCount, int nextEst, int volume, int trend) {
		LoadPlan plan = new LoadPlan();
		// 当前库存大于限制
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
		} else {
			// 未来会增加
			if (trend == State.GROW_TREND) {
				if (nextEst <= volume) {
					plan.setLoadType(State.IGNORE);
				} else {
					// 未来减少的量没有到容量之下
					plan.setLoadType(State.LOAD);
					plan.setLoadCount((nextEst - volume));
				}
			} else {
				// 未来会减少
				plan.setLoadType(State.UNLOAD);
				plan.setLoadCount(nowCount - nextEst);
			}
		}
		return plan;
	}

	public int estimate(Site site, int theHour, LoadPlan loadPlan) {

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
			loadPlan.setLoadType(State.UNLOAD);

		} else {
			// 下一时间单车会增加
			ratio = (double) (y) / (y - x);
			next = (int) (list.get(theHour) * ratio);
			loadPlan.setLoadType(State.LOAD);
		}
		loadPlan.setLoadCount(next);
		return next;
	}
=======
	public void anayLizeMove(List<Site> sites, Map<Integer, Map<String, Object>> siteInfos, Map<Double, Integer> outMap,
			Map<Double, Integer> inMap,int nowHour) {

		/**
		 * 
		 * 级别越高，越要重视|级别分的越细，需要搬的就越少 出站： 级别2:当前超出容量，且接下来还会继续增加 级别1:当前超出容量，且接下来会减少
		 * 
		 * 入站： 级别2:当前少于容量，且接下来还会继续减少 级别1:当前少于容量，且接下来会不变或增加
		 */

		for (Site site : sites) {
			int volume = site.getVolume();
			Map<String, Object> sInfo = siteInfos.get(site.getId());
			List<Integer> list= (List<Integer>) sInfo.get("bikes");
			int nowCount=list.get(nowHour);
					
			int nextEst = (int) sInfo.get("est");

			String moveOrder = "";
			boolean moveOut = true;
			int moveLevel = 0;
			double moveRatio = 0;
			if (nowCount < volume) {
				if (nextEst >= nowCount) {
					moveOrder = "往进搬,级别1";
					moveOut = false;
					moveLevel = 1;
				} else {
					moveOrder = "往进搬,级别2";
					moveOut = false;
					moveRatio = ((double) (volume - nextEst)) / volume;
					inMap.put(moveRatio, site.getId());
					moveLevel = 2;
				}
			} else {
				if (nextEst > nowCount) {
					moveOrder = "往出搬,级别2";
					moveOut = true;
					moveRatio = ((double) (nextEst - volume)) / volume;
					outMap.put(moveRatio, site.getId());
					moveLevel = 2;
				} else {
					moveOrder = "往出搬,级别1";
					moveOut = true;
					moveLevel = 1;
				}
			}
			sInfo.put("movetype", moveOut);
			sInfo.put("moveRatio", moveRatio);
			sInfo.put("moveLevel", moveLevel);
		}

	}
	
	public int estimate(Site site,int theHour,LoadPlan loadPlan){
		
			List<Integer> HourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour);
			List<Integer> nextHourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour + 1);
			Map<String, Object> sInfo = siteInfos.get(site.getId());
			List<Integer> list= (List<Integer>) sInfo.get("bikes");
			
			System.out.println(HourHistory);
			System.out.println(nextHourHistory);
			System.out.println(list);
			int x = HourHistory.get(HourHistory.size() - 1);
			int y = nextHourHistory.get(nextHourHistory.size() - 1);

			int next = 0;
			double ratio = 0;
			//下一时间单车会加少
			if (x > y) {
				ratio = ((double) (x - y)) / x;
				next = (int) (list.get(theHour) * ratio);
				loadPlan.setMoveType(State.UNLOAD);
				
			} else {
				//下一时间单车会增加
				ratio = (double)(y)/ (y - x);
				next = (int) (list.get(theHour) * ratio);
				loadPlan.setMoveType(State.LOAD);
			}
			loadPlan.setMoveCount(next);
		return next;
	}
	

>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

	public void estimate(int theHour) {
		for (Site site : sites) {
			List<Integer> HourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour);
			List<Integer> nextHourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour + 1);
			Map<String, Object> sInfo = siteInfos.get(site.getId());
<<<<<<< HEAD
			List<Integer> list = (List<Integer>) sInfo.get("bikes");
=======
			List<Integer> list= (List<Integer>) sInfo.get("bikes");
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

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

	public Map<Integer, Map<String, Object>> initSiteInfos() {
		Map<Integer, Map<String, Object>> sitesInfos = new HashMap<>();
		int dist = 50;
		for (Site s : sites) {
			Lnglat lnglat = new Lnglat(s.getLng(), s.getLat());
			BikeArea area = CoordsUtil.getCenterArea(lnglat, dist);

			Map<String, Object> info = new HashMap<>();
			info.put("site", s);
			info.put("area", area);

			List<Integer> list = new ArrayList<>(24);
			for (int i = 0; i < 24; i++) {
				list.add(0);
			}
			info.put("bikes", list);
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
					}
				}
			}
			hourCount++;
		}
		for (Integer i : sitesInfos.keySet()) {
			Map<String, Object> info = sitesInfos.get(i);
			List<Integer> list = (List<Integer>) info.get("bikes");
<<<<<<< HEAD

			List<Integer> fake = new ArrayList<>();
			for (Integer item : list) {
=======
			
			List<Integer> fake=new ArrayList<>();
			for(Integer item:list) {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
				fake.add(item);
			}
			info.put("fake", fake);
		}
		return sitesInfos;
	}

<<<<<<< HEAD
	/**
	 * 当调度车在某一个站点装卸完毕后，需要找到最近一个需要单车的站点进行卸货,或是装货
	 * 
	 * @param from     出发的那个站点
	 * @param nowHour
	 * @param needType State.load--需要找单车富足的点 State.unload--需要找单车不足的点
	 * @return
	 */
	public Site findMostNeedSite(Site from, int nowHour, int needType) {
		Map<Integer, int[]> siteScores = new HashMap<>();
		for (Integer siteID : siteInfos.keySet()) {
			Map<String, Object> siteInfo = siteInfos.get(siteID);
			if(siteID==from.getId()) {
				continue;
			}

			Site site = (Site) siteInfo.get("site");

			int lineDist = CoordsUtil.calcuDist(from.getLng(), from.getLat(), site.getLng(), site.getLat());
			if (lineDist > NEAR_SITE_DIST) {
				continue;
			}

			Route route=routeServ.getRoute(from.getId(), site.getId());
			if(route==null) {
				continue;
			}
			List<Lnglat> path=siteHelper.readRoute(route);
			if (path == null) {
				continue;
			}

			int distance =route.getDistance();
			LoadPlan plan = new LoadPlan();
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

	public Site findMostNeedSite(Lnglat fromPoint, int nowHour, int needType) {

		SiteUtil siteUtil = new SiteUtil();

		Map<Integer, int[]> siteScores = new HashMap<>();
		for (Integer siteID : siteInfos.keySet()) {
			Map<String, Object> siteInfo = siteInfos.get(siteID);

			Site site = (Site) siteInfo.get("site");

			int lineDist = CoordsUtil.calcuDist(fromPoint.getLng(), fromPoint.getLat(), site.getLng(), site.getLat());
			if (lineDist > NEAR_SITE_DIST) {
				continue;
			}

			GaodePath path = siteUtil.getPath(fromPoint, site, client);
			if (path == null) {
				continue;
			}

			int distance = path.getDistance();
			LoadPlan plan = new LoadPlan();
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
=======
	public void initDispatchers(List<Dispatcher> dispatchers) {
		dispatchers.get(0).setLng(108.97194);
		dispatchers.get(0).setLat(34.270711);
		dispatchers.get(0).setStorage(0);

		dispatchers.get(1).setLng(108.922158);
		dispatchers.get(1).setLat(34.286243);
		dispatchers.get(1).setStorage(0);

		dispatchers.get(2).setLng(108.923188);
		dispatchers.get(2).setLat(34.259361);
		dispatchers.get(2).setStorage(0);

		dispatchers.get(3).setLng(108.910314);
		dispatchers.get(3).setLat(34.241483);
		dispatchers.get(3).setStorage(0);

		dispatchers.get(4).setLng(108.995629);
		dispatchers.get(4).setLat(34.268725);
		dispatchers.get(4).setStorage(0);

//		dispatchers.get(5).setLng(108.972283);
//		dispatchers.get(5).setLat(34.250848);
//		dispatchers.get(5).setStorage(0);
//		
//		dispatchers.get(6).setLng(108.947393);
//		dispatchers.get(6).setLat(34.269718);
//		dispatchers.get(6).setStorage(0);
	}
	/**
	 * 当调度车在某一个站点装卸完毕后，需要找到最近一个需要单车的站点进行卸货,或是装货
	 * @param from 出发的那个站点
	 * @param nowHour
	 * @param needType 
	 * 			State.load--需要找单车富足的点
	 * 			State.unload--需要找单车不足的点
	 * @return
	 */
	public Site findMostNeedSite(Site from,int nowHour,int needType){
		
		SiteUtil siteUtil=new SiteUtil();
		
		Map<Integer, int[]> siteScores=new HashMap<>();
		for(Integer siteID:siteInfos.keySet()) {
			Map<String, Object> siteInfo=siteInfos.get(siteID);
			
			Site site=(Site) siteInfo.get("site");
			
			int lineDist=CoordsUtil.calcuDist(from.getLng(), from.getLat(), site.getLng(), site.getLat());
			if(lineDist>2000) {
				continue;
			}
			GaodePath path=siteUtil.getPath(from, site, client);
			if(path==null) {
				continue;
			}
			
			int distance=path.getDistance();
			LoadPlan plan=new LoadPlan();
			int need=estimate(site, nowHour,plan);
			//如果这个站需要我们进行卸载，即该站的单车呈下降趋势
			if(plan.getMoveType()==needType) {
				siteScores.put(siteID, new int[] {distance,need});
			}
			
		}
		int siteID=calcuSitesScore(siteScores);
		Map<String, Object> siteNeed= siteInfos.get(siteID);
		Site need=(Site) siteNeed.get("site");
		return need;
	}
	
	/**
	 * 根据各个站点距离和需求度，求出综合评分最高的站点id
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
	 * @param scores
	 * @return
	 */
	private int calcuSitesScore(Map<Integer, int[]> scores) {
<<<<<<< HEAD
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

	public MoveTask assignMoveTask(int nowHour, Site site,Dispatcher dispatcher,int nextSiteNeedtype) {
		MoveTask task = new MoveTask();

		Site needSite = findMostNeedSite(site, nowHour, nextSiteNeedtype);

		Route route=routeServ.getRoute(site.getId(), needSite.getId());
		
		List<Lnglat> paths=siteHelper.readRoute(route);
		GaodePath path=new GaodePath();
		path.setDistance(route.getDistance());
		path.setDuration(route.getDuration());
		path.setPaths(paths);

		task.setDispatcher(dispatcher);
		task.setEnd(paths.get(paths.size() - 1));
		task.setPath(path);
		task.setStart(paths.get(0));
		task.setTaskType(State.MOVE_TASK);
=======
		double maxScore=0;
		int resultID=0;
		int maxDist=0;
		int maxNeed=0;
		for(Integer i:scores.keySet()) {
			int[] params=scores.get(i);
			if(params[0]>maxDist) {
				maxDist=params[0];
			}
			if(params[1]>maxNeed) {
				maxNeed=params[1];
			}
		}
		for(Integer i:scores.keySet()) {
			int[] params=scores.get(i);
			double distSco=((double)(maxDist-params[0]))/(maxDist);
			double needSco=((double)(maxNeed-params[1]))/(maxNeed);
			double itemSco=distSco*0.5+needSco*0.5;
			if(itemSco>maxScore) {
				maxScore=itemSco;
				resultID=i;
			}
		}
		
		return resultID;
	}

	public MoveTask assignMoveTask(int nowHour,LoadTask loadTask) {
		MoveTask task=new MoveTask();
		Site fromSite=loadTask.getSite();
	
		Site needSite=findMostNeedSite(fromSite,nowHour,State.UNLOAD);
		
		SiteUtil siteUtil=new SiteUtil();
		GaodePath path=siteUtil.getPath(fromSite, needSite, client);
		
		List<Lnglat> paths=path.getPaths();
		task.setDispatcher(loadTask.getDispatcher());
		task.setEnd(paths.get(paths.size()-1));
		task.setPath(path);
		task.setStart(paths.get(0));
		task.setType(State.MOVE_TASK);
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		int distace = task.getPath().getDistance();
		int seconds = calcuTimeSpan(distace, task.getDispatcher().getType());
		task.setWorkTime(seconds);
		task.setTarget(needSite);
		return task;
	}

}
