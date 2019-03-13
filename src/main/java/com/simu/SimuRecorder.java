package com.simu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.init.State;
import com.pojo.LoadTask;
import com.pojo.RecordTask;
import com.pojo.SimuTask;
import com.pojo.WaitTask;
import com.util.DateUtil;
import com.util.MapperUtil;

public class SimuRecorder {
	public static void main(String[] args) {

	}

	public Path buildFile( Date date, int startHour, int endHour, int simuTimes) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd(HH:mm)");
		
		Path path = Paths.get("/Users/daniel/projects/simuData/" + timeFormat.format(new Date()) + "_"
				+ dateFormat.format(date) + "_" + startHour + "_" + endHour + "_" + simuTimes);
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
				return path;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	public void writeResult(Map<Integer, List<SimuTask>> recorder, Path filePath, int simuID) {
		System.out.println("后台数据模拟结束，准备将结果写入文件");
		Set<Integer> carIDs = recorder.keySet();
		Map<Integer, List<RecordTask>> result = new HashMap<>();
		for (Integer carID : carIDs) {
			List<SimuTask> tasks = recorder.get(carID);
			List<RecordTask> resultTasks = new ArrayList<>();
			int taskCount = 0;
			for (SimuTask task : tasks) {

				RecordTask recordTask = new RecordTask();
				recordTask.setTaskID(taskCount++);
				recordTask.setTaskTime(task.getWorkTime());
				recordTask.setTaskType(task.getTaskType());
				if (task.getTaskType() == State.LOAD_TASK) {

					LoadTask lTask = (LoadTask) task;
					recordTask.setStart(lTask.getStart());
					recordTask.setEnd(lTask.getEnd());
					recordTask.setLoadNum(lTask.getLoadNum());
					recordTask.setLoadTime(lTask.getLoadTime());
					recordTask.setLoadType(lTask.getLoadType());
					recordTask.setMoveTime(lTask.getMoveTime());
					recordTask.setTaskTime(lTask.getLoadTime()+lTask.getMoveTime());
					recordTask.setPath(lTask.getPath());
					recordTask.setTargetSite(lTask.getTargetSite());
				} else if (task.getTaskType() == State.WAIT_TASK) {
					WaitTask waitTask = (WaitTask) task;

				}
				resultTasks.add(recordTask);
			}
			result.put(carID, resultTasks);
		}
		System.out.println(filePath.toString());
		MapperUtil.writeMapListData(filePath.toString() + "/" + simuID + ".txt", result, Integer.class,
				RecordTask.class);

	}
}
