package com.pojo;

import java.util.Arrays;

import org.springframework.stereotype.Repository;

@Repository
public class GaodeLine {
	private String id;
	private String company;
	private String name;
	private String start_stop;
	private String end_stop;
	private String distance;
	private String stime;
	private String etime;
	private Lnglat path[];
	private GaodeStop via_stops[];
	private GaodeBound bounds;
	public GaodeLine() {
		super();
	}
	public GaodeLine(String id, String company, String name, String start_stop,
			String end_stop, String distance, String stime, String etime,
			Lnglat[] path, GaodeStop[] via_stops, GaodeBound bounds) {
		super();
		this.id = id;
		this.company = company;
		this.name = name;
		this.start_stop = start_stop;
		this.end_stop = end_stop;
		this.distance = distance;
		this.stime = stime;
		this.etime = etime;
		this.path = path;
		this.via_stops = via_stops;
		this.bounds = bounds;
	}
	public String getId() {
		return id;
	}
	public String getCompany() {
		return company;
	}
	public String getName() {
		return name;
	}
	public String getStart_stop() {
		return start_stop;
	}
	public String getEnd_stop() {
		return end_stop;
	}
	public String getDistance() {
		return distance;
	}
	public String getStime() {
		return stime;
	}
	public String getEtime() {
		return etime;
	}
	public Lnglat[] getPath() {
		return path;
	}
	public GaodeStop[] getVia_stops() {
		return via_stops;
	}
	public GaodeBound getbounds() {
		return bounds;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setStart_stop(String start_stop) {
		this.start_stop = start_stop;
	}
	public void setEnd_stop(String end_stop) {
		this.end_stop = end_stop;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public void setEtime(String etime) {
		this.etime = etime;
	}
	public void setPath(Lnglat[] path) {
		this.path = path;
	}
	public void setVia_stops(GaodeStop[] via_stops) {
		this.via_stops = via_stops;
	}
	public void setbounds(GaodeBound bounds) {
		this.bounds = bounds;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bounds == null) ? 0 : bounds.hashCode());
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result
				+ ((distance == null) ? 0 : distance.hashCode());
		result = prime * result
				+ ((end_stop == null) ? 0 : end_stop.hashCode());
		result = prime * result + ((etime == null) ? 0 : etime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(path);
		result = prime * result
				+ ((start_stop == null) ? 0 : start_stop.hashCode());
		result = prime * result + ((stime == null) ? 0 : stime.hashCode());
		result = prime * result + Arrays.hashCode(via_stops);
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
		GaodeLine other = (GaodeLine) obj;
		if (bounds == null) {
			if (other.bounds != null)
				return false;
		} else if (!bounds.equals(other.bounds))
			return false;
		if (company == null) {
			if (other.company != null)
				return false;
		} else if (!company.equals(other.company))
			return false;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.equals(other.distance))
			return false;
		if (end_stop == null) {
			if (other.end_stop != null)
				return false;
		} else if (!end_stop.equals(other.end_stop))
			return false;
		if (etime == null) {
			if (other.etime != null)
				return false;
		} else if (!etime.equals(other.etime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(path, other.path))
			return false;
		if (start_stop == null) {
			if (other.start_stop != null)
				return false;
		} else if (!start_stop.equals(other.start_stop))
			return false;
		if (stime == null) {
			if (other.stime != null)
				return false;
		} else if (!stime.equals(other.stime))
			return false;
		if (!Arrays.equals(via_stops, other.via_stops))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "GaodeLine [id=" + id + ", company=" + company + ", name="
				+ name + ", start_stop=" + start_stop + ", end_stop="
				+ end_stop + ", distance=" + distance + ", stime=" + stime
				+ ", etime=" + etime + ", path=" + path
				+ ", via_stops=" + via_stops + ", bounds="
				+ bounds + "]";
	}
	
	
	
}
