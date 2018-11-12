package com.pojo;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

@Repository
public class BikeArea implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1743863959959897169L;
	private double startLng ;
	private double startLat ;
	private double endLng ;
	private double endLat ;
	
	
	
	public BikeArea() {
		super();
	}

	/**
	 * 以左上角和右下角的经纬度构建一个矩形区域
	 * @param startLng 左上角经度
	 * @param startLat 左上角纬度
	 * @param endLng 右下角经度
	 * @param endLat 右下角纬度
	 */ 
	public BikeArea(double startLng, double startLat, double endLng,
			double endLat) {
		super();
		this.startLng = startLng;
		this.startLat = startLat;
		this.endLng = endLng;
		this.endLat = endLat;
	}

	public double getStartLng() {
		return startLng;
	}

	public double getStartLat() {
		return startLat;
	}

	public double getEndLng() {
		return endLng;
	}

	public double getEndLat() {
		return endLat;
	}

	public void setStartLng(double startLng) {
		this.startLng = startLng;
	}

	public void setStartLat(double startLat) {
		this.startLat = startLat;
	}

	public void setEndLng(double endLng) {
		this.endLng = endLng;
	}

	public void setEndLat(double endLat) {
		this.endLat = endLat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(endLat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(endLng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(startLat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(startLng);
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
		BikeArea other = (BikeArea) obj;
		if (Double.doubleToLongBits(endLat) != Double
				.doubleToLongBits(other.endLat))
			return false;
		if (Double.doubleToLongBits(endLng) != Double
				.doubleToLongBits(other.endLng))
			return false;
		if (Double.doubleToLongBits(startLat) != Double
				.doubleToLongBits(other.startLat))
			return false;
		if (Double.doubleToLongBits(startLng) != Double
				.doubleToLongBits(other.startLng))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BikeArea [startLng=" + startLng + ", startLat=" + startLat
				+ ", endLng=" + endLng + ", endLat=" + endLat + "]";
	}
	
}
