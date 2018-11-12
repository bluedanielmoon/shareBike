package com.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	/**
	 * 判断输入的字符串是否符合2018_10_21格式
	 * @param input
	 * @return
	 */
	public static boolean matchDate(String input) {
		if(input==null||input.isEmpty()) {
			return false;
		}
		return input.matches("\\d{4}_\\d{1,2}_\\d{1,2}");
	}
	
	/**
	 * 判断输入的字符串是否符合2018_10_21 12格式
	 * @param input
	 * @return
	 */
	public static boolean matchHour(String input) {
		if(input==null||input.isEmpty()) {
			return false;
		}
		return input.matches("\\d{4}_\\d{1,2}_\\d{1,2}\\s\\d{1,2}");
	}
	public static void main(String[] args) {
		boolean falg=matchHour("2018_10_22 12");
		System.out.println(falg);
		
	}
}
