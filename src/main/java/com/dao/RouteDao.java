package com.dao;

import java.util.List;

import com.pojo.Route;

public interface RouteDao {

	Route getRoute(int fromId,int toId);
	
	int addRoutes(List<Route> list);
	
	int truncateRoute();
}
