package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pojo.BikeHeader;
import com.pojo.Varia;
import com.service.AnayServ;

@Controller
@RequestMapping(value = "/anay")
public class AnayController {

	@Autowired
	private AnayServ anyServ;

	@GetMapping(value = "/varia")
	@ResponseBody
	public List<Varia> getVaria(@RequestParam String time, @RequestParam int dist,@RequestParam int type) {
		return anyServ.getVariaData(time, dist,type);
	}
	
	@GetMapping(value = "/stack")
	@ResponseBody
	public List<BikeHeader> getBikeCount() {
		return anyServ.getBikeInfo();
	}
	
	@GetMapping(value = "/daily")
	@ResponseBody
	public Map<Integer, List<Map<String, Object>>> getBikeDaily() {
		return anyServ.getBikeDaily();
	}

}
