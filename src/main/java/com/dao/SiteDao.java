package com.dao;

import java.util.List;
import com.pojo.Site;

public interface SiteDao{
	
	
	Site getSite(int id);
	
	int addSite(Site site);
	
	List<Site> getAll();
	
	int updateSite(Site site);
	
	int deleteSite(int id);
	
	int addSites(List<Site> sites);
	
	int deleteSites(List<Integer> names);

	int truncateSite();

}