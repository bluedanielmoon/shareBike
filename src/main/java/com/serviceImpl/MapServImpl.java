package com.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.execute.MapHelper;
import com.execute.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pojo.BikeArea;
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
		List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st, en);
		return maps;
	}


	public Map<String, Object> getBikeData(String time) {
		String ss = PathUtil.getFileByTime(time);
		return FilesUtil.readFileToPoint(ss);
	}

	public Map<String, Map<String, Object>> getDurationBikeData(
			String startTime, String endTime, int rows, int cols) {

		Date st = DateUtil.parseToMinute(startTime);
		Date en = DateUtil.parseToMinute(endTime);
		List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st, en);
		BikeArea area = State.getArea();

		Map<String, Map<String, Object>> result = maphelper
				.getDurationBikesInMultiAreas(maps, area, rows, cols);

		return result;
	}

	public List<Point> getClusterMap(String time, int distance) {
		String fileName = PathUtil.getFileByTime(time);
		Map<String, Object> mp = FilesUtil.readFileToBikeMap(fileName);

		List<BikePos> bikes = (List<BikePos>) mp.get("bikes");

		List<Map<String, BikePos>> packs = maphelper.neighborCluster(bikes,
				distance);
		List<Point> result = maphelper.calcuClusterCenter(packs);
		return result;
	}
	
	public List<Point>  getDurationClusterMap(String start,String end, int distance) {
		
		Date st = DateUtil.parseToMinute(start);
		Date en = DateUtil.parseToMinute(end);
		List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st, en);
		
		
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
				distance);
		List<Point> result = maphelper.calcuClusterCenter(packs);
		return result;
	}

	public long[] getFileRange() {
		Date[] dates=FilesUtil.getFileRange();
		long[] range=new long[] {dates[0].getTime(),dates[1].getTime()};
		return range;
		
	}

	public static void main(String[] args) {
		int i=0;
		while(i<10) {
			System.out.println(new String(""+i));
			i++;
				
		}
	}
}
