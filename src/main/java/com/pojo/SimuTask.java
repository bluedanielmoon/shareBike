package com.pojo;

public class SimuTask {
	private int id;
	private Dispatcher dispatcher;
	private int workTime;
	private int taskType;
	

	public SimuTask() {
		super();
	}

	
	public SimuTask(int id, Dispatcher dispatcher, int workTime, int taskType) {
		super();
		this.id = id;
		this.dispatcher = dispatcher;
		this.workTime = workTime;
		this.taskType = taskType;
	}


	public Dispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public int getWorkTime() {
		return workTime;
	}

	public void setWorkTime(int workTime) {
		this.workTime = workTime;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}


	
}
