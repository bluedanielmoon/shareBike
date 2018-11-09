package com.pojo;

import java.util.Arrays;

import org.springframework.stereotype.Repository;
@Repository
public class Point {
	
	private double[] lnglat;
	private String name;
	private int style;
	
	
	public Point() {
		super();
	}
	public Point(double[] lnglat, String name, int style) {
		super();
		this.lnglat = lnglat;
		this.name = name;
		this.style = style;
	}
	public double[] getLnglat() {
		return lnglat;
	}
	public String getName() {
		return name;
	}
	public int getStyle() {
		return style;
	}
	public void setLnglat(double[] lnglat) {
		this.lnglat = lnglat;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setStyle(int style) {
		this.style = style;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(lnglat);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + style;
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
		Point other = (Point) obj;
		if (!Arrays.equals(lnglat, other.lnglat))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (style != other.style)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Point [lnglat=" + Arrays.toString(lnglat) + ", name=" + name
				+ ", style=" + style + "]";
	}
	
}
