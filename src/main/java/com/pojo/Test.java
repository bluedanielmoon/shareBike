package com.pojo;

public class Test {
	private int id;
	private int bounds;
	
	
	public int getId() {
		return id;
	}


	public int getBounds() {
		return bounds;
	}


	public void setId(int id) {
		this.id = id;
	}


	public void setBounds(int bounds) {
		this.bounds = bounds;
	}


	@Override
	public String toString() {
		return "Test [id=" + id + ", bounds=" + bounds + "]";
	}
	
}
