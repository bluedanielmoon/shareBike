package com.service;

import java.util.List;

import com.pojo.Site;

public interface SiteServ {
	
	Site getSiteById(int id);
	
	List<Site> getAllSites();
	
	boolean addSite(Site site);

	boolean addSite(String name,int volume,int type,double lng,double lat);
	
	boolean updateSite(Site site);
	
	boolean deleteSite(int id);
	
	boolean patchaddSites(List<Site> sites);
	
	boolean patchDeleteSites(List<String> names);
}
