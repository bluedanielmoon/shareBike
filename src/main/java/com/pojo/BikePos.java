package com.pojo;

import org.springframework.stereotype.Repository;

@Repository
public class BikePos {
	private String bikeID;
	private double lng;
	private double lat;
	private int count;
	public BikePos() {
		super();
	}
	public BikePos(String bikeID, double lng, double lat, int count) {
		super();
		this.bikeID = bikeID;
		this.lng = lng;
		this.lat = lat;
		this.count = count;
	}
	public String getBikeID() {
		return bikeID;
	}
	public double getLng() {
		return lng;
	}
	public double getLat() {
		return lat;
	}
	public int getCount() {
		return count;
	}
	public void setBikeID(String bikeID) {
		this.bikeID = bikeID;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bikeID == null) ? 0 : bikeID.hashCode());
		result = prime * result + count;
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
		BikePos other = (BikePos) obj;
		if (bikeID == null) {
			if (other.bikeID != null)
				return false;
		} else if (!bikeID.equals(other.bikeID))
			return false;
		if (count != other.count)
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BikePos [bikeID=" + bikeID + ", lng=" + lng + ", lat=" + lat
				+ ", count=" + count + "]";
	}
	
	
	
}