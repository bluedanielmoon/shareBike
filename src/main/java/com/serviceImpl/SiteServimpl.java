package com.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.SiteDao;
import com.execute.BikeTracker;
import com.execute.FlowChecker;
import com.execute.LazyAnalyze;
import com.execute.SiteAnalyze;
import com.execute.SiteChooser;
import com.execute.SiteChooser.MaxScore;
import com.execute.SiteCircum;
import com.execute.SiteStorage;
import com.execute.SiteTypeJudger;
import com.helper.SiteHelper;
import com.init.State;
import com.pojo.BikeHeader;
import com.pojo.CircumState;
import com.pojo.Lnglat;
import com.pojo.Point;
import com.pojo.PredictResult;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.CoordsUtil;
import com.util.DateUtil;
import com.util.FilesUtil;

@Service
public class SiteServimpl implements SiteServ {

	@Autowired
	private SiteDao siteDao;

	@Autowired
	private SiteChooser SiteChooser;

	@Autowired
	private SiteAnalyze analyze;

	@Autowired
	private SiteHelper Helper;

	@Autowired
	private SiteTypeJudger judger;

	@Autowired
	private FlowChecker flowChecker;
	
	@Autowired
	private LazyAnalyze lazyChecker;
	@Autowired
	private SiteStorage siteStorage;
	
	@Autowired
	private SiteCircum siteCircum;
	
	@Autowired
	private BikeTracker bikeTracker;

	public SiteServimpl(SiteDao userDao) {
		this.siteDao = userDao;
	}

	@Override
	public Site getSiteById(int id) {
		return siteDao.getSite(id);
	}

	@Override
	public List<Site> getAllSites() {
		return siteDao.getAll();
	}

