package com.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.helper.SiteHelper;
import com.pojo.Lnglat;
import com.pojo.Route;
import com.service.RouteServ;
import com.xju.App;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class RouteServImplTest {

	
	@Autowired
	private RouteServ routeServ;
	
	@Autowired
	private SiteHelper Helper;
	
	@Test
	public void testGetRoute() {
		Route route=routeServ.getRoute(277, 266);

		List<Lnglat> paths=Helper.readRoute(route);
		System.out.println(paths);
		
	}
	
	@Test
	public void testClear() {
		routeServ.clearTable();
		
	}

	@Test
	public void testPatchAddPoi() {
		List<Route> list=new ArrayList<>();
		list.add(new Route());
		list.add(new Route());
		Route route=new Route();
		route.setPath("[{\"lng\":108.914398,\"lat\":34.242435},{\"lng\":108.916878,\"lat\":34.241165}]");
		list.add(route);
		routeServ.patchAddPoi(list);
	}

}
