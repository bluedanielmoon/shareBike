package com.pojo;

public class LoadTask extends SimuTask{
	
	private Site targetSite;
	//0--upload,1--download
	private int loadType;
	
	private int loadNum;
	
	private int loadTime;
	
	private Lnglat start;
	
	private Lnglat end;
	
	private GaodePath path;
	
	private int moveTime;
	

	public LoadTask() {
		super();
	}
	

	public LoadTask(Site targetSite, int loadType, int loadNum, int loadTime, Lnglat start, Lnglat end, GaodePath path,
			int moveTime) {
		super();
		this.targetSite = targetSite;
		this.loadType = loadType;
		this.loadNum = loadNum;
		this.loadTime = loadTime;
		this.start = start;
		this.end = end;
		this.path = path;
		this.moveTime = moveTime;
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

	@Override
	public String toString() {
		return "LoadTask [targetSite=" + targetSite + ", loadType=" + loadType + ", loadNum=" + loadNum + ", loadTime="
				+ loadTime + ", start=" + start + ", end=" + end + ", path=" + path + ", moveTime=" + moveTime + "]";
	}
	
	
	
}
