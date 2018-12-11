package com.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.ForbidDao;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.ForbidArea;
import com.pojo.Lnglat;
import com.service.ForbidAreaServ;

@Service
public class ForbidAreaServImpl implements ForbidAreaServ {

	
	@Autowired
	private ForbidDao areaDao;
	@Override
	public ForbidArea getAreaById(int id) {
		return areaDao.getArea(id);
		
	}

	@Override
	public List<ForbidArea> getAllAreas() {
		return areaDao.getAll();
	}

	@Override
	public boolean addArea(ForbidArea area) {
		if (areaDao.addArea(area) == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean addArea(String name, List<String> paths) {
		ObjectMapper mapper=new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		List<Lnglat> list=new ArrayList<>();
		for(String s:paths) {
			String[] nums=s.split(",");
			double[] cords=new double[] {Double.parseDouble(nums[0]),Double.parseDouble(nums[1])};
			Lnglat lnglat=new Lnglat(cords[0], cords[1]);
			list.add(lnglat);

		}
		ForbidArea area=new ForbidArea();
		area.setName(name);
		try {
			area.setPath(mapper.writeValueAsString(list));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println(area);
		if (areaDao.addArea(area) == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean updateArea(ForbidArea area) {
		if (areaDao.updateArea(area) == 1) {
			return true;
		}

		return false;
	}
	
	@Override
	public boolean updateArea(int id, String name) {
		ForbidArea area=areaDao.getArea(id);
		area.setName(name);
		return updateArea(area);
	}

	@Override
	public boolean deleteArea(int id) {
		if (areaDao.deleteArea(id) == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean patchaddAreas(List<ForbidArea> areas) {
		
		return false;
	}

	@Override
	public boolean patchDeleteAreas(List<Integer> ids) {
		if (ids.size() > 0) {
			int change = areaDao.deleteAreas(ids);
			if (change > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean clearTable() {
		// TODO Auto-generated method stub
		return false;
	}

	



}
