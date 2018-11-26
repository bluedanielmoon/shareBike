package com.pojo;

import java.util.concurrent.CountDownLatch;

public class AreaScore {
	
	private double fluc;
	private int maxCount;
	private int poiCount;
	private BikeArea area;
	private Lnglat center;
	public double getFluc() {
		return fluc;
	}
	public void setFluc(double fluc) {
		this.fluc = fluc;
	}
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	public int getPoiCount() {
		return poiCount;
	}
	public void setPoiCount(int poiCount) {
		this.poiCount = poiCount;
	}
	public BikeArea getArea() {
		return area;
	}
	public void setArea(BikeArea area) {
		this.area = area;
	}
	public Lnglat getCenter() {
		return center;
	}
	public void setCenter(Lnglat center) {
		this.center = center;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + ((center == null) ? 0 : center.hashCode());
		long temp;
		temp = Double.doubleToLongBits(fluc);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + maxCount;
		result = prime * result + poiCount;
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
		AreaScore other = (AreaScore) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (center == null) {
			if (other.center != null)
				return false;
		} else if (!center.equals(other.center))
			return false;
		if (Double.doubleToLongBits(fluc) != Double.doubleToLongBits(other.fluc))
			return false;
		if (maxCount != other.maxCount)
			return false;
		if (poiCount != other.poiCount)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AreaScore [fluc=" + fluc + ", maxCount=" + maxCount + ", poiCount=" + poiCount + ", area=" + area
				+ ", center=" + center + "]";
	}
	
	
}
