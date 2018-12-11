package com.serviceImpl;

<<<<<<< HEAD
import java.io.IOException;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.init.State;
=======
import org.springframework.stereotype.Service;

>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.init.Storage;
import com.pojo.Dispatcher;
import com.pojo.SimuTask;
import com.pojo.Site;
<<<<<<< HEAD
import com.service.DispatcherServ;
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.simu.Simulator;

@Service
public class SimuServImpl {

<<<<<<< HEAD
	@Autowired
	private DispatcherServ dispatcherServ;
	
	public Map<String, Integer> getConfigs() {
		Map<String, Integer> configs=new HashMap<>();
		configs.put("truckType", State.TRUCK_TYPE);
		configs.put("truckSpeed", State.TRUCK_SPEED);
		configs.put("truckCapacity", State.TRUCK_CAPACITY);
		configs.put("tricycleType", State.TRICYCLE_TYPE);
		configs.put("tricycleSpeed", State.TRICYCLE_SPEED);
		configs.put("tricycleCapacity", State.TRICYCLE_CAPACITY);
		configs.put("manType", State.MAN_TYPE);
		configs.put("manSpeed", State.MAN_SPEED);
		configs.put("manCapacity", State.MAN_CAPACITY);
		configs.put("loadUnitTime", State.LOAD_UNIT_TIME);
		return configs;
	}

	public Map<String, Object> initAndStart(String carPos) {
=======
	

	public Map<String, Object> initAndStart() {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

		Simulator simulator = new Simulator();
		UUID sId = UUID.randomUUID();

		List<Site> sites = new ArrayList<>();
<<<<<<< HEAD
		
		List<Dispatcher> dispatchers=setDispatPos(carPos);
		
=======
		List<Dispatcher> dispatchers = new ArrayList<>();
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		Date startTime = new Date();
		Date endTime = new Date();
		int timeSpeed = 1;
		List<SimuTask>  initTasks=simulator.init(sites, dispatchers, startTime, endTime, timeSpeed);

<<<<<<< HEAD
		new Thread(simulator).start();
		
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		Storage.simulers.put(sId.toString(), simulator);
		Map<String, Object> result=new HashMap<>();
		result.put("uuid", sId);
		result.put("list", initTasks);
		return result;
	}
<<<<<<< HEAD
	
	public SimuTask getNextJob(int dispID,String simuID) {
		
		if (Storage.simulers.containsKey(simuID)) {
			Simulator sim = Storage.simulers.get(simuID);
			SimuTask task=sim.getTask(dispID);
			System.out.println("取一个工作");
			System.out.println(task);
			return task;
		}
		return null;
	}
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b


	public boolean pause(String id) {
		if (Storage.simulers.containsKey(id)) {
			Simulator sim = Storage.simulers.get(id);
<<<<<<< HEAD
			//sim.pause();
=======
			sim.pause();
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
			return true;
		}
		return false;
	}

	public boolean resume(String id) {
		if (Storage.simulers.containsKey(id)) {
			Simulator sim = Storage.simulers.get(id);
<<<<<<< HEAD
			//sim.resume();
=======
			sim.resume();
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
			return true;
		}
		return false;
	}

	public boolean stop(String id) {
		if (Storage.simulers.containsKey(id)) {
			Simulator sim = Storage.simulers.get(id);
<<<<<<< HEAD
			//sim.stop();
=======
			sim.stop();
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
			Storage.simulers.remove(id);
			return true;
		}
		return false;
	}

<<<<<<< HEAD
	
	private List<Dispatcher> setDispatPos(String data) {
		
		List<Dispatcher> dispatchers=dispatcherServ.getAllDispatchers();
		ObjectMapper mapper=new ObjectMapper();
		List<Dispatcher> checked=new ArrayList<>();
		try {
			JsonNode root=mapper.readTree(data);
			if(root==null) {
				return checked;
			}
			JsonNode cars=root.get("carPos");
			if(cars==null) {
				return checked;
			}
			
			for(int i=0;i<cars.size();i++) {
				JsonNode carInfo=cars.get(i);
				int id=carInfo.get("id").asInt();
				double lng=carInfo.get("lng").asDouble();
				double lat=carInfo.get("lat").asDouble();
				for(Dispatcher dispatcher:dispatchers) {
					if(dispatcher.getId()==id) {
						dispatcher.setLng(lng);
						dispatcher.setLat(lat);
						dispatcher.setStorage(0);
						checked.add(dispatcher);
						break;
					}
				}
			}
			return checked;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	




	
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
}
