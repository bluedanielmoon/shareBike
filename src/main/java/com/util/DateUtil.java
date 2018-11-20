package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * dateformat是非线程安全的，不能写static类型的变量来解析
 * @author daniel
 *
 */
@Component
public class DateUtil {		

	/**
	 * 解析yyyy-MM-dd HH:mm格式的时间
	 * @param time
	 * @return
	 */
	public static Date parseToMinute(String time){
		Date date=null;
		SimpleDateFormat parse=new SimpleDateFormat("yyyy_MM_dd HH:mm");
		
		try {
			date=parse.parse(time);
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
	public static Date parseToDay(String fileName){
		
		Date date=null;
		SimpleDateFormat parse=new SimpleDateFormat("yyyy_MM_dd");
		try {
			date=parse.parse(fileName);
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
	public static String parseTimeToFile(String time){
		SimpleDateFormat parse=new SimpleDateFormat("yyyy_M_d/H");
		Date date=parseToMinute(time);
		return parse.format(date);
		
	}

	/**
	 * 将时间转化为yyyy_M_d/H格式的字符串
	 * @param time
	 * @return
	 */
	public static String timeToPath(Date time){
		SimpleDateFormat parse=new SimpleDateFormat("yyyy_M_d/H");
		return parse.format(time);
		
	}
	
	
	
	/**
	 * 解析yyyy_M_d H格式的时间
	 * @param time
	 * @return
	 */
	public static Date pareToHour(String time){
		Date date=null;
		SimpleDateFormat parse=new SimpleDateFormat("yyyy_MM_dd HH");
		try {
			date=parse.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getLatestDay() {
		return "2018_11_14";
	}
}
