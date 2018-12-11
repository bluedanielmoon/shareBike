package com.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.RouteDao;
import com.pojo.Route;
import com.service.RouteServ;

@Service
public class RouteServImpl implements RouteServ{

	@Autowired
	private RouteDao routeDao;
	
	@Override
	public Route getRoute(int fromID, int toID) {
		
		return routeDao.getRoute(fromID, toID);
	}

	@Override
	public boolean patchAddPoi(List<Route> list) {
		if (routeDao.addRoutes(list) > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean clearTable() {
		routeDao.truncateRoute();
		return true;
	}

}
