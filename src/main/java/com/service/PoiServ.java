package com.service;

import java.util.List;

import com.pojo.Poi;

public interface PoiServ {
	
	Poi getPoi(int id);
	
	List<Poi> getAllPois();
	
	void deleteAll();
	
	boolean addPoi(Poi poi);
	
	boolean addPoi(String name, int type, double lng, double lat);
	
	boolean updatePoi(Poi poi);
	
	boolean deletePoi(int id);
	
	boolean patchAddPoi(List<Poi> list);
	
	boolean patchDeletePoi(List<String> names);

	
	
}
