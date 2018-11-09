package com.controller;

import java.util.List;
import java.util.Map;

import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.BikePos;
import com.pojo.BusLine;
import com.pojo.ClusterPt;
import com.pojo.GaodeLine;
import com.pojo.GaodeStop;
import com.pojo.Point;
import com.pojo.Test;
import com.serviceImpl.MapServImpl;
import com.util.FilesUtil;

@Controller
@RequestMapping(value = "/map")
public class MapController {

	@Autowired
	private MapServImpl mapServ;
	
	@GetMapping(value = "/bikePos")
	@ResponseBody
	public Map<String, Object> getOrignialBike(@RequestParam String time) {
		return mapServ.getBikeData(time);
	}

	@GetMapping(value = "/heat")
	@ResponseBody
	public Map<String, Object> getHeatData(@RequestParam String time) {

		try {
			return mapServ.getHeatMap(time);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		};
		return null;
	}

	@GetMapping(value = "/cluster")
	@ResponseBody
	public Map<String, Object> getClusterData(@RequestParam String time,@RequestParam int distance) {
		return mapServ.getClusterMap(time,distance);
	}
	
	@GetMapping(value = "/lines")
	@ResponseBody
	public List<BusLine> getLinesData() {
		
		return FilesUtil.readFromFile("./line.txt", BusLine.class);
		
	}
	
	@GetMapping(value = "/busStop")
	@ResponseBody
	public List<GaodeStop> getBusStops() {
		return FilesUtil.readFromFile(FilesUtil.BUS_STOP_FILE, GaodeStop.class);
		
	}
	
	@PostMapping(value = "/busLine",consumes="application/json")
	@ResponseBody
	public boolean saveData(@RequestBody GaodeLine line) {
		String fileName=FilesUtil.DEFAULT_BIKE_FILE+"allLines5.txt";
		FilesUtil.writeObjectToFile(fileName,line,GaodeLine.class);
		return true;
		
	}
	
	@PostMapping(value = "/test",consumes="application/json")
	@ResponseBody
	public boolean saveData(@RequestBody Test bsline) {
		ObjectMapper mp=new ObjectMapper();
		System.out.println(bsline);
		return true;
		
	}
}
