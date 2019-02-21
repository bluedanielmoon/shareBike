package com.service;

import java.util.List;

import com.pojo.GaodePath;
import com.pojo.Route;

public interface RouteServ {
	
	Route getRoute(int fromID,int toID);
	
	boolean patchAddRoute(List<Route> list);
	
	boolean clearTable();

	GaodePath turnRoute2Path(Route route);
	
}
