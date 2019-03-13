package com.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.serviceImpl.SimuServImpl;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SimuControllerTest {
	@Autowired
	private SimuServImpl simuServ;
	
//	@Test
//	public void testStartSimu() {
//		simuServ.initAndStart();
//	}

//	@Test
//	public void testGetTasks() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testPauseSimu() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testResumeSimu() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testStopSimu() {
//		fail("Not yet implemented");
//	}

}
