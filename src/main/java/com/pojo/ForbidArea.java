package com.pojo;

public class ForbidArea {
	private int id;
	private String name;
	private String path;
	public ForbidArea() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "ForbidArea [id=" + id + ", name=" + name + ", path=" + path + "]";
	}
	
}
