package com.execute;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.util.DateUtil;
import com.xju.App;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class LazyAnalyzeTest {

	@Autowired
	private LazyAnalyze lazyAnalyze;
	
	@Test
	public void testGetDurationInactive() {
		Date startTime=DateUtil.pareToHour("2019_1_14 23");
		Map<Date,Integer> result=lazyAnalyze.getDurationInactive(startTime, 30, 24);
		lazyAnalyze.writeResult(result);
	}

	@Test
	public void testGetDayInactiveBikes() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInactiveBikes() {
		fail("Not yet implemented");
	}

}
