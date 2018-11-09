package com.pojo;

import org.springframework.stereotype.Repository;

@Repository
public class GaodeBound {
	private Lnglat northeast;
	private Lnglat southwest;
	public GaodeBound() {
		super();
	}
	public GaodeBound(Lnglat northeast, Lnglat southwest) {
		super();
		this.northeast = northeast;
		this.southwest = southwest;
	}
	public Lnglat getNortheast() {
		return northeast;
	}
	public Lnglat getSouthwest() {
		return southwest;
	}
	public void setNortheast(Lnglat northeast) {
		this.northeast = northeast;
	}
	public void setSouthwest(Lnglat southwest) {
		this.southwest = southwest;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((northeast == null) ? 0 : northeast.hashCode());
		result = prime * result
				+ ((southwest == null) ? 0 : southwest.hashCode());
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
		GaodeBound other = (GaodeBound) obj;
		if (northeast == null) {
			if (other.northeast != null)
				return false;
		} else if (!northeast.equals(other.northeast))
			return false;
		if (southwest == null) {
			if (other.southwest != null)
				return false;
		} else if (!southwest.equals(other.southwest))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "GaodeBound [northeast=" + northeast + ", southwest="
				+ southwest + "]";
	}
	
	
}
