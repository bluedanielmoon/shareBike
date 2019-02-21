package com.execute;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.service.SiteServ;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class HeaterTest {
	
	@Autowired
	Heater heater;
	
	@Autowired
	SiteServ siteServ;
	
	@Test
	public void testPutBikesToSite() {
		
		
	}
	//1小时---51
	//2小时---87
	//3小时--102
	//5小时--141
	
	//1--17,3,23,127,72
	//2--3,21,21,50,50,93,112,120,122,147,148
	//3--32,34,42,93,108,110,111,141,146
	//4--6,6,23
	//5--3,3,99,133
	//6--13,24,75
	//7--6,75,121,141
	//
	
	
}
