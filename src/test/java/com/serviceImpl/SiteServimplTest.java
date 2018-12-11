package com.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.Site;
import com.service.SiteServ;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteServimplTest {
	
	@Autowired
	private SiteServ siteServ;

	@Test
	public void testGetSiteById() {
		siteServ.getSiteById(1);
	}

	@Test
	public void testGetAllSites() {
		siteServ.getAllSites();
	}

	@Test
	public void testAddSite() {
		Site site=new Site("123", 20, 1, 123.123, 456.456);
		siteServ.addSite(site);
	}

	@Test
	public void testUpdateSite() {
		Site site=siteServ.getSiteById(1);
		site.setName("1111111");
		siteServ.updateSite(site);
	}

	@Test
	public void testDeleteSite() {
		siteServ.deleteSite(1);
	}

	@Test
	public void testPatchaddSites() {
		List<Site> ls=new ArrayList<>();
		Site s1=new Site("1111", 1, 1, 123.123, 123.123);
		Site s2=new Site("2222", 1, 1, 123.123, 123.123);
		Site s3=new Site("3333", 1, 1, 123.123, 123.123);
		
		ls.add(s1);
		ls.add(s2);
		ls.add(s3);
		siteServ.patchaddSites(ls);
	}

	@Test
	public void testPatchDeleteSites() {
		List<Site> ls=siteServ.getAllSites();
		List<Integer> names=new ArrayList<>();
		for(Site u:ls) {
			names.add(u.getId());
		}
		siteServ.patchDeleteSites(names);
	}
	
	@Test
	public void testClear() {
		siteServ.clearTable();
	}

}
