package com.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.PoiDao;
import com.pojo.Poi;
import com.service.PoiServ;

@Service
public class PoiServImpl implements PoiServ {

	@Autowired
	private PoiDao poiDao;

	@Override
	public Poi getPoi(int id) {
		return poiDao.getPoi(id);
	}

	public static void main(String[] args) {
		PoiServImpl impl = new PoiServImpl();
		impl.getAllPois();
	}

	@Override
	public List<Poi> getAllPois() {
		
		return poiDao.getAll();
	}

	@Override
	public boolean addPoi(Poi poi) {
		if (poiDao.addPoi(poi) == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean addPoi(String name, int type, double lng, double lat) {
		Poi poi = new Poi(name, type, lng, lat);
		if (poiDao.addPoi(poi) == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean updatePoi(Poi poi) {
		if (poiDao.updatePoi(poi) == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean deletePoi(int id) {
		if (poiDao.deletePoi(id) == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean patchAddPoi(List<Poi> list) {
		if (poiDao.addPoiList(list) > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean patchDeletePoi(List<String> names) {
		if (names.size() > 0 && poiDao.deletePois(names) > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void deleteAll() {
		List<Poi> allPois = poiDao.getAll();
		if (allPois.size() == 0) {
			return;
		}
		List<String> names = new ArrayList<>();
		for (Poi p : allPois) {
			names.add(p.getName());
		}
		poiDao.deletePois(names);

	}

}
