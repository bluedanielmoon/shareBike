package com.execute;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.Lnglat;
import com.pojo.LnglatTime;
import com.util.CoordsUtil;
import com.util.DateUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;

@Component
public class LazyAnalyze {

	/**
	 * 获得某一个日期，前daysBefore天，每天向前checkDay天数内的不活跃单车数 每次变动的是startTime
	 * 
	 * @param startTime
	 * @param daysBefore 从零开始计数
	 * @param checkDay   1代表1*24个小时，即一天
	 */
	public Map<Date, Integer> getDurationInactive(Date startTime, int daysBefore, int checkDay) {

		Calendar calendar = Calendar.getInstance();
		Map<Date, Integer> list = new TreeMap<>();
		int oneday = 0;
		int[] hours = new int[1];
		Map<String, Integer> daysList = new HashMap<>();
		for (int i = 0; i <= daysBefore; i++) {
			daysList = checkLatestUnactive(startTime, checkDay, hours);
			if (hours[0] == 0) {
				continue;
			}
			oneday = getCount(daysList, hours[0]);
			list.put(startTime, oneday);
			calendar.setTime(startTime);
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
			startTime = calendar.getTime();
		}
		return list;
	}

	public void writeResult(Map<Date, Integer> data) {
		MapperUtil.writeMapData("/Users/daniel/projects/avtiveData/active1.txt", data, Date.class, Integer.class);
	}

	public Map<Date, Integer> readResult() {
		return MapperUtil.readMapData("/Users/daniel/projects/avtiveData/active1.txt", Date.class, Integer.class);
	}

	/**
	 * 某一个日期，向前若干天的不活跃排布，startTime不变，分别向前0,1,2,3...天数
	 * 
	 * @param startTime
	 * @param checkDays
	 */
	public List<Integer> getDayInactiveBikes(Date startTime, int checkDays) {

		int[] hours = new int[1];
		int oneday = 0;
		Map<String, Integer> daysList = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		for (int i = 1; i <= checkDays; i++) {
			daysList = checkLatestUnactive(startTime, i, hours);
			oneday = getCount(daysList, hours[0]);
			list.add(oneday);
		}
		return list;
	}

	/**
	 * 某一个日期，之前多长时间不活跃的单车的ID集合
	 * 
	 * @param startTime
	 * @param checkDays
	 */
	public List<String> getInactiveBikes(Date startTime, int checkDays) {
		int[] hours = new int[1];
		Map<String, Integer> bikes = checkLatestUnactive(startTime, checkDays, hours);

		int hoursMax = hours[0];
		getCount(bikes, hoursMax);
		List<String> inactives = new ArrayList<>();
		for (String bikeID : bikes.keySet()) {
			if (bikes.get(bikeID) == hoursMax) {
				inactives.add(bikeID);
			}
		}
		return inactives;
	}

	private int getCount(Map<String, Integer> bikeCount, int hoursCount) {
		int[] counts = new int[hoursCount + 1];
		for (String bikeID : bikeCount.keySet()) {
			int inActCount = bikeCount.get(bikeID);
			counts[inActCount]++;
		}
		return counts[counts.length - 1];
	}

	private Map<String, Integer> checkLatestUnactive(Date startTime, int daysBefore, int[] hours) {
		List<Path> allFiles = FilesUtil.listAllFiles(false);
		FilesUtil.sortFiles(allFiles);
		Map<String, List<LnglatTime>> bikes = new HashMap<>();

		String bikeId = null;
		int dayCount = 0;
		int hoursCount = 0;
		Date endTime = null;
		Calendar calendar = Calendar.getInstance();
		String temp = null;
		Map<String, Integer> bikeCount = new HashMap<>();

		Map<String, Lnglat> bikePos = new HashMap<>();

		Map<String, Object> firstFile = FilesUtil.readFileToBikeMap(allFiles.get(0).toString());
		Date firstFileTime = ((BikeHeader) firstFile.get("header")).getStartTime();
		if (startTime.compareTo(firstFileTime) < 0) {
			return bikeCount;
		}
		for (int i = allFiles.size() - 1; i >= 0; i--) {

			BikeHeader header = FilesUtil.readFileHeader(allFiles.get(i).toString());
			Date fileTime = header.getStartTime();

			if ((i == allFiles.size() - 1) && startTime.compareTo(fileTime) > 0) {
				break;
			}
			if (endTime != null && fileTime.compareTo(endTime) <= 0) {
				break;
			}
			if (DateUtil.isSameDayHour(startTime, fileTime)) {
				Map<String, Object> file = FilesUtil.readFileToBikeMap(allFiles.get(i).toString());
				List<BikePos> fileBikes = (List<BikePos>) file.get("bikes");
				calendar.setTime(startTime);
				calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - daysBefore);
				endTime = calendar.getTime();
				for (BikePos bike : fileBikes) {
					bikePos.put(bike.getBikeID(), new Lnglat(bike.getLng(), bike.getLat()));
					bikeCount.put(bike.getBikeID(), 0);
				}
			} else if (endTime == null) {
				continue;
			} else {
				Map<String, Object> file = FilesUtil.readFileToBikeMap(allFiles.get(i).toString());
				List<BikePos> fileBikes = (List<BikePos>) file.get("bikes");
				for (BikePos bike : fileBikes) {
					temp = bike.getBikeID();
					if (bikePos.containsKey(temp)) {
						Lnglat startPos = bikePos.get(temp);
						// 车子位置一直没有移动过
						if (CoordsUtil.calcuDist(startPos.getLng(), startPos.getLat(), bike.getLng(),
								bike.getLat()) <= 500 && bikeCount.get(temp) == hoursCount) {
							bikeCount.put(temp, bikeCount.get(temp) + 1);
						}
					}
				}
				hoursCount++;
			}
		}
		hours[0] = hoursCount;
		return bikeCount;

	}
}
