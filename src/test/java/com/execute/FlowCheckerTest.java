package com.execute;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.init.State;
import com.pojo.Site;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class FlowCheckerTest {
	
	@Autowired
	FlowChecker checker;

	@Test
	public void testAnalyzeSiteFlowByAllHistory() {
		
		//checker.calcuSortWrite(19, State.FLOW_OUT);
//		List<Site> sortCounts=checker.readFlow(19, State.FLOW_IN);
//		for(int i=0;i<10;i++) {
//			Site site=sortCounts.get(i);
//			System.out.println(site.getId());
//		}
//		System.out.print(sortCounts+" ");
	}
	
	@Test
	public void testGetSiteFlow() {
		
//		checker.flowType();
	}
	
	@Test
	public void testProduceAllSiteFlows() {
		
		checker.produceAllSiteFlows(State.FLOW_IN);
	}
	
}
