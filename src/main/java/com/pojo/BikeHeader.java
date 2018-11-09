package com.pojo;

import java.util.Date;

import org.springframework.stereotype.Repository;
@Repository
public class BikeHeader {
	private int bikeCount;
	private int jobsTotal;
	private int jobsLeft;
	private Date startTime;
	private Date endTime;
	private BikeArea bikeRec;
	private Weather weather;
	public BikeHeader() {
		super();
	}
	public BikeHeader(int bikeCount, int jobsTotal, int jobsLeft,
			Date startTime, Date endTime, BikeArea bikeRec, Weather weather) {
		super();
		this.bikeCount = bikeCount;
		this.jobsTotal = jobsTotal;
		this.jobsLeft = jobsLeft;
		this.startTime = startTime;
		this.endTime = endTime;
		this.bikeRec = bikeRec;
		this.weather = weather;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bikeCount;
		result = prime * result + ((bikeRec == null) ? 0 : bikeRec.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + jobsLeft;
		result = prime * result + jobsTotal;
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((weather == null) ? 0 : weather.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BikeHeader other = (BikeHeader) obj;
		if (bikeCount != other.bikeCount)
			return false;
		if (bikeRec == null) {
			if (other.bikeRec != null)
				return false;
		} else if (!bikeRec.equals(other.bikeRec))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (jobsLeft != other.jobsLeft)
			return false;
		if (jobsTotal != other.jobsTotal)
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (weather == null) {
			if (other.weather != null)
				return false;
		} else if (!weather.equals(other.weather))
			return false;
		return true;
	}
	public int getBikeCount() {
		return bikeCount;
	}
	public int getJobsTotal() {
		return jobsTotal;
	}
	public int getJobsLeft() {
		return jobsLeft;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public BikeArea getBikeRec() {
		return bikeRec;
	}
	public Weather getWeather() {
		return weather;
	}
	public void setBikeCount(int bikeCount) {
		this.bikeCount = bikeCount;
	}
	public void setJobsTotal(int jobsTotal) {
		this.jobsTotal = jobsTotal;
	}
	public void setJobsLeft(int jobsLeft) {
		this.jobsLeft = jobsLeft;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public void setBikeRec(BikeArea bikeRec) {
		this.bikeRec = bikeRec;
	}
	public void setWeather(Weather weather) {
		this.weather = weather;
	}
	@Override
	public String toString() {
		return "BikeHeader [bikeCount=" + bikeCount + ", jobsTotal="
				+ jobsTotal + ", jobsLeft=" + jobsLeft + ", startTime="
				+ startTime + ", endTime=" + endTime + ", bikeRec=" + bikeRec
				+ ", weather=" + weather + "]";
	}
	
	
	
	
	
}
