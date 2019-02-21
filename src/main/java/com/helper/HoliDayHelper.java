package com.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HoliDayHelper {

	private static List<Date> specialsWorks;
	private static List<Date> specialsHolidays;

	static {

		specialsWorks = new ArrayList<>();
		specialsHolidays = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DATE, 29);
		specialsWorks.add(calendar.getTime());
		calendar.set(Calendar.DATE, 30);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 31);
		specialsHolidays.add(calendar.getTime());

		calendar.set(Calendar.YEAR, 2019);
		calendar.set(Calendar.MONTH, 0);

		calendar.set(Calendar.DATE, 1);
		specialsHolidays.add(calendar.getTime());

		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.DATE, 2);
		specialsWorks.add(calendar.getTime());
		calendar.set(Calendar.DATE, 3);
		specialsWorks.add(calendar.getTime());

		calendar.set(Calendar.DATE, 4);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 5);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 6);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 7);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 8);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 9);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 10);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 11);
		specialsWorks.add(calendar.getTime());

		// 4月5日~4月7日与周末连休，共3天。2019年4月8日(星期一)上班。

		calendar.set(Calendar.MONTH, 3);
		calendar.set(Calendar.DATE, 5);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 6);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 7);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 8);
		specialsWorks.add(calendar.getTime());

		calendar.set(Calendar.DATE, 27);
		specialsWorks.add(calendar.getTime());
		calendar.set(Calendar.DATE, 28);
		specialsWorks.add(calendar.getTime());

		calendar.set(Calendar.DATE, 29);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 30);
		specialsHolidays.add(calendar.getTime());

		calendar.set(Calendar.MONTH, 4);
		calendar.set(Calendar.DATE, 1);
		specialsHolidays.add(calendar.getTime());
		calendar.set(Calendar.DATE, 2);
		specialsWorks.add(calendar.getTime());
	}

	public static boolean isWorkDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date temp = calendar.getTime();
		for (Date d : specialsWorks) {
			if (temp.compareTo(d) == 0) {
				return true;
			}
		}
		for (Date d : specialsHolidays) {
			if (temp.compareTo(d) == 0) {
				return false;
			}

		}
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		if (weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY) {
			return false;
		}
		return true;

	}

}
