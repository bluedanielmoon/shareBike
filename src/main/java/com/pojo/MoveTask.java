package com.pojo;

public class MoveTask extends SimuTask{
	private Lnglat start;
	private Lnglat end;
	private Site target;
	private GaodePath path;
	
	
	public MoveTask() {
		super();
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
	
	public Site getTarget() {
		return target;
	}
	public void setTarget(Site target) {
		this.target = target;
	}
<<<<<<< HEAD

	@Override
	public String toString() {
		return "MoveTask [start=" + start + ", end=" + end + ", target=" + target + ", path=" + path + "]";
	}
	
	
	
=======
	
	
	public static void main(String[] args) {
		MoveTask moveTask=new MoveTask();
		moveTask.getWorkTime();
	}
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
	
}
