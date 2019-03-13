package com.pojo;

/**
 * 用来记录模拟结果的类
 * @author daniel
 *
 */
public class RecordTask {
	
	private int taskID;
	
	private int taskType;
	
	private int taskTime;
	
	private Site targetSite;
	//0--upload,1--download
	private int loadType;
	
	private int loadNum;
	
	private int loadTime;
	
	private Lnglat start;
	
	private Lnglat end;
	
	private GaodePath path;
	
	private int moveTime;

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(int taskTime) {
		this.taskTime = taskTime;
	}

	public Site getTargetSite() {
		return targetSite;
	}

	public void setTargetSite(Site targetSite) {
		this.targetSite = targetSite;
	}

	public int getLoadType() {
		return loadType;
	}

	public void setLoadType(int loadType) {
		this.loadType = loadType;
	}

	public int getLoadNum() {
		return loadNum;
	}

	public void setLoadNum(int loadNum) {
		this.loadNum = loadNum;
	}

	public int getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(int loadTime) {
		this.loadTime = loadTime;
	}

	public Lnglat getStart() {
		return start;
	}

	public void setStart(Lnglat start) {
		this.start = start;
	}

	public Lnglat getEnd() {
		return end;
	}

	public void setEnd(Lnglat end) {
		this.end = end;
	}

	public GaodePath getPath() {
		return path;
	}

	public void setPath(GaodePath path) {
		this.path = path;
	}

	public int getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(int moveTime) {
		this.moveTime = moveTime;
	}

	public RecordTask() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
