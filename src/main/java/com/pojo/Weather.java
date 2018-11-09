package com.pojo;

import java.util.Date;

import org.springframework.stereotype.Repository;
@Repository
public class Weather {
	private Date time;
	private int code;
	private String weather;
	private int tempature;
	
	
	public Weather() {
		super();
	}
	public Date getTime() {
		return time;
	}
	public int getCode() {
		return code;
	}
	public String getWeather() {
		return weather;
	}
	public int getTempature() {
		return tempature;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public void setTempature(int tempature) {
		this.tempature = tempature;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + tempature;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		Weather other = (Weather) obj;
		if (code != other.code)
			return false;
		if (tempature != other.tempature)
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (weather == null) {
			if (other.weather != null)
				return false;
		} else if (!weather.equals(other.weather))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Weather [time=" + time + ", code=" + code + ", weather="
				+ weather + ", tempature=" + tempature + "]";
	}
	
}
