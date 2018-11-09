package com.pojo;

import java.util.List;

public class BusLine {
	private int id;
	private String name;
	private List<BusSite> BusSites;
	public BusLine() {
		super();
	}
	public BusLine(int id, String name, List<BusSite> busSites) {
		super();
		this.id = id;
		this.name = name;
		BusSites = busSites;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public List<BusSite> getBusSites() {
		return BusSites;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setBusSites(List<BusSite> busSites) {
		BusSites = busSites;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((BusSites == null) ? 0 : BusSites.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		BusLine other = (BusLine) obj;
		if (BusSites == null) {
			if (other.BusSites != null)
				return false;
		} else if (!BusSites.equals(other.BusSites))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BusLine [id=" + id + ", name=" + name + ", BusSites="
				+ BusSites + "]";
	}
	
	
}
