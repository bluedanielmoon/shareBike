package com.pojo;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class Varia {

	
	private double avrg;
	private double fluc;
	private BikeArea area;
	private Lnglat center;
	private List<Integer> numList;
	
	
	public Varia() {
		super();
		// TODO Auto-generated constructor stub
	}
	public double getAvrg() {
		return avrg;
	}
	public void setAvrg(double avrg) {
		this.avrg = avrg;
	}
	public double getFluc() {
		return fluc;
	}
	public void setFluc(double fluc) {
		this.fluc = fluc;
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
	public List<Integer> getNumList() {
		return numList;
	}
	public void setNumList(List<Integer> numList) {
		this.numList = numList;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		long temp;
		temp = Double.doubleToLongBits(avrg);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((center == null) ? 0 : center.hashCode());
		result = prime * result + ((numList == null) ? 0 : numList.hashCode());
		temp = Double.doubleToLongBits(fluc);
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
		Varia other = (Varia) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (Double.doubleToLongBits(avrg) != Double.doubleToLongBits(other.avrg))
			return false;
		if (center == null) {
			if (other.center != null)
				return false;
		} else if (!center.equals(other.center))
			return false;
		if (numList == null) {
			if (other.numList != null)
				return false;
		} else if (!numList.equals(other.numList))
			return false;
		if (Double.doubleToLongBits(fluc) != Double.doubleToLongBits(other.fluc))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Varia [avrg=" + avrg + ", fluc=" + fluc + ", area=" + area + ", center=" + center + ", numList="
				+ numList + "]";
	}
	
	
}
