package com.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.execute.MapHelper;
import com.execute.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.Bike;
import com.pojo.BikeArea;
import com.pojo.BikePos;
import com.pojo.ClusterPt;
import com.pojo.Point;
import com.service.MapServ;
import com.util.DateUtil;
import com.util.FilesUtil;
import com.util.PathUtil;

@Service
public class MapServImpl implements MapServ {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MapHelper maphelper;

	@Override
	public Map<String, Object> getHeatMap(String time)
			throws JsonProcessingException {
		String ss = PathUtil.getFileByTime(time);
		return FilesUtil.readFileInfo(ss);

	}

	public Map<String, Object> getBikeData(String time) {

		String ss = PathUtil.getFileByTime(time);
		return FilesUtil.readFileToPoint(ss);
	}

	public Map<String, Map<String, Object>> getDurationBikeData(
			String startTime, String endTime, int rows, int cols) {

		// "2018_11_1 3";
		Date st = DateUtil.pareFileTime(startTime);
		Date en = DateUtil.pareFileTime(endTime);
		List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st, en);
		BikeArea area = State.getArea();

		Map<String, Map<String, Object>> result = maphelper
				.getDurationBikesInMultiAreas(maps, area, rows, cols);

		return result;
	}

	public Map<String, Object> getClusterMap(String time, int distance) {
		String fileName = PathUtil.getFileByTime(time);
		Map<String, Object> mp = FilesUtil.readFileToBikeMap(fileName);

		@SuppressWarnings("unchecked")
		List<BikePos> bikes = (List<BikePos>) mp.get("bikes");

		List<Map<String, BikePos>> packs = maphelper.neighborCluster(bikes,
				distance);
		mp.remove("bikes");
		List<Point> result = maphelper.calcuClusterCenter(packs);
		mp.put("clusters", result);
		return mp;
	}

}
