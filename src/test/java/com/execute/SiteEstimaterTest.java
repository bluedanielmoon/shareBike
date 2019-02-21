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
		int hour=17;
		cm.setHour(hour);
		cm.setTemp(1);
		cm.setWeather(1);
		cm.setWorkDay(0);
		Site site=siteServ.getSiteById(1);	
		Date date=DateUtil.parseToDay("2018_11_29");
    	estim.estimateByHistory(site, hour,date,null);
	}

}
