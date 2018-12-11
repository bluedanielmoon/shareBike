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
import com.pojo.MoveTask;
import com.pojo.SimuTask;
import com.pojo.Site;

public class TaskRunner {
	private LinkedList<SimuTask> states = new LinkedList<>();
	private Map<Integer, LinkedList<SimuTask>> recorder = new HashMap<>();
	private int nowSeconds = 0;
	private int startHour = 0;
	private int nowHour = 0;
	private int endSeconds = 0;
	private TaskProducer tasker;

	public List<SimuTask> init(List<SimuTask> tasks,TaskProducer tasker,int startHour,int endHour) {
		tasks.sort(new Comparator<SimuTask>() {
			@Override
			public int compare(SimuTask o1, SimuTask o2) {		
				return Integer.compare(o2.getWorkTime(), o1.getWorkTime());
			};
		});
		for(SimuTask task:tasks) {
			states.push(task);
			LinkedList<SimuTask> dispTask=new LinkedList<>();
//			dispTask.add(task);
			recorder.put(task.getDispatcher().getId(), dispTask);
		}
		this.tasker=tasker;
		this.startHour=startHour;
		nowHour=startHour;
		endSeconds=(endHour-startHour)*3600;
		return states;
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
		nowHour = nowSeconds / 3600 + startHour;
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

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			printState();
			moveNext();
		}
	}

	public void moveNext() {
		// 取出任务列表中第一个完成的
		SimuTask task = states.poll();
		boolean goOn = refreshTime(task.getWorkTime());
		if (task.getTaskType() == State.MOVE_TASK) {
			MoveTask moveTask = (MoveTask) task;
			Dispatcher dispatcher = moveTask.getDispatcher();
			// 更新disp位置
			Site finishLocat = moveTask.getTarget();
			dispatcher.setLng(finishLocat.getLng());
			dispatcher.setLat(finishLocat.getLat());
			if (goOn) {
				// 安排装卸任务
				// System.out.println("安排装卸任务");
				LoadTask loadTask = tasker.assignLoadTask(nowHour, nowSeconds, dispatcher, moveTask);
				if (loadTask.getWorkTime() == 0) {
					// System.out.println("另外安排移动任务");
					MoveTask keepMove = tasker.assignMoveTask(nowHour, moveTask.getTarget(), dispatcher, State.LOAD);
					pushTaskByWorkTime(keepMove);
				} else {
					pushTaskByWorkTime(loadTask);
				}

			}
		} else {
			LoadTask loadTask = (LoadTask) task;
			// 因为存在单车数量变化，必须对地图所有有影响的站点进行更新
			tasker.refreshSites(nowHour, nowSeconds, loadTask);
			if (goOn) {
				// 安排移动任务
				// System.out.println("安排移动任务");
				MoveTask moveTask = tasker.assignMoveTask(nowHour, loadTask.getSite(), loadTask.getDispatcher(),
						State.UNLOAD);
				pushTaskByWorkTime(moveTask);

			}
		}
	}

	private void printState() {

		int moveCount = 0;
		int LoadCount = 0;
		for (SimuTask task : states) {
			if (task.getTaskType() == State.MOVE_TASK) {
				moveCount++;
			} else {
				LoadCount++;
			}
		}
		double secods = nowSeconds + startHour * 3600;

		int hour = (int) (secods / 3600);
		int minute = (int) ((secods % 3600) / 60);
		int seconds = (int) ((secods % 3600) % 60);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, seconds);
		SimpleDateFormat simpleDat = new SimpleDateFormat("HH:mm:ss");
//		System.out.println("当前时间"+simpleDat.format(calendar.getTime()));
//		System.out.println("共有任务："+states.size()+" ,移动任务："+moveCount+" ,装卸任务："+LoadCount);
//		
//		for(SimuTask task:states) {
//			System.out.println(task.getTaskType()+"---   "+task.getWorkTime());
//		}
	}

	private void pushTaskByWorkTime(SimuTask task) {
		int time=task.getWorkTime();
		
		for(int i=0;i<states.size();i++) {
			if(time>=states.get(i).getWorkTime()) {
				if(i==(states.size()-1)) {
					states.add(task);
					break;
				}else {
					continue;
				}
			}else {
				states.add(i,task);
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

	private  int calcuDistance(int type, int seconds) {
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
	

//	/**
//	 * 对任务中的每个调度力量，根据其行进线路和当前位置，时钟，模拟出下一个行进到的位置
//	 * @param task
//	 * @param seconds
//	 * @return
//	 */
//	private Lnglat setNextPos(SimuTask task, int seconds) {
//
//		Dispatcher dispatcher = task.getDispatcher();
//		
//
//		//计算可移动的距离
//		int distance = calcuDistance(dispatcher.getType(), seconds);
//		
//		//当前调度车的位置
//		Lnglat nowPos = new Lnglat(dispatcher.getLng(), dispatcher.getLat());
//
//		List<Lnglat> paths = task.getPath().getPaths();
//
//		int count = task.getNowCount();
//		if (count >= paths.size()) {
//			return paths.get(paths.size() - 1);
//		}
//		Lnglat pos = nowPos;
//		Lnglat nextResult = null;
//
//		while (distance > 0) {
//			if (count == paths.size() - 1) {
//				nextResult = paths.get(count);
//				break;
//			}
//			Lnglat next = paths.get(count + 1);
//			int step = CoordsUtil.calcuDist(pos.getLng(), pos.getLat(), next.getLng(), next.getLat());
//			if (distance > step) {
//
//				distance = distance - step;
//				pos = next;
//				count++;
//			} else {
//				Lnglat last = paths.get(count);
//				double ratio = (double) distance / step;
//
//				nextResult = CoordsUtil.getRatioPos(last, next, ratio);
//				break;
//			}
//		}
//		
//		dispatcher.setLng(nextResult.getLng());
//		dispatcher.setLat(nextResult.getLat());
//		task.setNowCount(count);
//		return nextResult;
//
//	}
//
//	public static void main(String[] args) {
//		long now = System.currentTimeMillis();
//		int seco = 1;
//
//		TaskRunner taskRunner = new TaskRunner();
//		List<SimuTask> list = new ArrayList<>();
//		SimuTask tSimuTask = new SimuTask();
//		GaodePath path = new GaodePath();
//		tSimuTask.setPath(path);
//		list.add(tSimuTask);
//		taskRunner.moveStep(list, now, seco);
//	}
}
