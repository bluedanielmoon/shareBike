package com.simu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.Dispatcher;
import com.pojo.SimuTask;
import com.pojo.Site;
import com.service.DispatcherServ;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SimulatorTest {

	@Autowired
	DispatcherServ dispatcherServ;
	@Test
	public void testStartSimu() {
		Simulator simulator = new Simulator();

		List<Site> sites = new ArrayList<>();
		List<Dispatcher> dispatchers = new ArrayList<>();
		Dispatcher d1=dispatcherServ.getDispatcherById(11);
		d1.setLng(108.922413);
		d1.setLat(34.263833);
		Dispatcher d2=dispatcherServ.getDispatcherById(12);
		d2.setLng(108.947035);
		d2.setLat(34.26243);
		
		dispatchers.add(d1);
		dispatchers.add(d2);
		
		
		
		Date startTime = new Date();
		Date endTime = new Date();
		int timeSpeed = 1;
//		simulator.init(sites, dispatchers, startTime, endTime, timeSpeed);
		
		new Thread(simulator).start();
		
		
	}

}
