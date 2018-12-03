package com.pojo;

public class DispMove {
	
	private Lnglat start;
	private Lnglat end;
	private Lnglat position;
	private int timeSpan;
	//0--移动,1--停止
	private int type;
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
	public Lnglat getPosition() {
		return position;
	}
	public void setPosition(Lnglat position) {
		this.position = position;
	}
	public int getTimeSpan() {
		return timeSpan;
	}
	public void setTimeSpan(int timeSpan) {
		this.timeSpan = timeSpan;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
