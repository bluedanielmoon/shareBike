package com.util;

import java.util.Calendar;
import java.util.Date;

public class Test {
	public Date go(){
		//获取今年的日历 1
		Calendar today=Calendar.getInstance();
		int date=today.get(Calendar.DATE);
		
		//获取去年的日历 2
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.YEAR, today.get(Calendar.YEAR)-1);	
		
		//获取日历2 在一年中的周数
		int week=cal.get(Calendar.WEEK_OF_YEAR);	
		cal.set(Calendar.DAY_OF_WEEK,today.get(Calendar.DAY_OF_WEEK));
		
		//下面分别把周数减一，不动，加一得到的结果和去年这一天的结果作比较，得出离的最近的一天。取那一天的结果
		cal.set(Calendar.WEEK_OF_YEAR, week-1);
		int preWeek=cal.get(Calendar.DATE);
		int pre_abs=Math.abs(date-preWeek);

		cal.set(Calendar.WEEK_OF_YEAR, week);
		int nowWeek=cal.get(Calendar.DATE);
		int now_abs=Math.abs(date-nowWeek);

		cal.set(Calendar.WEEK_OF_YEAR, week+1);
		int nextWeek=cal.get(Calendar.DATE);
		int next_abs=Math.abs(date-nextWeek);

		int small=0;
		if(pre_abs<now_abs){
			small=pre_abs;
			cal.set(Calendar.WEEK_OF_YEAR, week-1);
		}else{
			small=now_abs;
			cal.set(Calendar.WEEK_OF_YEAR, week);
		}
		if(small>next_abs){
			cal.set(Calendar.WEEK_OF_YEAR, week+1);
		}
		return cal.getTime();
		
	}
	
	public static void main(String[] args) {
		Test tt=new Test();
		Date result=tt.go();
		System.out.println(result);
	}
}
