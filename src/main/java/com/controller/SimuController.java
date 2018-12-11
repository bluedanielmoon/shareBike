package com.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

<<<<<<< HEAD
import com.pojo.SimuTask;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.serviceImpl.SimuServImpl;

@RestController
@RequestMapping(value = "/simu")
public class SimuController {

	@Autowired
	private SimuServImpl simuServ;

<<<<<<< HEAD
	@PostMapping(value = "/start",consumes = "application/json")
	@ResponseBody
	public Map<String, Object> startSimu(@RequestBody String carPos) {
		return simuServ.initAndStart(carPos);
=======
	@GetMapping(value = "/start")
	@ResponseBody
	public Map<String, Object> startSimu() {
		return simuServ.initAndStart();
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
	}
	
//	@GetMapping(value = "/tasks")
//	@ResponseBody
//	public List<SimuTask> getTasks(@RequestParam String simuID) {
//		return simuServ.getTasks(simuID);
//	}
<<<<<<< HEAD
	
	@GetMapping(value = "/next")
	@ResponseBody
	public SimuTask getNextJob(@RequestParam int dispID,@RequestParam String simuID) {
		return simuServ.getNextJob(dispID,simuID);
	}
	
	@GetMapping(value = "/config")
	@ResponseBody
	public Map<String, Integer> getconfigs() {
		return simuServ.getConfigs();
	}
	
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

	@GetMapping(value = "/pause")
	@ResponseBody
	public boolean pauseSimu(@RequestParam String simuID) {
		return simuServ.pause(simuID);
	}

	@GetMapping(value = "/resume")
	@ResponseBody
	public boolean resumeSimu(@RequestParam String simuID) {
		return simuServ.resume(simuID);
	}

	@GetMapping(value = "/stop")
	@ResponseBody
	public boolean stopSimu(@RequestParam String simuID) {
		return simuServ.stop(simuID);
	}

}
