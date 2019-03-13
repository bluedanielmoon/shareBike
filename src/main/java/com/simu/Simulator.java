package com.simu;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import com.pojo.Dispatcher;
import com.pojo.SimuTask;

public class Simulator implements Runnable {

	private SimuManager manager;
	private TaskProducer tasker;
	private TaskRunner runner;
	private String sId;

	public Simulator(String sId,SimuManager manager) {
		this.sId = sId;
		this.manager=manager;
	}

	@Override
	public void run() {
		runner.start();
	}

	public void callEnd() {
		System.out.println("一次模拟结束");
		if (manager.simulers.containsKey(sId)) {
			manager.runQueue.poll();
		}

	}

	public SimuTask getTask(int dispatcherID) {
		return runner.getTask(dispatcherID);
	}

	public List<SimuTask> init(List<Dispatcher> dispatchers, Date date, int startHour, int endHour
			,boolean needStart,Path filePath,int simuID) {
		tasker = new TaskProducer();
		runner = new TaskRunner();

		tasker.init(date, startHour, endHour);
		List<SimuTask> startJobs = tasker.getStartJobs(dispatchers, startHour);
		runner.init(this, startJobs, tasker, dispatchers, startHour, endHour,needStart,filePath,simuID);

		return startJobs;
	}

}
