package com.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.init.Storage;
import com.pojo.Dispatcher;
import com.pojo.SimuTask;
import com.pojo.Site;
import com.simu.Simulator;

@Service
public class SimuServImpl {

	

	public Map<String, Object> initAndStart() {

		Simulator simulator = new Simulator();
		UUID sId = UUID.randomUUID();

		List<Site> sites = new ArrayList<>();
		List<Dispatcher> dispatchers = new ArrayList<>();
		Date startTime = new Date();
		Date endTime = new Date();
		int timeSpeed = 1;
		List<SimuTask>  initTasks=simulator.init(sites, dispatchers, startTime, endTime, timeSpeed);

		Storage.simulers.put(sId.toString(), simulator);
		Map<String, Object> result=new HashMap<>();
		result.put("uuid", sId);
		result.put("list", initTasks);
		return result;
	}


	public boolean pause(String id) {
		if (Storage.simulers.containsKey(id)) {
			Simulator sim = Storage.simulers.get(id);
			sim.pause();
			return true;
		}
		return false;
	}

	public boolean resume(String id) {
		if (Storage.simulers.containsKey(id)) {
			Simulator sim = Storage.simulers.get(id);
			sim.resume();
			return true;
		}
		return false;
	}

	public boolean stop(String id) {
		if (Storage.simulers.containsKey(id)) {
			Simulator sim = Storage.simulers.get(id);
			sim.stop();
			Storage.simulers.remove(id);
			return true;
		}
		return false;
	}

}
