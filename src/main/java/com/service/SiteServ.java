package com.service;

import java.util.List;
import java.util.Map;

import com.pojo.Lnglat;
import com.pojo.Point;
import com.pojo.Site;

public interface SiteServ {
	
	Site getSiteById(int id);
	
	List<Site> getAllSites();
	
	boolean addSite(Site site);

	boolean addSite(String name,int volume,int type,double lng,double lat);
	
	boolean updateSite(Site site);
	
	boolean updateSite(int id,double lng,double lat);
	
	boolean updateSite(int id, String siteName, int siteLimit, int siteType);
	
	boolean deleteSite(int id);
	
	boolean patchaddSites(List<Site> sites);
	
	boolean patchDeleteSites(List<Integer> names);
	
	boolean clearTable();
	
	boolean writeBase(int rate,double flucSca,double countSca,double poiSca,int clusterDist);

	List<Point> mergeSites(int rate, double flucSca, double countSca, double poiSca, int clusterDist);

	Map<Double, Lnglat> getScores(int rate, double flucSca, double countSca, double poiSca);

	double[] getSiteChange(int siteID);

	boolean produceSiteBikeData();
}
