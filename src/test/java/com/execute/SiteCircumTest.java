package com.execute;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.util.DateUtil;
import com.xju.App;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteCircumTest {

	@Autowired
	SiteCircum circum;
	
//	@Test
//	public void testAnalyzeWeather() {
//		circum.analyzeWorkForAllSites();
//	}
	
	@Test
	public void testGetWeather() {
		circum.getWeather(1,DateUtil.parseToDay("2018_12_21"));
	}
	

//	@Test
//	public void testAnalyzeWorkDay() {
//		fail("Not yet implemented");
//	}

}
