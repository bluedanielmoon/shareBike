package com.simu;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.init.State;
import com.pojo.Site;
import com.pojo.SitePlan;
import com.util.DateUtil;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class TaskProducerTest {
	
	

	@Test
	public void testTaskProducer() {
		TaskProducer taskProducer=new TaskProducer();
		 int nowCount=12;
		 int nextEst=35;
		 int volume=20;
		 int trend= 1;
		 int carHave=10;
		 int canLoad=10;
		 SitePlan plan=taskProducer.analyzeSiteNeed(nowCount, nextEst, volume, trend, carHave, canLoad);
		 System.out.println(plan.getLoadCount());
	}

//	@Test
//	public void testAssignLoadTask() {
//		TaskProducer tasker=new TaskProducer();
//		
//		Date oneDay=DateUtil.parseToDay("2018_11_1");
//		int startHour=7;
//		
//	}
//	@Test
//	public void testCalcuTimeSpan() {
//		TaskProducer tProducer=new TaskProducer();
//		int sends=tProducer.calcuTimeSpan(1000, State.TRUCK_TYPE);
//		System.out.println(sends);
//	}
//
//	@Test
//	public void testInitJobsDate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testProduceStartJobs() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testInitJobsListOfSiteMapOfIntegerMapOfStringObjectTreeMapOfDoubleIntegerTreeMapOfDoubleInteger() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDecideInitMoveTasks() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAnayLizeMove() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEstimate() {
//		Date oneDay=DateUtil.parseToDay("2018_11_1");
//		TaskProducer producer=new TaskProducer();
////		producer.initJobs(oneDay);
//		Site site= new Site();
//		site.setId(279);
//	}
//
//	@Test
//	public void testInitSiteInfos() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testInitDispatchers() {
//		fail("Not yet implemented");
//	}

}
