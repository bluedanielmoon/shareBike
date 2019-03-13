package com.execute;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.xju.App;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteChooserTest {
	
	@Autowired
	private SiteChooser chooser;

	@Test
	public void testProduceScoreFile() {
		chooser.produceScoreFile(50);
	}

//	@Test
//	public void testJudgeScore() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testChooseSite() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMergeSites() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testWriteToDatabase() {
//		fail("Not yet implemented");
//	}

}
