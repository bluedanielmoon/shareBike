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
	
<<<<<<< HEAD
	int deleteSites(List<Integer> names);

	int truncateSite();
=======
	int deleteSites(List<String> names);

	
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

}