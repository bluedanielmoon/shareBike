package com.simu;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pojo.Dispatcher;
import com.pojo.Site;

public class SimuInfo {

	private List<Site> sites;

	private List<Dispatcher> dispatchers;

	private Date startTime;

	private Date endTime;

	private int timeSpeed;

	public List<Site> getSites() {
		return sites;
	}

	public void setSites(List<Site> sites) {
		this.sites = sites;
	}

	public List<Dispatcher> getDispatchers() {
		return dispatchers;
	}

	public void setDispatchers(List<Dispatcher> dispatchers) {
		this.dispatchers = dispatchers;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getTimeSpeed() {
		return timeSpeed;
	}

	public void setTimeSpeed(int timeSpeed) {
		this.timeSpeed = timeSpeed;
	}
}
