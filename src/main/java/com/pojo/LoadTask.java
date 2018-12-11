package com.pojo;

public class LoadTask extends SimuTask{
	
	private Site site;
	//0--upload,1--download
	private int type;
	
	private int loadNum;
	
	private int workTime;

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLoadNum() {
		return loadNum;
	}

	public void setLoadNum(int loadNum) {
		this.loadNum = loadNum;
	}

	public int getWorkTime() {
		return workTime;
	}

	public void setWorkTime(int workTime) {
		this.workTime = workTime;
	}
<<<<<<< HEAD

	@Override
	public String toString() {
		return "LoadTask [site=" + site + ", type=" + type + ", loadNum=" + loadNum + ", workTime=" + workTime + "]";
	}
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
	
	
	
}
