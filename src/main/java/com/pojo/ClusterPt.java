package com.pojo;

import org.springframework.stereotype.Repository;

@Repository
public class ClusterPt {
	private int ID;
	private double lat;
	private double lng;
	public ClusterPt() {
		super();
	}
	public ClusterPt(int iD,double lng, double lat) {
		super();
		ID = iD;
		this.lng = lng;
		this.lat = lat;
		
	}
	public int getID() {
		return ID;
	}
	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		ClusterPt other = (ClusterPt) obj;
		if (ID != other.ID)
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ClusterPt [ID=" + ID + ", lat=" + lat + ", lng=" + lng + "]";
	}
	
	
}
