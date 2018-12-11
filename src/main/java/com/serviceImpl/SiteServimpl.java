package com.serviceImpl;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.SiteDao;
<<<<<<< HEAD
import com.execute.SiteAnalyze;
import com.execute.SiteChooser;
import com.execute.SiteTypeJudger;
import com.execute.SiteChooser.MaxScore;
import com.helper.SiteHelper;
import com.pojo.Lnglat;
import com.pojo.Point;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.pojo.Site;
import com.service.SiteServ;

@Service
public class SiteServimpl implements SiteServ {

	@Autowired
	private SiteDao siteDao;
	
<<<<<<< HEAD
	@Autowired
	private SiteChooser SiteChooser;
	
	@Autowired
	private SiteAnalyze analyze;
	
	@Autowired
	private SiteHelper Helper;
	
	@Autowired
	private SiteTypeJudger judger;
	
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
	public SiteServimpl(SiteDao userDao) {
		this.siteDao = userDao;
	}

	@Override
	public Site getSiteById(int id) {
<<<<<<< HEAD
=======
		// TODO Auto-generated method stub
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		return siteDao.getSite(id);
	}

	@Override
	public List<Site> getAllSites() {
<<<<<<< HEAD
=======
		// TODO Auto-generated method stub
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
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
<<<<<<< HEAD
	
	@Override
	public boolean updateSite(int id,double lng, double lat) {
		Site site=siteDao.getSite(id);
		site.setLng(lng);
		site.setLat(lat);
		return updateSite(site);
	}
	
	@Override
	public boolean updateSite(int id, String siteName, int siteLimit, int siteType) {
		Site site=siteDao.getSite(id);
		site.setName(siteName);
		site.setVolume(siteLimit);
		site.setType(1);
		return updateSite(site);
	}
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

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
<<<<<<< HEAD
	public boolean patchDeleteSites(List<Integer> names) {
=======
	public boolean patchDeleteSites(List<String> names) {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
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
		Site site= new Site(name, volume, type, lng, lat);
		
		return addSite(site);
	}

<<<<<<< HEAD
	@Override
	public boolean clearTable() {
		siteDao.truncateSite();
		return true;
	}

	@Override
	public boolean writeBase(int rate,double flucSca,double countSca,double poiSca,int clusterDist) {
		List<Point> points=mergeSites(rate,flucSca, countSca, poiSca, clusterDist);
		
		List<Site>  lSites =SiteChooser.writeToDatabase(points);
		if(lSites.size()>0) {
			Helper.getPath(lSites);
			Helper.readRouteFileAddToBase();
		}
		analyze.produceSiteBikes();
		return false;
	}
	
	@Override
	public List<Point> mergeSites(int rate,double flucSca,double countSca,double poiSca,int clusterDist) {
		MaxScore maxScore=SiteChooser.new MaxScore();
		Map<String, Map<String, Object>> totalScore =SiteChooser.judgeScore(maxScore,flucSca,countSca,poiSca);
		
		Map<Double, Lnglat> choosed=SiteChooser.chooseSite(totalScore,maxScore,rate,false);
		List<Point> sites=SiteChooser.mergeSites(choosed, clusterDist);
		
		return sites;
	}
	
	@Override
	public Map<Double, Lnglat> getScores(int rate,double flucSca,double countSca,double poiSca) {
		MaxScore maxScore=SiteChooser.new MaxScore();
		Map<String, Map<String, Object>> totalScore =SiteChooser.judgeScore(maxScore,flucSca,countSca,poiSca);
		
		Map<Double, Lnglat> choosed=SiteChooser.chooseSite(totalScore,maxScore,rate,false);
		
		return choosed;
	}

	@Override
	public double[] getSiteChange(int siteID) {
		double[] list= analyze.analyzeSiteChange(siteID);
		
		judger.getSiteType(list);
		return list;
	}

	@Override
	public boolean produceSiteBikeData() {
		
		return true;
	}

=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
}
