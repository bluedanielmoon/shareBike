package com.dao;

import java.util.List;

import com.pojo.Poi;

public interface PoiDao{
	
	
	Poi getPoi(int id);
	
	int addPoi(Poi p);
	
	int addPoiList(List<Poi> list);
	
	List<Poi> getAll();
	
	int updatePoi(Poi p);
	
	int deletePoi(int id);
	
	int deletePois(List<String> names);

}
