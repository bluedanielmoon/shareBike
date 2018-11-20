package com.execute;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.Lnglat;
import com.pojo.MapSize;
import com.pojo.Varia;
import com.util.CoordsUtil;
import com.util.DateUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;

@Component
public class Heater {

	private static MapHelper helper = new MapHelper();
	private static String DEFAULT_HEAT = "heatData/";

	public static void main(String[] args) {

		String date="2018_10_31";
		int dist=50;
		int type=2;
		Heater heater=new Heater();
		heater.checkOrProduce(date, dist, type);

	}

	public List<Varia> calcuFlucByChance(Map<String, Varia> data, List<Map<String, Object>> bikes) {

		List<Varia> result = new ArrayList<>();

		List<Double> bkCounts = new ArrayList<>();
		for (Map<String, Object> b : bikes) {
			BikeHeader header = (BikeHeader) b.get("header");
			bkCounts.add((double) header.getBikeCount());
		}
		double[] avgChange = new double[23];
		int[] changed = new int[23];

		int temp = 0;
		for (String s : data.keySet()) {
			Varia var = data.get(s);

			List<Integer> nums = var.getNumList();
			for (int i = 0; i < nums.size() - 1; i++) {
				temp = Math.abs(nums.get(i) - nums.get(i + 1));
				avgChange[i] = avgChange[i] + temp;
				if (temp > 0) {
					changed[i] += 1;
				}
			}

		}
		for (int i = 0; i < avgChange.length; i++) {
			avgChange[i] /= changed[i];
		}

		for (String s : data.keySet()) {
			Varia var = data.get(s);

			List<Integer> nums = var.getNumList();
			double start = 1;
			// 1*（时刻变化数/时刻平均变化数）*（单车数量/单车总数）
			for (int i = 0; i < nums.size() - 1; i++) {
				int change = Math.abs(nums.get(i) - nums.get(i + 1));
				start = start * (1 + (nums.get(i) / bkCounts.get(i)) * (change / avgChange[i]));
			}

			var.setFluc(start);
			Lnglat gaode = CoordsUtil.getAreaCenter(var.getArea());
			var.setCenter(CoordsUtil.turnBaiDuCoord(gaode));
			result.add(var);

		}
		return result;
	}

	public List<Varia> checkOrProduce(String day, int dist, int type) {
		String path = getFilePath(day, dist, type);
		
		if (Files.exists(Paths.get(path))) {
			return MapperUtil.readListData(path, Varia.class);

		} else {
			List<Varia> ls = new ArrayList<>();
			if (type == 1) {
				ls = getFlucByVaria(day,dist);
			} else if (type == 2) {
				ls = getFlucByChance(day,dist);
			}
			MapperUtil.writeListData(path, ls,Varia.class);
			return ls;
		}
	}

	/**
	 * 通过概率累积的方式计算波动
	 * 
	 * @param day
	 * @param dis
	 * @return
	 */
	public List<Varia> getFlucByChance(String day, int dist) {

		Date date = DateUtil.parseToDay(day);
		List<Map<String, Object>> bikes = FilesUtil.ListFilesInDay(date);

		Map<String, Varia> data = getBikesData(day, dist);
		return calcuFlucByChance(data, bikes);
	}

	private static Map<String, Varia> getBikesData(String day, int dist) {
		String path = getFilePath(day, dist, 0);
		Map<String, Varia> data = null;
		if (!Files.exists(Paths.get(path))) {
			data = getGridBikesList(day, dist);

			MapperUtil.writeMapData(path, data, Varia.class);
		} else {
			data = MapperUtil.readMapData(path, Varia.class);
		}
		return data;
	}

	/**
	 * 通过方差的方式计算波动,并转化为百度地图坐标值
	 * 
	 * @param day
	 * @param dis
	 * @return
	 */
	public List<Varia> getFlucByVaria(String day, int dist) {
		Map<String, Varia> data = getBikesData(day, dist);

		List<Varia> result = new ArrayList<>();

		for (String s : data.keySet()) {
			Varia var = data.get(s);
			List<Integer> ls = var.getNumList();

			double avg = calcuAverage(ls);

			double varia = calcuVariance(ls, avg);
			var.setAvrg(avg);
			var.setFluc(varia);

			// 百度地图存在坐标加密， 通过转换，以近似的方式展示坐标
			Lnglat gaode = CoordsUtil.getAreaCenter(var.getArea());
			var.setCenter(CoordsUtil.turnBaiDuCoord(gaode));
			result.add(var);
		}
		return result;
	}

	/**
	 * 计算平均值
	 * 
	 * @param ls
	 * @return
	 */
	public static double calcuAverage(List<Integer> ls) {
		if (ls.size() == 0) {
			return 0;
		}
		double total = 0;
		for (Integer i : ls) {
			total += i;
		}
		BigDecimal bigDecimal = new BigDecimal(total / ls.size());
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 计算方差
	 * 
	 * @param ls
	 * @param avg
	 * @return
	 */
	public static double calcuVariance(List<Integer> ls, double avg) {

		double total = 0;
		for (Integer i : ls) {
			total += (i - avg) * (i - avg);
		}
		BigDecimal bigDecimal = new BigDecimal(total / (ls.size() - 1));
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 得到文件路径,0--
	 * 
	 * @param day
	 * @param dist
	 * @return
	 */
	private static String getFilePath(String day, int dist, int type) {
		if (type == 1 || type == 2) {
			return DEFAULT_HEAT + day + "_" + dist + "_" + type + ".txt";
		} else {
			return DEFAULT_HEAT + day + "_" + dist + ".txt";
		}

	}

	/**
	 * 把栅格地图单车的结果写入文件
	 * 
	 * @param        <T>
	 * @param path
	 * @param result
	 */
	private static <T> void writeMapData(String path, Map<String, T> result) {
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

	/**
	 * 对某一天的数据，将单车数据放入栅格地图
	 * 
	 * @param day
	 * @param dist
	 * @return
	 */
	private static Map<String, Varia> getGridBikesList(String day, int dist) {
		BikeArea area = State.getArea();

		MapSize size = new MapSize();
		Map<String, Map<String, Object>> map = helper.divideMapToGrid(area, dist, size);

		System.out.println(size);
		Date date = DateUtil.parseToDay(day);

		List<Map<String, Object>> bikes = FilesUtil.ListFilesInDay(date);

		return addRecBikesCount(area, dist, map, bikes);
	}

	/**
	 * 把单车的数据放入栅格地图
	 * 
	 * @param area
	 * @param dist
	 * @param map
	 * @param bikes
	 * @return
	 */
	private static Map<String, Varia> addRecBikesCount(BikeArea area, int dist, Map<String, Map<String, Object>> map,
			List<Map<String, Object>> bikes) {

		TreeMap<String, Varia> result = new TreeMap<>(new Comparator<String>() {

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

					Varia recInfo = new Varia();
					recInfo.setArea(rec);
					recInfo.setNumList(nums);

					result.put(s, recInfo);
				}

			} else {
				for (String s : map.keySet()) {
					Map<String, Object> info = map.get(s);
					List<BikePos> count = (List<BikePos>) info.get("bikes");

					Varia recInfo = result.get(s);
					List<Integer> nums = recInfo.getNumList();

					recCount = count.size();
					nums.add(recCount);
					count.clear();
				}
			}

		}

		return result;

	}

}
