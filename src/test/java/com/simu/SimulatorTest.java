package com.simu;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

//	@Test
//	public void testRun() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testPause() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testResume() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testStop() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testInit() {
		Simulator simulator = new Simulator();

		List<Site> sites = new ArrayList<>();
		List<Dispatcher> dispatchers = new ArrayList<>();
		Date startTime = new Date();
		Date endTime = new Date();
		int timeSpeed = 1;
		simulator.init(sites, dispatchers, startTime, endTime, timeSpeed);
		new Thread(simulator).start();
	}


}
