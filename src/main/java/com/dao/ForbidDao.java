package com.dao;

import java.util.List;

import com.pojo.ForbidArea;
public interface ForbidDao {
	ForbidArea getArea(int id);
	
	int addArea(ForbidArea p);
	
	int addAreaList(List<ForbidArea> list);
	
	List<ForbidArea> getAll();
	
	int updateArea(ForbidArea p);
	
	int deleteArea(int id);
	
	int deleteAreas(List<Integer> ids);
}
