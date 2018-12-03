package com.simu;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.SimuTask;
import com.pojo.Site;
import com.util.DateUtil;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class TaskProducerTest {

	@Test
	public void testTaskProducer() {
		fail("Not yet implemented");
	}

	@Test
	public void testAssignLoadTask() {
		TaskProducer tasker=new TaskProducer();
		
		Date oneDay=DateUtil.parseToDay("2018_11_1");
		int startHour=7;
		tasker.initJobs(oneDay);
		List<SimuTask> tasks= tasker.produceStartJobs(startHour);
		System.out.println(tasks);
	}

	@Test
	public void testInitJobsDate() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceStartJobs() {
		fail("Not yet implemented");
	}

	@Test
	public void testInitJobsListOfSiteMapOfIntegerMapOfStringObjectTreeMapOfDoubleIntegerTreeMapOfDoubleInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecideInitMoveTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testAnayLizeMove() {
		fail("Not yet implemented");
	}

	@Test
	public void testEstimate() {
		Date oneDay=DateUtil.parseToDay("2018_11_1");
		TaskProducer producer=new TaskProducer();
		producer.initJobs(oneDay);
		Site site= new Site();
		site.setId(279);
		producer.estimate(site,7);
	}

	@Test
	public void testInitSiteInfos() {
		fail("Not yet implemented");
	}

	@Test
	public void testInitDispatchers() {
		fail("Not yet implemented");
	}

}
