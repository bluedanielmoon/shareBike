package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pojo.BusLine;
import com.pojo.GaodeLine;
import com.pojo.GaodeStop;
import com.pojo.Point;
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

	@GetMapping(value = "/rangebikePos")
	@ResponseBody
	public Map<String, Map<String, Object>> getRangeBike(@RequestParam String start, @RequestParam String end,
			@RequestParam(defaultValue = "4") int rows, @RequestParam(defaultValue = "4") int cols) {
		return mapServ.getDurationBikeData(start, end, rows, cols);
	}

	@GetMapping(value = "/heat")
	@ResponseBody
	public Map<String, Object> getHeatData(@RequestParam String time) {
		try {
			return mapServ.getHeatMap(time);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		;
		return null;
	}

	@GetMapping(value = "/rangeheat")
	@ResponseBody
	public List<Map<String, Object>> getRangeHeatData(@RequestParam String start, @RequestParam String end) {
		return mapServ.getDurationHeat(start, end);
	}

	@GetMapping(value = "/daterange")
	@ResponseBody
	public long[] getDataRange() {
		return mapServ.getFileRange();
	}

	@GetMapping(value = "/cluster")
	@ResponseBody
	public List<Point> getClusterData(@RequestParam String time, @RequestParam int distance) {
		return mapServ.getClusterMap(time, distance);
	}
	
	@GetMapping(value = "/rangecluster")
	@ResponseBody
	public List<Point>  getRangeClusterData(@RequestParam String start, @RequestParam String end, @RequestParam int distance) {
		return mapServ.getDurationClusterMap(start,end, distance);
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

	@PostMapping(value = "/busLine", consumes = "application/json")
	@ResponseBody
	public boolean saveData(@RequestBody GaodeLine line) {
		String fileName = FilesUtil.DEFAULT_BIKE_FILE + "allLines5.txt";
		FilesUtil.writeObjectToFile(fileName, line, GaodeLine.class);
		return true;

	}

}
