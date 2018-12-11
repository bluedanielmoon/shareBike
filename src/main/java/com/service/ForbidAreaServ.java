package com.service;

import java.util.List;

import com.pojo.ForbidArea;
import com.pojo.Lnglat;

public interface ForbidAreaServ {
	ForbidArea getAreaById(int id);
	
	List<ForbidArea> getAllAreas();
	
	boolean addArea(ForbidArea area);

	boolean addArea(String name,List<String> paths);
	
	boolean updateArea(ForbidArea area);
	
	boolean deleteArea(int id);
	
	boolean patchaddAreas(List<ForbidArea> areas);
	
	boolean patchDeleteAreas(List<Integer> ids);
	
	boolean clearTable();

	boolean updateArea(int id, String name);

}