	@Override
	public boolean addSite(Site site) {
		int change = siteDao.addSite(site);
		if (change == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateSite(Site site) {
		int change = siteDao.updateSite(site);
		if (change == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateSite(int id, double lng, double lat) {
		Site site = siteDao.getSite(id);
		site.setLng(lng);
		site.setLat(lat);
		return updateSite(site);
	}

	@Override
	public boolean updateSite(int id, String siteName, int siteLimit, int siteType) {
		Site site = siteDao.getSite(id);
		site.setName(siteName);
		site.setVolume(siteLimit);
		site.setType(1);
		return updateSite(site);
	}

	@Override
	public boolean deleteSite(int id) {
		int change = siteDao.deleteSite(id);
		if (change == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean patchaddSites(List<Site> sites) {
		if (sites.size() > 0) {
			int change = siteDao.addSites(sites);
			if (change > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean patchDeleteSites(List<Integer> names) {
		if (names.size() > 0) {
			int change = siteDao.deleteSites(names);
			if (change > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addSite(String name, int volume, int type, double lng, double lat) {
		Site site = new Site(name, volume, type, lng, lat);

		return addSite(site);
	}

	@Override
	public boolean clearTable() {
		siteDao.truncateSite();
		return true;
	}

	@Override
	public boolean writeBase(int rate, double flucSca, double countSca, double poiSca, int clusterDist,int maxPack,int minPack) {
		List<Point> points = mergeSites(rate, flucSca, countSca, poiSca, clusterDist,maxPack,minPack);

		List<Site> lSites = SiteChooser.writeToDatabase(points);
		System.out.println("站点更新结束");
		if (lSites.size() > 0) {
			Helper.getPath(lSites);
			Helper.readRouteFileAddToBase();
		}
		System.out.println("路线更新结束");
		analyze.produceSiteBikes();
		
		//在上面的工作结束后，分析历史数据，然后得到每个站点的推荐容量
		siteStorage.changeSiteStorage();

		System.out.println("容量分析结束");
		bikeTracker.produceBikeTackFile();

		System.out.println("生产追踪文件结束");
		
		flowChecker.produceAllSiteFlows(State.FLOW_OUT);
		flowChecker.produceAllSiteFlows(State.FLOW_IN);
		return false;
	}

	@Override
	public List<Point> mergeSites(int rate, double flucSca, double countSca, double poiSca, int clusterDist,int maxPack,int minPack) {
		MaxScore maxScore = SiteChooser.new MaxScore();
		Map<String, Map<String, Object>> totalScore = SiteChooser.judgeScore(maxScore, flucSca, countSca, poiSca);

		Map<Double, Lnglat> choosed = SiteChooser.chooseSite(totalScore, maxScore, rate, false);
		List<Point> sites = SiteChooser.mergeSites(choosed, clusterDist,maxPack,minPack);

		return sites;
	}

	@Override
	public Map<Double, Lnglat> getScores(int rate, double flucSca, double countSca, double poiSca) {
		MaxScore maxScore = SiteChooser.new MaxScore();
		Map<String, Map<String, Object>> totalScore = SiteChooser.judgeScore(maxScore, flucSca, countSca, poiSca);

		Map<Double, Lnglat> choosed = SiteChooser.chooseSite(totalScore, maxScore, rate, true);

		return choosed;
	}

	@Override
	public Map<String, Object> getSiteChange(String date,String choose, int siteID) {
		Map<String, Object> result = new HashMap<>();
		analyze.analyzeSiteSimilar(siteID);
		if (choose.equals("all")) {
			double[] list = analyze.analyzeSiteChange(siteID);
			Map<String, Object> bump = judger.getBump(list);
			int type = judger.getSiteType(list);
			result.put("list", list);
			result.put("bump", bump);
			result.put("type", type);

		}else if(choose.equals("predict")){
			PredictResult sitePredict=analyze.getSitePredict(siteID,date);
			if (sitePredict!=null) {
				result.put("list", sitePredict.getResults());
			}else {
				result.put("list", null);
			}
			
		}else if(choose.equals("workDayAvg")){
			int[] list=siteCircum.getAvgWorkData(siteID,1);
			result.put("list", list);
			
		}else if(choose.equals("noWorkDayAvg")){
			int[] list=siteCircum.getAvgWorkData(siteID,0);
			result.put("list", list);
		}else {
			Date askDate = DateUtil.parseToDay(date);
			List<Integer> list = analyze.estimate(askDate, siteID);
			List<CircumState> dayCircums = analyze.getDayCircums(askDate);
			result.put("list", list);
			result.put("circum", dayCircums);
			System.out.println(dayCircums);
		}
		
		
		return result;

	}

	@Override
	public boolean produceSiteBikeData() {

		return true;
	}

	@Override
	public Map<String, Object> getAllSitesWithTypes() {
		List<Site> sites = getAllSites();

		List<Integer> scores = judger.anayAllSites(sites);
		Map<String, Object> result = new HashMap<>();
		result.put("sites", sites);
		result.put("types", scores);
		return result;
	}
	
	@Override
	public Map<String, Object> getAllSitesWithWorks() {
		List<Site> sites = getAllSites();

		List<Integer> scores=siteCircum.analyzeWorkForAllSites();
		Map<String, Object> result = new HashMap<>();
		result.put("sites", sites);
		result.put("works", scores);
		return result;
	}

	@Override
	public Map<String, Object> getSitesFlow(double flowRatio,int flowType) {
		State.FLOW_RATIO=flowRatio;
		Map<Integer, List<Integer>> flowData = flowChecker.getAllSitesFlow(flowType);
		List<Site> sites = siteDao.getAll();
		for(Site site:sites) {
			double[] newLLs=CoordsUtil.turnBaiDuCoord(site.getLng(),site.getLat());
			site.setLng(newLLs[0]);
			site.setLat(newLLs[1]);
		}
		
		Map<String, Object> result=new HashMap<>();
		result.put("sites", sites);
		result.put("flow", flowData);
		return result;
	}

	@Override
	public Map<Date, Integer> getDurationInactive(String startTime, int daysBefore, int checkDay) {
		Date time=DateUtil.pareToHour(startTime);
		return lazyChecker.getDurationInactive(time, daysBefore, checkDay);
	}

	@Override
	public List<Integer> getDayInactiveBikes(String startTime, int checkDays) {
		Date time=null;
		if(startTime.equals("latest")) {
			String latFile=FilesUtil.checkLastestFile();
			Map<String, Object> files=FilesUtil.readFileInfo(latFile);
			BikeHeader header=(BikeHeader) files.get("header");
			time= header.getStartTime();
		}else {
			time=DateUtil.pareToHour(startTime);
		}
		return lazyChecker.getDayInactiveBikes(time, checkDays);
	}

	@Override
	public List<String> getInactiveBikes(String startTime, int checkDays) {
		Date time=null;
		if(startTime.equals("latest")) {
			String latFile=FilesUtil.checkLastestFile();
			Map<String, Object> files=FilesUtil.readFileInfo(latFile);
			BikeHeader header=(BikeHeader) files.get("header");
			time= header.getStartTime();
		}else {
			time=DateUtil.pareToHour(startTime);
		}
		return lazyChecker.getInactiveBikes(time, checkDays);
	}


	

}
