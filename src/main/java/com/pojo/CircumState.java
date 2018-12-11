package com.pojo;

public class CircumState {

	private int hour;
	// 1-work ,0-notWork
	private int workDay;
	// 1-canGo,0-cannot Go
	private int weather;
	// 1-[10,25],2-[5-10,25-30],3-[<5,>30]
	private int temp;

	public CircumState() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CircumState(int hour, int workDay, int weather, int temp) {
		super();
		this.hour = hour;
		this.workDay = workDay;
		this.weather = weather;
		this.temp = temp;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getWorkDay() {
		return workDay;
	}

	public void setWorkDay(int workDay) {
		this.workDay = workDay;
	}

	public int getWeather() {
		return weather;
	}

	public void setWeather(int weather) {
		this.weather = weather;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hour;
		result = prime * result + temp;
		result = prime * result + weather;
		result = prime * result + workDay;
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
		CircumState other = (CircumState) obj;
		if (hour != other.hour)
			return false;
		if (temp != other.temp)
			return false;
		if (weather != other.weather)
			return false;
		if (workDay != other.workDay)
			return false;
		return true;
	}
<<<<<<< HEAD
	
	public boolean equalsNoTemp(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CircumState other = (CircumState) obj;
		if (hour != other.hour)
			return false;
		if (weather != other.weather)
			return false;
		if (workDay != other.workDay)
			return false;
		return true;
	}
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

	@Override
	public String toString() {
		return "CircumState [hour=" + hour + ", workDay=" + workDay + ", weather=" + weather + ", temp=" + temp + "]";
	}

}
