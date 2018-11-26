package com.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.dao.PoiDao;
import com.pojo.Poi;
import com.service.PoiServ;

public class PoiLocal implements PoiServ {
	private final String resource = "config/mybatis.xml";
	private SqlSessionFactory sqlSessionFactory;
	
	public PoiLocal() {
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	}

	@Override
	public Poi getPoi(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Poi> getAllPois() {
		
		List<Poi> ls =new ArrayList<>();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			PoiDao mapper = session.getMapper(PoiDao.class);
			ls = mapper.getAll();
		} finally {
			session.close();
		}
		return ls;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addPoi(Poi poi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPoi(String name, int type, double lng, double lat) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updatePoi(Poi poi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deletePoi(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean patchAddPoi(List<Poi> list) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean patchDeletePoi(List<String> names) {
		// TODO Auto-generated method stub
		return false;
	}

}
