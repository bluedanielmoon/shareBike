package com.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.execute.LazyAnalyze;
import com.execute.LazyChecker;
import com.execute.MapHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.init.State;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.Point;
import com.service.MapServ;
import com.util.DateUtil;
import com.util.FilesUtil;
import com.util.PathUtil;

@Service
public class MapServImpl implements MapServ {


	@Autowired
	private MapHelper maphelper;
	
	@Autowired
	private LazyChecker lazychecker;
	
	@Autowired
	private LazyAnalyze lazyAna;
	
	

	@Override
	public Map<String, Object> getHeatMap(String time)
			throws JsonProcessingException {
		String ss = PathUtil.getFileByTime(time);
		return FilesUtil.readFileInfo(ss);

	}
	
	public List<Map<String, Object>>  getDurationHeat(
			String startTime, String endTime) {

		Date st = DateUtil.parseToMinute(startTime);
		Date en = DateUtil.parseToMinute(endTime);
		List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st, en,false);
		return maps;
	}


	public Map<String, Object> getBikeData(String time) {
		
		
		
		String fileName=null;
		if (time.equals("latest")) {
			fileName = FilesUtil.checkLastestFile();
		} else {

			String timePath = DateUtil.parseTimeToFile(time);
			fileName = FilesUtil.DEFAULT_BIKE_FILE + timePath + ".txt";
		}
		Map<String, Object> points=FilesUtil.readFileToPoint(fileName);
		List<Point> bikes =(List<Point>) points.get("bikes");
		Date time2=null;
		if(time.equals("latest")) {
			BikeHeader header=(BikeHeader) points.get("header");
			time2= header.getStartTime();
		}else {
			time2=DateUtil.pareToHour(time);
		}
		List<String> inacts=lazyAna.getInactiveBikes(time2, 3);
		for(Point bike:bikes) {
			if(inacts.contains(bike.getName())) {
				bike.setStyle(1);
			}else {
				bike.setStyle(0);
			}
		}
		return points;
	}

	public Map<String, Map<String, Object>> getDurationBikeData(
			String startTime, String endTime, int rows, int cols) {

		Date st = DateUtil.parseToMinute(startTime);
		Date en = DateUtil.parseToMinute(endTime);
		List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st, en,false);
		BikeArea area = State.AREA;

		Map<String, Map<String, Object>> result = maphelper
				.getDurationBikesInMultiAreas(maps, area, rows, cols);

		return result;
	}

	public List<Point> getClusterMap(String time, int distance) {
		String fileName = PathUtil.getFileByTime(time);
		Map<String, Object> mp = FilesUtil.readFileToBikeMap(fileName);

		List<BikePos> bikes = (List<BikePos>) mp.get("bikes");

		List<Map<String, BikePos>> packs = maphelper.neighborCluster(bikes,
				distance,0);
		List<Point> result = maphelper.calcuClusterCenter(packs);
		return result;
	}
	
	public List<Point>  getDurationClusterMap(String start,String end, int distance) {
		
		Date st = DateUtil.parseToMinute(start);
		Date en = DateUtil.parseToMinute(end);
		List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st, en,false);
		
		
		List<BikePos> allBikes=new ArrayList<>();
		//header,bikes(bikePos)
		int count=0;
		for(Map<String, Object> map:maps) {
			List<BikePos> bikes = (List<BikePos>)map.get("bikes");
			for(BikePos b:bikes) {
				b.setBikeID(new String(""+count++));
			}
			allBikes.addAll(bikes);
		}


		List<Map<String, BikePos>> packs = maphelper.neighborCluster(allBikes,
				distance,0);
		List<Point> result = maphelper.calcuClusterCenter(packs);
		return result;
	}

	public long[] getFileRange() {
		Date[] dates=FilesUtil.getFileRange();
		long[] range=new long[] {dates[0].getTime(),dates[1].getTime()};
		return range;
		
	}

	public Map<String, Object> getDateActive(String time, int days) {
		Date date=DateUtil.pareToHour(time);
		Map<String, Integer> actives=lazychecker.checkActiveBefore(date, 3);
		List<Integer> divides =new ArrayList<>();
		divides.add(0);
		divides.add(3);
		divides.add(6);
		divides.add(10);
		Map<String, Object> result=new HashMap<>();
		result.put("pies", lazychecker.countActive(actives, divides));
		result.put("names", divides);
		return result;
	}

	public Map<String, Object> getBikeData(String time, int inAct) {
		String fileName=null;
		if (time.equals("latest")) {
			fileName = FilesUtil.checkLastestFile();
		} else {

			String timePath = DateUtil.parseTimeToFile(time);
			fileName = FilesUtil.DEFAULT_BIKE_FILE + timePath + ".txt";
		}
		Map<String, Object> points=FilesUtil.readFileToPoint(fileName);
		List<Point> bikes =(List<Point>) points.get("bikes");
		Date time2=null;
		if(time.equals("latest")) {
			BikeHeader header=(BikeHeader) points.get("header");
			time2= header.getStartTime();
		}else {
			time2=DateUtil.pareToHour(time);
		}
		List<String> inacts=lazyAna.getInactiveBikes(time2, inAct);
		for(Point bike:bikes) {
			if(inacts.contains(bike.getName())) {
				bike.setStyle(1);
			}else {
				bike.setStyle(0);
			}
		}
		return points;
	}
}
