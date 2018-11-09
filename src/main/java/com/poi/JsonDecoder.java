package com.poi;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.Message;

/**
 * 字符串解析
 * @author Administrator
 *
 */
public class JsonDecoder {
	
	private static ObjectMapper mapper=new ObjectMapper();
	
	/**
	 * 将字符串转化为对象
	 * @param input
	 * @return
	 */
	public Message toJson(String input){
		if(input==null){
			return null;
		}
		try {
			
			return mapper.readValue(input, Message.class);
					
		} catch (IOException e) {
			return null;
		}
	}
}
