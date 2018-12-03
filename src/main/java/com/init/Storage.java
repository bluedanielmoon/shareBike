package com.init;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simu.Simulator;

@Component
public class Storage {
	
	@Autowired
	public static ConcurrentHashMap<String, Simulator> simulers = new ConcurrentHashMap<>();
	
	public static Simulator getSimuler(String id) {
		
		if(simulers.containsKey(id)) {
			return simulers.get(id);
		}
		return null;
	}
	
	
}
