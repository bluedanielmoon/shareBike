package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pojo.Bike;
import com.pojo.BikePos;

@Service
public interface MapServ {
		
	public Map<String, Object> getHeatMap(String data) throws JsonProcessingException;
}
