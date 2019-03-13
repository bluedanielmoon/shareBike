package com.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pojo.SimuTask;
import com.serviceImpl.SimuServImpl;

@RestController
@RequestMapping(value = "/simu")
public class SimuController {

	@Autowired
	private SimuServImpl simuServ;

	@PostMapping(value = "/start",consumes = "application/json")
	@ResponseBody
	public Map<String, Object> startSimu(@RequestBody String carPos) {
		
		
		return simuServ.initAndStart(carPos);
	}
	
	@GetMapping(value = "/next")
	@ResponseBody
	public SimuTask getNextJob(@RequestParam int dispID,@RequestParam String manageID,@RequestParam String simuID) {
		return simuServ.getNextJob(dispID,manageID,simuID);
	}
	
	@GetMapping(value = "/config")
	@ResponseBody
	public Map<String, Integer> getconfigs() {
		return simuServ.getConfigs();
	}
	

}
