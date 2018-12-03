package com.execute;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.pojo.BikeHeader;
import com.util.FilesUtil;

@Component
public class BikesCounter {
	
	
	public Map<Integer, List<Map<String, Object>>> countByDay() {
		List<Path> all = FilesUtil.listAllFiles(false);
		Map<Integer, List<Map<String, Object>>> result = new TreeMap<>();

		BikeHeader bk = null;
		Date startTime = null;
		int hour = 0;
		Calendar cal = Calendar.getInstance();
		for (Path p : all) {

			bk = FilesUtil.readFileHeader(p.toString());
			startTime = bk.getStartTime();

			cal.setTime(startTime);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			Map<String, Object> item = new HashMap<>();
			item.put("count", bk.getBikeCount());
			item.put("date", bk.getStartTime());
			if (result.containsKey(hour)) {

				result.get(hour).add(item);
			} else {

				List<Map<String, Object>> hourList = new ArrayList<>();
				hourList.add(item);
				result.put(hour, hourList);

			}
		}

		for (Integer i : result.keySet()) {
			List<Map<String, Object>> ls = result.get(i);
			ls.sort(new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Date da1 = (Date) o1.get("date");
					Date da2 = (Date) o2.get("date");
					return da1.compareTo(da2);
				}
			});
		}

//		for (Integer i : result.keySet()) {
//			List<Map<String, Object>> ls = result.get(i);
//			System.out.println(i + "---" + result.get(i));
//		}
		return result;

	}
}
