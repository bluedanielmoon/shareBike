package com.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.SiteDao;
import com.pojo.Site;
import com.service.SiteServ;

@Service
public class SiteServimpl implements SiteServ {

	@Autowired
	private SiteDao siteDao;
	
	public SiteServimpl(SiteDao userDao) {
		this.siteDao = userDao;
	}

	@Override
	public Site getSiteById(int id) {
		// TODO Auto-generated method stub
		return siteDao.getSite(id);
	}

	@Override
	public List<Site> getAllSites() {
		// TODO Auto-generated method stub
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
	public boolean patchDeleteSites(List<String> names) {
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

}
