package com.simu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.init.State;
import com.pojo.Dispatcher;
import com.pojo.Lnglat;
import com.pojo.LoadTask;
import com.pojo.SimuTask;
import com.pojo.WaitTask;

public class TaskRunner {
	private LinkedList<SimuTask> states = new LinkedList<>();
	private Map<Integer, LinkedList<SimuTask>> recorder = new HashMap<>();
	private int nowSeconds = 0;
	private int pastHourSeconds = 0;
	private int startHour = 0;
	private int nowHour = 0;
	private int endSeconds = 0;
	private TaskProducer tasker;
	private List<Dispatcher> dispatchers;

	public List<SimuTask> init(List<SimuTask> tasks, TaskProducer tasker, List<Dispatcher> dispatchers, int startHour,
			int endHour) {
		tasks.sort(new Comparator<SimuTask>() {
			@Override
			public int compare(SimuTask o1, SimuTask o2) {
				return Integer.compare(o2.getWorkTime(), o1.getWorkTime());
			}
		});
		for (SimuTask task : tasks) {
			states.push(task);
			LinkedList<SimuTask> dispTask = new LinkedList<>();
			dispTask.add(task);
			recorder.put(task.getDispatcher().getId(), dispTask);
		}
		for (Dispatcher disp : dispatchers) {
			disp.setStorage(0);
		}
		this.dispatchers = dispatchers;
		this.tasker = tasker;
		this.startHour = startHour;
		nowHour = startHour;
		endSeconds = (endHour - startHour) * 3600;
		return states;
	}

	public void moveNext() {
		// 取出任务列表中第一个完成的
		printState();
		// printDispatchers();
		SimuTask task = states.poll();
		boolean goOn = refreshTime(task.getWorkTime());
		finshTask(task);
		if (goOn) {
			Dispatcher dispatcher = task.getDispatcher();
			SimuTask assignTask = tasker.assignTask(nowHour, pastHourSeconds, dispatcher, task,1);
			pushTaskByWorkTime(assignTask);
		}
	}

	public SimuTask getTask(int dispID) {
		System.out.println("调度车id:" + dispID + ",任务列表" + recorder.get(dispID).size());

		LinkedList<SimuTask> list = recorder.get(dispID);
		for (SimuTask t : list) {
			System.out.print(t.getTaskType() + "---" + t.getWorkTime() + "   \n");
		}
		SimuTask task = null;
		if (list.size() > 0) {
			task = list.poll();
		}
		return task;
	}

	private boolean refreshTime(int secondsPast) {
		nowSeconds += secondsPast;

		int lasthour = nowHour;
		nowHour = nowSeconds / 3600 + startHour;

		// 经过一次时间更新后，小时数加一，变动预测的时间段
		if (lasthour != nowHour) {
			tasker.changeNextHour(nowHour);
		}
		pastHourSeconds = nowSeconds % 3600;
		for (SimuTask task : states) {
			task.getWorkTime();
			task.setWorkTime(task.getWorkTime() - secondsPast);
		}
		if (nowSeconds < endSeconds) {
			return true;
		} else {
			return false;
		}
	}

	public void start() {
		System.out.println("开始模拟");
		while (states.size() > 0) {
			moveNext();
		}
	}

	public void finshTask(SimuTask task) {
		if (task.getTaskType() == State.LOAD_TASK) {
			LoadTask loadTask = (LoadTask) task;
			// 因为存在单车数量变化，必须对地图所有有影响的站点进行更新
			tasker.refreshSites(nowHour, pastHourSeconds, loadTask);
			Dispatcher dispatcher = loadTask.getDispatcher();

			Lnglat end = loadTask.getEnd();
			dispatcher.setLng(end.getLng());
			dispatcher.setLat(end.getLat());

			if (loadTask.getLoadType() == State.LOAD) {
				dispatcher.setStorage(dispatcher.getStorage() + loadTask.getLoadNum());
			} else if (loadTask.getLoadType() == State.UNLOAD){
				dispatcher.setStorage(dispatcher.getStorage() - loadTask.getLoadNum());
			}
		}
	}

	public void printDispatchers() {
		for (Dispatcher disp : dispatchers) {
			System.out.println(disp.getId());
			System.out.println("坐标" + disp.getLng() + "," + disp.getLat());
			System.out.println("储量" + disp.getStorage());

		}
	}

	private void printState() {
		double secods = nowSeconds + startHour * 3600;

		int hour = (int) (secods / 3600);
		int minute = (int) ((secods % 3600) / 60);
		int seconds = (int) ((secods % 3600) % 60);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, seconds);
		SimpleDateFormat simpleDat = new SimpleDateFormat("HH:mm:ss");
		System.out.println("当前时间" + simpleDat.format(calendar.getTime()));

		for (SimuTask task : states) {
			if (task.getTaskType()==State.LOAD_TASK) {
				LoadTask loadTask=(LoadTask) task;
				Dispatcher dispatcher=loadTask.getDispatcher();
				System.out.println("调度车："+dispatcher.getId()+"，装货："+dispatcher.getStorage()
						+ "下一站："+loadTask.getTargetSite().getId()+",预计用时："+task.getWorkTime());
			
			}else if (task.getTaskType()==State.WAIT_TASK) {
				WaitTask waitTask=(WaitTask) task;
				Dispatcher dispatcher=waitTask.getDispatcher();
				System.out.println("调度车："+dispatcher.getId()+" 等待："+waitTask.getWorkTime());
			
			}
		}
		System.out.println("*******************************************");
	}

	private void pushTaskByWorkTime(SimuTask task) {
		int dispId = task.getDispatcher().getId();
		LinkedList<SimuTask> lists = recorder.get(dispId);
		lists.add(task);
		int time = task.getWorkTime();

		for (int i = 0; i < states.size(); i++) {

			if (time >= states.get(i).getWorkTime()) {
				if (i == (states.size() - 1)) {
					states.add(task);
					break;
				} else {
					continue;
				}
			} else {
				states.add(i, task);
				break;
			}
		}
	}

	private void calcuLoadWorkTime(LoadTask loadTask) {
		int loadNum = loadTask.getLoadNum();
		int loadTime = State.LOAD_UNIT_TIME * loadNum;
		// double wastTime=loadTime*(1+0.3);
		loadTask.setWorkTime(loadTime);
	}

	private int calcuDistance(int type, int seconds) {
		int distance = 0;
		if (type == State.TRUCK_TYPE) {
			distance = State.TRUCK_SPEED * seconds;
		} else if (type == State.TRICYCLE_TYPE) {
			distance = State.TRICYCLE_SPEED * seconds;
		} else if (type == State.MAN_TYPE) {
			distance = State.MAN_SPEED * seconds;
		}
		return distance;
	}
}
