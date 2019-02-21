package com.serviceImpl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.RouteDao;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.GaodePath;
import com.pojo.Lnglat;
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
	public boolean patchAddRoute(List<Route> list) {
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

	@Override
	public GaodePath turnRoute2Path(Route route) {
		
		GaodePath path=new GaodePath();
		if(route==null) {
			return null;
		}
		String s_path=route.getPath();
		ObjectMapper mapper=new ObjectMapper();
		JavaType listType=mapper.getTypeFactory().constructCollectionType(List.class, Lnglat.class);
		
		try {
			List<Lnglat> paths = mapper.readValue(s_path, listType);
			path.setPaths(paths);
			path.setDistance(route.getDistance());
			path.setDuration(route.getDuration());
			if(paths!=null) {
				return path;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
