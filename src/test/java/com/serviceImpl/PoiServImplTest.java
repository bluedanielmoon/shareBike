package com.serviceImpl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.Poi;
import com.service.PoiServ;
import com.service.UserServ;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class PoiServImplTest {
	
	@Autowired
	private PoiServ poiServ;
	
	
	@Test
	public void testGetPoi() {
		
		System.out.println(poiServ.getPoi(1));
	}

	@Test
	public void testGetAllPois() {
		System.out.println(poiServ.getAllPois());
	}

	@Test
	public void testAddPoiPoi() {
		Poi p=new Poi("缝合路", 2, 123.123, 123.123123);
		poiServ.addPoi(p);
	}

	@Test
	public void testAddPoiStringIntDoubleDouble() {
		String name="doc";
		int type=3;
		double lng= 123.123;
		double lat=456.456;
		poiServ.addPoi(name, type, lng, lat);
	}

	@Test
	public void testUpdatePoi() {
		Poi p= poiServ.getPoi(1);
		p.setName("hospital");
		poiServ.updatePoi(p);
	}

	@Test
	public void testDeletePoi() {
		
		poiServ.deletePoi(2);
	}

	@Test
	public void testPatchDeletePoi() {
		List<Poi> ls=poiServ.getAllPois();
		List<String> names=new ArrayList<>();
		for(Poi p:ls) {
			names.add(p.getName());
		}
		poiServ.patchDeletePoi(names);
	}

}
