package com.execute;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.MapSize;
import com.util.DateUtil;
import com.util.FilesUtil;

public class Heater {

	private static MapHelper helper = new MapHelper();
	private static String DEFAULT_HEAT = "heatData/";

	public static void main(String[] args) {
		String str_day = "2018_10_31";
		int dist = 50;
		long start = System.currentTimeMillis();
		String path=getFilePath(str_day, dist);
//		Map<String, Map<String, Object>> result = getHeatList(str_day, dist);
//		writeHeatData(str_day, dist, result);

		
		Map<String, Map<String, Object>> data=readHeatData(path);
		
		Map<String, Double>  vaResult=new HashMap<String, Double>();
		
		for(String s:data.keySet()) {
			List<Integer>  ls=(List<Integer>) data.get(s).get("numList");
			double varia=calcuVariance(ls);
			System.out.println(varia);
			vaResult.put(s, varia);
		}
		long end = System.currentTimeMillis();

//		System.out.println("cost time:" + (end - start));
	}
	
	public static double calcuAverage(List<Integer>  ls) {
		if(ls.size()==0) {
			return 0;
		}
		double total=0;
		for(Integer i:ls) {
			total+=i;
		}
	
		return total/ls.size();
	}
	
	public static double calcuVariance(List<Integer> ls) {
		
		double avg=calcuAverage(ls);
		double total=0;
		for(Integer i:ls) {
			total+=(i-avg)*(i-avg);
		}
		BigDecimal bigDecimal=new BigDecimal(total/(ls.size()-1));
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static String getFilePath(String day, int dist) {
		return DEFAULT_HEAT + day + "_" + dist + ".txt";
	}

	public static void writeHeatData(String path,Map<String, Map<String, Object>> result) {
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(path);
		try {

			ObjectWriter wrter = mapper.writerWithDefaultPrettyPrinter();
			SequenceWriter seq = wrter.writeValues(file);
			seq.write(result);
			seq.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static Map<String, Map<String, Object>> readHeatData(String file) {
		ObjectMapper mapper = new ObjectMapper();
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return null;
		}

		try {

			Map<String, Map<String, Object>> maps = mapper.readValue(path.toFile(), Map.class);
			return maps;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static Map<String, Map<String, Object>> getHeatList(String day, int dist) {
		BikeArea area = State.getArea();

		MapSize size = new MapSize();
		Map<String, Map<String, Object>> map = helper.divideMapToGrid(area, dist, size);

		Date date = DateUtil.parseToDay(day);

		List<Map<String, Object>> bikes = FilesUtil.ListFilesInDay(date);

		return getRecBikesCount(area, dist, map, bikes);
	}

	public static Map<String, Map<String, Object>> getRecBikesCount(BikeArea area, int dist,
			Map<String, Map<String, Object>> map, List<Map<String, Object>> bikes) {

		TreeMap<String, Map<String, Object>> result = new TreeMap<>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String[] rowCol = o1.split("_");
				String[] rowCol2 = o2.split("_");
				int[] num1 = new int[] { Integer.parseInt(rowCol[0]), Integer.parseInt(rowCol[1]) };
				int[] num2 = new int[] { Integer.parseInt(rowCol2[0]), Integer.parseInt(rowCol2[1]) };
				if (num1[0] == num2[0]) {
					return Integer.compare(num1[1], num2[1]);
				} else {
					return Integer.compare(num1[0], num2[0]);
				}
			}
		});

		int recCount = 0;
		for (int i = 0; i < bikes.size(); i++) {
			Map<String, Object> b = bikes.get(i);
			BikeHeader header = (BikeHeader) b.get("header");
			System.out.println(header.getStartTime());
			List<BikePos> ls = (List<BikePos>) b.get("bikes");
			helper.pubBikesToGrid(ls, area, map, dist);
			if (i == 0) {
				for (String s : map.keySet()) {
					List<Integer> nums = new ArrayList<>();
					Map<String, Object> info = map.get(s);
					BikeArea rec = (BikeArea) info.get("area");
					List<BikePos> count = (List<BikePos>) info.get("bikes");
					recCount = count.size();
					nums.add(recCount);
					count.clear();

					Map<String, Object> recInfo = new HashMap<>();
					recInfo.put("area", rec);
					recInfo.put("numList", nums);
					result.put(s, recInfo);
				}

			} else {
				for (String s : map.keySet()) {
					Map<String, Object> info = map.get(s);
					List<BikePos> count = (List<BikePos>) info.get("bikes");

					Map<String, Object> recInfo = result.get(s);
					List<Integer> nums = (List<Integer>) recInfo.get("numList");

					recCount = count.size();
					nums.add(recCount);
					count.clear();
				}
			}

		}

		return result;

	}

}
