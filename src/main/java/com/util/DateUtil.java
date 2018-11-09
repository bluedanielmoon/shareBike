package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {		
	//2018-11-16 03:00
	private static SimpleDateFormat parseFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat bikeFormat=new SimpleDateFormat("yyyy_MM_dd");
	private static SimpleDateFormat fileFormat=new SimpleDateFormat("yyyy_M_d/H");
	private static SimpleDateFormat durationFormat=new SimpleDateFormat("yyyy_M_d H");

	/**
	 * 解析yyyy-MM-dd HH:mm格式的时间
	 * @param time
	 * @return
	 */
	public static Date parseTime(String time){
		Date date=null;
		try {
			date=parseFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 解析yyyy_MM_dd格式的文件名到日期
	 * @param fileName
	 * @return
	 */
	public static Date parseFile(String fileName){
		
		Date date=null;
		try {
			date=bikeFormat.parse(fileName);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 将yyyy-MM-dd HH:mm格式的时间解析出来，转换成yyyy_M_d/H格式的目录字符串
	 * @param time
	 * @return
	 */
	public static String timeToPath(String time){
		
		return fileFormat.format(parseTime(time));
		
	}

	
	public static String timeToPath(Date time){
		
		return fileFormat.format(time);
		
	}
	
	/**
	 * 解析yyyy_M_d H格式的时间
	 * @param time
	 * @return
	 */
	public static Date pareFileTime(String time){
		Date date=null;
		try {
			date=durationFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static void printTime(Date time){
		System.out.println(parseFormat.format(time));
	}
	
}
