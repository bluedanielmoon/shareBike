package com.execute;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.CircumState;
import com.util.DateUtil;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteAnalyzeTest {
	
	@Autowired
	private SiteAnalyze siteAna;

	/**
	 * 生产出需要的站点环境和站点单车文件
	 */
	@Test
	public void testProduce() {
		siteAna.produceSiteBikes();
	}
	
	@Test
	public void testanalyzeSiteChange() {
		siteAna.analyzeSiteChange(1);
	}
	
	@Test
	public void testsiteActives() {
		Date date=DateUtil.pareToHour("2018_11_3 12");
		siteAna.siteActives(date,2);
	}
	
	@Test
	public void testEstimate() {
		Date askDate=DateUtil.parseToDay("2018_12_21");
		List<Integer> ls=siteAna.estimate(askDate, 1);
		System.out.println(ls);
	}
	
	@Test
	public void testanalyzeSiteSimilar() {
		siteAna.analyzeSiteSimilar(1);
	}
	
	

//	@Test
//	public void testCreateSitesCircums() {
//		CircumState circumState = new CircumState();
//		circumState.setHour(12);
//		circumState.setWorkDay(10);
//		circumState.setWeather(1);
//		circumState.setTemp(1);
//		
//		int id=9;
//		List<Integer> ls= siteAna.getSitesCircums(circumState,id);
//		
//		System.out.println(ls);
//	}
	

}
