package com.pojo;

import org.springframework.stereotype.Repository;

@Repository
public class GaodeStop {
	private String id;
	private Lnglat location;
	private String name;
	private int sequence;
	public GaodeStop() {
		super();
	}
	public GaodeStop(String id, Lnglat location, String name, int sequence) {
		super();
		this.id = id;
		this.location = location;
		this.name = name;
		this.sequence = sequence;
	}
	public String getId() {
		return id;
	}
	public Lnglat getLocation() {
		return location;
	}
	public String getName() {
		return name;
	}
	public int getSequence() {
		return sequence;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setLocation(Lnglat location) {
		this.location = location;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + sequence;
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
		GaodeStop other = (GaodeStop) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sequence != other.sequence)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "GaodeStop [id=" + id + ", location=" + location + ", name="
				+ name + ", sequence=" + sequence + "]";
	}
	
	
}
