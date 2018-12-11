package com.simu;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
=======
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

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

<<<<<<< HEAD
	
	@Test
	public void testStartSimu() {
=======
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
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		Simulator simulator = new Simulator();

		List<Site> sites = new ArrayList<>();
		List<Dispatcher> dispatchers = new ArrayList<>();
		Date startTime = new Date();
		Date endTime = new Date();
		int timeSpeed = 1;
		simulator.init(sites, dispatchers, startTime, endTime, timeSpeed);
<<<<<<< HEAD

		simulator.startSimu();		
	}

=======
		new Thread(simulator).start();
	}


>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
}
