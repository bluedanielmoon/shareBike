package com.execute;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteTypeJudgerTest {
	
	@Autowired
	private SiteTypeJudger judger;

	@Test
	public void testanayAllSites() {

	}

}
