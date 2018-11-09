package com.poi;

public class UrlBuilder {
	private static final String BUSLINE="http://www1.xbus.cn/search1.asp?t=4";
	private static final String BUSSITES="http://www1.xbus.cn/xianlu.asp?checi=";
	
	public static String buildBusline(){
		return BUSLINE;
	}
	public static String buildBusSites(String lineName){
		
		return BUSSITES+lineName;
	}
	
	public static String test(){
		return "http://www1.xbus.cn/xianlu.asp?checi=%B6%FE%BB%B71%BA%C5%CF%DF";
	}

}
