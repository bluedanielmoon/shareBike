package com.simu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pojo.Dispatcher;
import com.pojo.SimuTask;
import com.pojo.Site;
import com.util.DateUtil;

public class Simulator {

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
	private List<SimuTask> tasks;
	
	public void moveOneTask() {
		runner.moveNext();
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
		tasker.initJobs(oneDay);
		tasks= tasker.produceStartJobs(startHour);
		runner.init(tasks,tasker,startHour);
		return tasks;
	}
	
	public static void main(String[] args) {
		Simulator simulator = new Simulator();

		List<Site> sites = new ArrayList<>();
		List<Dispatcher> dispatchers = new ArrayList<>();
		Date startTime = new Date();
		Date endTime = new Date();
		int timeSpeed = 1;
		simulator.init(sites, dispatchers, startTime, endTime, timeSpeed);
		try {
			Thread.sleep(5000);
			simulator.pause();
			Thread.sleep(5000);
			simulator.resume();

			Thread.sleep(3000);
			simulator.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void run() {
		
		long startSecond = System.currentTimeMillis() / 1000;
		System.out.println("模拟开始,开始时间：" + startSecond);

		while (true) {
			if (paused) {

				try {
					synchronized (pauser) {
						pauser.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (ended) {
				break;
			}
			try {
				Thread.sleep(100000);
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		System.out.println("模拟结束");
	}
	public void pause() {
		synchronized (pauser) {
			paused = true;
			System.out.println("模拟暂停");
		}

	}

	public void resume() {
		synchronized (pauser) {
			paused = false;
			pauser.notify();
			System.out.println("模拟恢复");
		}

	}

	public void stop() {
		synchronized (pauser) {
			ended = true;
		}
	}

}
