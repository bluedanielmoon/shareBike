package com.execute;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.init.FileName;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.Lnglat;
import com.pojo.LnglatTime;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;

/**
 * 此类中只统计了历史上单车变动的数据，本意是想做单车活跃性分析，但是因为没有 统计单车在空白的时间到底是超出统计区还是没动，无法做。
 * 
 * 此类应该可以实现的是对于单车迁移的分析。例如，某一站点有哪些单车 ，追踪这些单车，分析出该站点迁出单车的地点
 * 
 * @author daniel
 *
 */
@Component
public class BikeTracker {
	private final int LAST_NOW_DISTANCE = 100;

	@Autowired
	private SiteServ siteServ;

	public static void main(String[] args) {

//		System.out.println("共有单车："+data.size());
//		List<Integer> statis=getMoveCount(data);
//		System.out.println(statis);

		BikeTracker tracker = new BikeTracker();
		tracker.analyze();

	}

	/**
	 * 生产出单车轨迹文件
	 */
	public void produceBikeTackFile() {
		Map<String, List<LnglatTime>> data = getBikesToDay();

		MapperUtil.writeMapListData(FileName.DEFAULT_TRACK, data, String.class, LnglatTime.class);
	}

	public void analyze() {
		String latest = FilesUtil.checkLastestFile();
		Map<String, Object> latestBikes = FilesUtil.readFileToBikeMap(latest);
		List<BikePos> bikes = (List<BikePos>) latestBikes.get("bikes");
		Map<String, List<LnglatTime>> data = MapperUtil.readMapListData(FileName.DEFAULT_TRACK, LnglatTime.class);

		Map<String, List<LnglatTime>> bikeTracks = new HashMap<>();

		String tempID = null;
		for (BikePos bike : bikes) {
			tempID = bike.getBikeID();
			if (data.containsKey(tempID)) {
				bikeTracks.put(tempID, data.get(tempID));
			}
		}
		List<Integer> statis = getMoveCount(data);

		List<Integer> statis2 = getMoveCount(bikeTracks);

		List<Double> ratios = getPercents(statis2);

		trackBikesInSite(3, bikes);
	}

	/**
	 * 根据站点ID和单车信息，找出属于该站点的单车和单车的轨迹信息
	 * 
	 * @param siteID
	 * @param bikes
	 * @return
	 */
	public Map<String, Object> trackBikesInSite(int siteID, List<BikePos> bikes) {
		Site site = siteServ.getSiteById(siteID);
		List<BikePos> inSiteBikes = new ArrayList<>();
		Lnglat lnglat = new Lnglat(site.getLng(), site.getLat());
		BikeArea area = CoordsUtil.getCenterArea(lnglat);
		Map<String, List<LnglatTime>> data = MapperUtil.readMapListData(FileName.DEFAULT_TRACK, LnglatTime.class);

		Map<String, List<LnglatTime>> bikeTracks = new HashMap<>();
		String tempID = null;
		for (BikePos bike : bikes) {
			if (CoordsUtil.isInArea(area, bike.getLng(), bike.getLat())) {
				tempID = bike.getBikeID();
				if (data.containsKey(tempID)) {
					bikeTracks.put(tempID, data.get(tempID));
				}
				inSiteBikes.add(bike);
			}
		}
		Map<String, Object> result = new HashMap<>();
		result.put("tracks", bikeTracks);
		result.put("bikes", inSiteBikes);

		return result;
	}

	public void anaylyzeTrack(List<LnglatTime> tracks) {
		int count = tracks.size();
		LnglatTime start = tracks.get(0);
		LnglatTime end = tracks.get(count - 1);
		Date startTime = start.getTime();
		Date endTime = end.getTime();
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startTime);
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		int startDay = startCal.get(Calendar.DAY_OF_YEAR);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startTime);
		int endDay = endCal.get(Calendar.DAY_OF_YEAR);

		int dayLag = endDay - startDay;
		Date temp = null;
		Calendar cal = Calendar.getInstance();
		Map<Date, Integer> dayCounts = new TreeMap<>();
		for (int i = 0; i < tracks.size(); i++) {

			temp = tracks.get(i).getTime();
			cal.setTime(temp);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date oneDay = cal.getTime();

			if (dayCounts.containsKey(oneDay)) {
				dayCounts.put(oneDay, dayCounts.get(oneDay) + 1);
			} else {
				dayCounts.put(oneDay, 1);
			}

		}

