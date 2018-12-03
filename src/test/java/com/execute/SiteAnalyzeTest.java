package com.execute;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.CircumState;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteAnalyzeTest {
	
	@Autowired
	private SiteAnalyze siteAna;

	@Test
	public void testGetFileHeaders() {
		siteAna.getFileHeaders();
	}
//
//	@Test
//	public void testGo() {
//		siteAna.go();
//	}
//	
//	@Test
//	public void testCreateDefaultCircums() {
//		siteAna.createDefaultCircums();
//	}
	
	@Test
	public void testCreateSitesCircums() {
		CircumState circumState = new CircumState();
		circumState.setHour(12);
		circumState.setWorkDay(10);
		circumState.setWeather(1);
		circumState.setTemp(1);
		
		int id=9;
		List<Integer> ls= siteAna.getSitesCircums(circumState,id);
		
		System.out.println(ls);
	}
	

}
