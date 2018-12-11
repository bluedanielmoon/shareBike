package com.simu;

import java.util.Date;
import java.util.List;

import com.pojo.Dispatcher;
import com.pojo.SimuTask;
import com.pojo.Site;
import com.util.DateUtil;

public class Simulator implements Runnable{

	private SimuInfo infos;
	private TaskProducer tasker;
	private TaskRunner runner;
	private boolean inited = false;
	private boolean started = false;
	private boolean paused = false;
	private boolean ended = false;
	private Object pauser=new Object();
	private int count = 0;
	private int secondStep;
	
	
	@Override
	public void run() {
		runner.start();	
	}

	public SimuTask getTask(int dispatcherID ) {
		return runner.getTask(dispatcherID);
	}

	public List<SimuTask> init(List<Site> sites, List<Dispatcher> dispatchers, Date startTime, Date endTime, int timeSpeed) {
		infos = new SimuInfo();
		infos.setSites(sites);
		infos.setDispatchers(dispatchers);
		infos.setStartTime(startTime);
		infos.setEndTime(endTime);
		infos.setTimeSpeed(timeSpeed);
		
		tasker=new TaskProducer();
		runner = new TaskRunner();
		inited = true;

		secondStep = 1;		
		Date oneDay=DateUtil.parseToDay("2018_11_1");
		int startHour=7;
		int endHour=20;
		
		
		
		tasker.init(oneDay);
		
		List<SimuTask> startJobs=tasker.produceStartJobs(startHour,dispatchers);
		
		return runner.init(startJobs,tasker,startHour,endHour);
	}
	
	
}
