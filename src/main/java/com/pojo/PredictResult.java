package com.pojo;

import java.util.List;

public class PredictResult {
	int id;
	List<Integer> results;
	
	public PredictResult() {
	}
	
	public PredictResult(int id, List<Integer> results) {
		super();
		this.id = id;
		this.results = results;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public List<Integer> getResults() {
		return results;
	}


	public void setResults(List<Integer> results) {
		this.results = results;
	}


	@Override
	public String toString() {
		return "PredictResult [id=" + id + ", results=" + results + "]";
	}
	
	
}
