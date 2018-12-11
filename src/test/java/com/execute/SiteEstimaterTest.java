package com.execute;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.CircumState;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.DateUtil;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteEstimaterTest {
	
	@Autowired
	SiteEstimater estim;
	
	@Autowired
	SiteServ siteServ;

	@Test
	public void testEstimate() {
		CircumState cm=new CircumState();
		int hour=7;
		cm.setHour(hour);
//		// 1-work ,0-notWork
//		private int workDay;
//		// 1-canGo,0-cannot Go
//		private int weather;
//		// 1-[10,25],2-[5-10,25-30],3-[<5,>30]
		cm.setTemp(1);
		cm.setWeather(1);
		cm.setWorkDay(1);
		
		//19-7
		Site site=siteServ.getSiteById(275);	
		Date date=DateUtil.pareToHour("2018_11_13 9");
    	//estim.estimateByHistory(site, hour,date,null);
	}

}
