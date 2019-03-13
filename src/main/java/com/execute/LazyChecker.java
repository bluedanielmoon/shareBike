package com.execute;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.pojo.BikePos;
import com.pojo.Lnglat;
import com.util.CoordsUtil;
import com.util.DateUtil;
import com.util.FilesUtil;

/**
 * 活跃度分析
 * 
 * @author daniel
 *
 */
@Component
public class LazyChecker {

	public static void main(String[] args) {
		LazyChecker checker = new LazyChecker();
		Date date = DateUtil.pareToHour("2018_12_03 12");
		Map<String, Integer> actives = checker.checkActiveBefore(date, 3);
		List<Integer> divides = new ArrayList<>();
		divides.add(0);
		divides.add(2);
		divides.add(6);
		divides.add(10);
		checker.countActive(actives, divides);
	}

	/**
	 * data代表单车ID加变动次数，divides是变动的范围，将所有的单车的变动次数归入范围内
	 * 对应于0即代表几天内单车位置一次都没动的数量
	 * @param data
	 * @param divides
	 * @return
	 */
	public int[] countActive(Map<String, Integer> data, List<Integer> divides) {
		int[] counts = new int[divides.size() + 1];
		int temp = 0;
		for (String s : data.keySet()) {
			temp = data.get(s);
			for (int i = 0; i < divides.size(); i++) {
				if (temp <= divides.get(i)) {
					counts[i]++;
					break;
				} else if (i == divides.size() - 1) {
					counts[divides.size()]++;
				}
			}

		}
		//System.out.println(Arrays.toString(counts));
		return counts;
	}

	/**
	 * 返回某个日期之前若干天，单车的ID+该单车位置变动的次数
	 * @param day
	 * @param checkDays
	 * @return
	 */
	public Map<String, Integer> checkActiveBefore(Date day, int checkDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(day);
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - checkDays);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		Date before = calendar.getTime();
		List<Path> files = FilesUtil.listFilesInDuration(before, day, true);

		FilesUtil.sortFiles(files);

		Map<String, Object> lastFile = FilesUtil.readFileInfo(files.get(files.size() - 1).toString());
		List<BikePos> targets = (List<BikePos>) lastFile.get("bikes");
		Map<String, Lnglat> checkMap = new HashMap<>();
		Map<String, Integer> countMap = new HashMap<>();
		for (BikePos bike : targets) {
			checkMap.put(bike.getBikeID(), null);
			countMap.put(bike.getBikeID(), 0);
		}
		String temp = null;
		// 过滤所有的文件，只要单车出现过，并且位置变动过一次，就算作活跃，把其从checkFile中去掉
		for (int i = 0; i < files.size(); i++) {
			Map<String, Object> checkFile = FilesUtil.readFileInfo(files.get(i).toString());
			List<BikePos> checks = (List<BikePos>) checkFile.get("bikes");
			// 拿到该文件的单车数据
			for (BikePos bk : checks) {
				// 如果该数据包含目标里面的单车
				temp = bk.getBikeID();
				if (checkMap.containsKey(temp)) {
					Lnglat lnglat = checkMap.get(temp);
					// 如果坐标为空，说明单车第一次出现
					if (lnglat == null) {

						lnglat = new Lnglat(bk.getLng(), bk.getLat());
						checkMap.put(temp, lnglat);
					} else {
						// 如果相差较大
						if (CoordsUtil.calcuDist(bk.getLng(), bk.getLat(), lnglat.getLng(), lnglat.getLat()) > 100) {
							Lnglat coords = checkMap.get(temp);
							coords.setLng(bk.getLng());
							coords.setLat(bk.getLat());
							checkMap.put(temp, coords);
							countMap.put(temp, countMap.get(temp) + 1);

						}
					}
				}
			}
		}
		return countMap;

	}
	
	
}
