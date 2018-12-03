package com.init;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pojo.GaodeStop;
import com.pojo.Poi;
import com.service.PoiServ;
import com.serviceImpl.PoiServImpl;
import com.util.BusUtil;

@Component
public class InitPoi {
	
	@Autowired
	private PoiServ poiServ;
	
	@Autowired
	public InitPoi(PoiServ poiServ) {
		this.poiServ=poiServ;
		poiServ.deleteAll();
		
	}

	public void initBus() {
		
		List<GaodeStop> stops=BusUtil.readBusStops();
		List<Poi> buses=new ArrayList<>();
		for(GaodeStop stop:stops) {
			Poi poi=new Poi(stop.getName(), State.POI_BUS, stop.getLocation().getLng(), stop.getLocation().getLat());
			buses.add(poi);
		}
		
		poiServ.patchAddPoi(buses);
		System.out.println("初始化数据库中公交:"+buses.size());
		
	}
	
	public void initSubway() {
		List<GaodeStop> stops=BusUtil.readSubStations();
		List<Poi> subs=new ArrayList<>();
		for(GaodeStop stop:stops) {
			Poi poi=new Poi(stop.getName(),State.POI_SUBWAY, stop.getLocation().getLng(), stop.getLocation().getLat());
			subs.add(poi);
		}
		poiServ.patchAddPoi(subs);
		System.out.println("初始化数据库中地铁:"+subs.size());
		
	}
	
	
}
