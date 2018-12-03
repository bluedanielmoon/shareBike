package com.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.execute.BikesCounter;
import com.execute.Heater;
import com.pojo.BikeHeader;
import com.pojo.Varia;
import com.util.DateUtil;
import com.util.FilesUtil;

@Service
public class AnayServ {
	
	@Autowired
	private BikesCounter counter;
	
	@Autowired
	private Heater heater;

	public List<Varia> getVariaData(String time, int dist,int type) {
		if(time.equals("latest")) {
			time = DateUtil.getLatestDay();
		}
		
		return heater.checkOrProduce(time, dist,type);

	}

	public List<BikeHeader> getBikeInfo() {

		List<Path> all = FilesUtil.listAllFiles(false);
		List<BikeHeader> list = new ArrayList<>();
		BikeHeader bk = null;
		for (Path p : all) {

			bk = FilesUtil.readFileHeader(p.toString());
			list.add(bk);
		}
		list.sort(new Comparator<BikeHeader>() {

			@Override
			public int compare(BikeHeader o1, BikeHeader o2) {
				return o1.getStartTime().compareTo(o2.getStartTime());
			}
		});

		return list;

	}


	public Map<Integer, List<Map<String, Object>>> getBikeDaily() {

		return counter.countByDay();
	}
}