//		System.out.println("开始日期："+startTime);
//		System.out.println("结束日期："+endTime);
//		System.out.println("前后相差："+dayLag);
//		System.out.println("按天统计信息:");
//		for(Date date:dayCounts.keySet()) {
//			System.out.println(date+"---"+dayCounts.get(date));
//		}

	}

	public List<Double> getPercents(List<Integer> list) {
		int total = 0;
		List<Double> ratios = new ArrayList<>();
		for (Integer i : list) {
			total += i;
		}
		for (Integer i : list) {
			double percent = (int) (((double) i) / total * 1000) / 1000.0;
			ratios.add(percent);
		}
		return ratios;
	}

	/**
	 * 统计单车出现的次数，返回的list表示从零开始，单车移动的次数，如果为零，表示一次没动过
	 * 
	 * @param data
	 * @return
	 */
	private List<Integer> getMoveCount(Map<String, List<LnglatTime>> data) {
		int[] counts = new int[1000];
		for (String s : data.keySet()) {
			List<LnglatTime> bkList = data.get(s);
			counts[bkList.size() - 1]++;
		}
		List<Integer> result = new ArrayList<>();
		// 下面的是为了把如果连续出现若干个零，就说明后面基本都是零，没有必要再统计.
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] != 0) {
				result.add(counts[i]);
			} else {
				boolean flag = true;
				for (int j = i; j < i + 5 && j < 1000; j++) {
					if (counts[j] != 0) {
						flag = false;
					}
				}
				if (flag) {
					break;
				} else {
					result.add(counts[i]);
				}
			}
		}
		return result;
	}

	/**
	 * 运行所有单车文件，捕捉单车的运行轨迹
	 * 
	 * @return 返回值，key-单车ID，list-单车轨迹和时间
	 */
	private Map<String, List<LnglatTime>> getBikesToDay() {
		List<Path> allFiles = FilesUtil.listAllFiles(true);
		FilesUtil.sortFiles(allFiles);
		Map<String, List<LnglatTime>> bikes = new HashMap<>();

		String bikeId = null;
		for (Path p : allFiles) {
			System.out.println(p);
			Map<String, Object> file = FilesUtil.readFileToBikeMap(p.toString());

			BikeHeader header = (BikeHeader) file.get("header");
			List<BikePos> fileBikes = (List<BikePos>) file.get("bikes");
			Date time = header.getStartTime();

			for (BikePos b : fileBikes) {
				bikeId = b.getBikeID();
				if (bikes.containsKey(bikeId)) {
					List<LnglatTime> posList = bikes.get(bikeId);
					if (!posList.isEmpty()) {
						LnglatTime lastPos = posList.get(posList.size() - 1);
						LnglatTime pos = new LnglatTime(b.getLng(), b.getLat(), time);
						// 检查上次和本次的距离，如果距离很少，表示没有移动过
						if (CoordsUtil.calcuDist(lastPos.getLng(), lastPos.getLat(), pos.getLng(),
								pos.getLat()) > LAST_NOW_DISTANCE) {
							posList.add(pos);
						}
					} else {
						LnglatTime pos = new LnglatTime(b.getLng(), b.getLat(), time);
						posList.add(pos);
					}
				} else {
					List<LnglatTime> posList = new ArrayList<>();
					LnglatTime pos = new LnglatTime(b.getLng(), b.getLat(), time);
					posList.add(pos);
					bikes.put(bikeId, posList);
				}
			}
		}
		return bikes;
	}
}
