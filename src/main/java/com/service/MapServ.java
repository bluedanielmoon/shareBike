package com.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public interface MapServ {

	public Map<String, Object> getHeatMap(String data) throws JsonProcessingException;
}
