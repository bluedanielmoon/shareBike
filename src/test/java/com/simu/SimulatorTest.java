package com.simu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.Dispatcher;
import com.pojo.Site;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SimulatorTest {

	
	@Test
	public void testStartSimu() {
		Simulator simulator = new Simulator();

		List<Site> sites = new ArrayList<>();
		List<Dispatcher> dispatchers = new ArrayList<>();
		Date startTime = new Date();
		Date endTime = new Date();
		int timeSpeed = 1;
		simulator.init(sites, dispatchers, startTime, endTime, timeSpeed);

		simulator.startSimu();		
	}

}
