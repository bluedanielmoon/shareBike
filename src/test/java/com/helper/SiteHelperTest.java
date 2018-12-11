package com.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.Route;
import com.service.RouteServ;
import com.xju.App;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SiteHelperTest {

	
	@Autowired
	private SiteHelper helper;
	
	@Autowired
	private RouteServ routeServ;
	
	@Test
	public void testReadRoute() {
		Route route=routeServ.getRoute(289, 266);
		helper.readRoute(route);
		
	}
	@Test
	public void testGetPath() {
	
	}

	@Test
	public void testReadRouteFileAddToBase() {
		helper.readRouteFileAddToBase();
		
	}

	
}
