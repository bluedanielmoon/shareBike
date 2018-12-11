package com.service;

import java.util.List;

import com.pojo.Route;

public interface RouteServ {
	
	Route getRoute(int fromID,int toID);
	
	boolean patchAddPoi(List<Route> list);
	
	boolean clearTable();
	
}
