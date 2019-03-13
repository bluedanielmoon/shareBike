package com.serviceImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.init.State;
import com.pojo.Dispatcher;
import com.pojo.SimuTask;
import com.service.DispatcherServ;
import com.simu.SimuManager;
import com.simu.SimuRecorder;
import com.simu.Simulator;
import com.simu.Storage;
import com.util.DateUtil;

@Service
public class SimuServImpl {

	@Autowired
	private DispatcherServ dispatcherServ;

	public Map<String, Integer> getConfigs() {
		Map<String, Integer> configs = new HashMap<>();
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

	public Map<String, Object> initAndStart(String initData) {

		
		
		// 提取信息
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		try {
			root = mapper.readTree(initData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int simuTimes = root.get("simuTimes").asInt();
		List<Dispatcher> dispatchers = setDispatPos(root.get("carPos"));

		String s_date = root.get("date").asText();
		Date date = DateUtil.parseToDay(s_date);
		int startHour = root.get("startHour").asInt();
		int endHour = root.get("endHour").asInt();
		
		State.SITE_CHOOSE_RATIO=root.get("chooseRatio").asInt();
		State.SITE_CHOOSE_BEST=root.get("chooseBest").asInt();
		
		List<String> simuIDs=new ArrayList<>();
		String mangeID = UUID.randomUUID().toString();
		
		SimuRecorder recorder=new SimuRecorder();
		Path filePath=recorder.buildFile(date, startHour, endHour, simuTimes);
		if (filePath==null) {
			return null;
		}
		
		SimuManager simuManager=new SimuManager(dispatchers,date,startHour,endHour,filePath);
		for (int i = 0; i < simuTimes; i++) {

			UUID sId = UUID.randomUUID();
			String simuID = sId.toString();
			Simulator simulator = new Simulator(simuID,simuManager);
			simuIDs.add(simuID);
			
			simuManager.simulers.put(simuID, simulator);
			try {
				simuManager.waitQueue.put(simulator);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Simulator firstSimuLator=simuManager.waitQueue.poll();
		try {
			simuManager.runQueue.put(firstSimuLator);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 初始化模拟
		List<SimuTask> initTasks = firstSimuLator.init(dispatchers, date, startHour, 
				endHour,true,filePath,1);
		System.out.println("初始化结束------");
		// 开启线程
		new Thread(firstSimuLator).start();
		
		new Thread(simuManager).start();
		
		// 把线程模拟放入模拟池，并返回结果
		Storage.simulers.put(mangeID, simuManager);
		Map<String, Object> result = new HashMap<>();
		result.put("manage", mangeID);
		result.put("uuid", simuIDs);
		result.put("list", initTasks);
		return result;
	}

	public SimuTask getNextJob(int dispID, String manageID, String simuID) {

		if (Storage.simulers.containsKey(manageID)) {
			SimuManager manager = Storage.simulers.get(manageID);
			if (manager.simulers.containsKey(simuID)) {
				Simulator sim=manager.simulers.get(simuID);
				SimuTask task = sim.getTask(dispID);
				return task;
			}
			
		}
		return null;
	}

	private List<Dispatcher> setDispatPos(JsonNode cars) {

		List<Dispatcher> dispatchers = dispatcherServ.getAllDispatchers();
		List<Dispatcher> checked = new ArrayList<>();
		if (cars == null) {
			return checked;
		}
		for (int i = 0; i < cars.size(); i++) {
			JsonNode carInfo = cars.get(i);
			int id = carInfo.get("id").asInt();
			double lng = carInfo.get("lng").asDouble();
			double lat = carInfo.get("lat").asDouble();
			for (Dispatcher dispatcher : dispatchers) {
				if (dispatcher.getId() == id) {
					dispatcher.setLng(lng);
					dispatcher.setLat(lat);
					dispatcher.setStorage(0);
					checked.add(dispatcher);
					break;
				}
			}
		}
		return checked;

	}

}
