package com.pojo;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class GaodePath implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6211069659398539967L;
	private int distance;
	private int duration;
	private List<Lnglat> paths;
	public GaodePath() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GaodePath(int distance, int duration, List<Lnglat> paths) {
		super();
		this.distance = distance;
		this.duration = duration;
		this.paths = paths;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public List<Lnglat> getPaths() {
		return paths;
	}
	public void setPaths(List<Lnglat> paths) {
		this.paths = paths;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + distance;
		result = prime * result + duration;
		result = prime * result + ((paths == null) ? 0 : paths.hashCode());
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
		GaodePath other = (GaodePath) obj;
		if (distance != other.distance)
			return false;
		if (duration != other.duration)
			return false;
		if (paths == null) {
			if (other.paths != null)
				return false;
		} else if (!paths.equals(other.paths))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "GaodePath [distance=" + distance + ", duration=" + duration + ", paths=" + paths + "]";
	}
	
	
}
