package com.execute;

import java.nio.file.Path;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
=======
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helper.HoliDayHelper;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.CircumState;
import com.pojo.Lnglat;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;
<<<<<<< HEAD
import com.util.MathUtil;

=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
/**
 * 对于数据，提取工作日，天气，温度等信息
 * 
 * @author daniel
 *
 */

<<<<<<< HEAD
@Component
=======
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
public class SiteAnalyze {

	int workDay = 1;
	int noWorkDay = 0;
	@Autowired
	SiteServ siteServ;

	// 多云，晴间多云，小雨，晴，阴
	private int judgeWeather(String weather) {
		if (weather.length() > 0) {
			if (weather.contains("雨") || weather.contains("雪")) {
				return 0;
			} else {
				return 1;
			}
		}
		return -1;
	}

	private int judgeTemperature(int temparature) {
		if (temparature >= 10 && temparature <= 25) {
			return 1;
		} else if ((temparature >= 5 && temparature < 10) || (temparature > 25 && temparature < 30)) {
			return 2;
		} else {
			return 3;
		}
	}

<<<<<<< HEAD
	private CircumState analyzeHeader(BikeHeader header) {
=======
	public CircumState analyzeHeader(BikeHeader header) {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		Date date = header.getStartTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		CircumState cState = new CircumState();
		cState.setHour(hour);
		if (HoliDayHelper.isWorkDay(date)) {
			cState.setWorkDay(workDay);
		} else {
			cState.setWorkDay(noWorkDay);
		}
		String weather = header.getWeather().getWeather();
		cState.setWeather(judgeWeather(weather));
		cState.setTemp(judgeTemperature(header.getWeather().getTempature()));

		return cState;
	}

	/**
	 * 采用每个单车和所有站点挨个比对的本办法，效率差精度高
	 * 
	 * @param bikes
	 * @param sitesInfos
	 * @param count
	 */
<<<<<<< HEAD
	private void giteSiteBikes(List<BikePos> bikes, Map<Integer, Map<String, Object>> sitesInfos, int count) {
=======
	public void giteSiteBikes(List<BikePos> bikes, Map<Integer, Map<String, Object>> sitesInfos, int count) {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

		for (BikePos bike : bikes) {

			for (Integer i : sitesInfos.keySet()) {
				Map<String, Object> info = sitesInfos.get(i);
				BikeArea area = (BikeArea) info.get("area");
				if (CoordsUtil.isInArea(area, bike.getLng(), bike.getLat())) {
					List<Integer> ls = (List<Integer>) info.get("bikes");
					ls.set(count, ls.get(count) + 1);
					break;
				}
			}
		}
	}

	/**
	 * 根据站点的坐标生成关于站点的信息map,
	 * 
	 * @param sitesInfos 包含site,area,bikes
	 * @param sites
	 */
<<<<<<< HEAD
	private void initSiteAreas(Map<Integer, Map<String, Object>> sitesInfos, List<Site> sites, int count) {
=======
	public void initSiteAreas(Map<Integer, Map<String, Object>> sitesInfos, List<Site> sites, int count) {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		int dist = 50;
		for (Site s : sites) {
			Lnglat lnglat = new Lnglat(s.getLng(), s.getLat());
			BikeArea area = CoordsUtil.getCenterArea(lnglat, dist);

			Map<String, Object> info = new HashMap<>();
			info.put("site", s);
			info.put("area", area);
			List<Integer> ls = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				ls.add(0);
			}
			info.put("bikes", ls);
			sitesInfos.put(s.getId(), info);
		}
	}

	/**
	 * 生成站点的单车采集文件，和采集文件对应的CircumState信息文件
	 * 
	 * @param check24 是否检查文件符合24个小时采集数量
	 * @return
	 */
<<<<<<< HEAD
	public Map<Date, List<Map<String, Object>>> produceSiteBikes() {
=======
	public Map<Date, List<Map<String, Object>>> getFileHeaders() {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		Date[] range = FilesUtil.getFileRange();
		Date start = range[0];
		Date enDate = range[1];

		Date temp = start;
		Calendar calendar = Calendar.getInstance();

		Map<Date, List<Map<String, Object>>> dateFiles = new TreeMap<>();
		List<Path> paths = FilesUtil.listAllFiles(true);
		while (temp.before(enDate)) {
			List<Map<String, Object>> ls = new ArrayList<>();
			dateFiles.put(temp, ls);

			calendar.setTime(temp);
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
			temp = calendar.getTime();
		}
		List<Site> allSites = siteServ.getAllSites();

		Map<Integer, Map<String, Object>> sitesInfos = new HashMap<>();

		initSiteAreas(sitesInfos, allSites, paths.size());

<<<<<<< HEAD
		Map<Date, CircumState> circums = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {

				return o1.compareTo(o2);
			}
		});
=======
		List<CircumState> circums = new ArrayList<>();
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		for (int i = 0; i < paths.size(); i++) {
			Map<String, Object> bikeFile = FilesUtil.readFileInfo(paths.get(i).toString());

			BikeHeader header = (BikeHeader) bikeFile.get("header");
			CircumState circum = analyzeHeader(header);
<<<<<<< HEAD

			circums.put(header.getStartTime(), circum);
=======
			circums.add(circum);
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

			List<BikePos> bikes = (List<BikePos>) bikeFile.get("bikes");
			giteSiteBikes(bikes, sitesInfos, i);
			System.out.println("finish" + paths.get(i).toString());

		}

		MapperUtil.writeIntMapMapData("./siteBikes.txt", sitesInfos);

<<<<<<< HEAD
		MapperUtil.writeMapData("./circumList.txt", circums, Date.class, CircumState.class);

		return dateFiles;
	}

	public double[] analyzeSiteChange(int siteID) {

		int starthour = 8;
		List<Integer> startHistory = getEveryDaySitesCircums(siteID, starthour);

		int maxStart = MathUtil.getListMax(startHistory);
		Map<Integer, Map<String, Object>> sitesInfo = getSiteBikes();
		Map<String, Object> siteData = sitesInfo.get(siteID);
		List<Integer> bikes = (List<Integer>) siteData.get("bikes");

		List<Date> dates = getDates();
		int dayCount = dates.size();
		System.out.println(dayCount);
		Map<Date, List<Integer>> dailyInfo = new TreeMap<>();
		for (int i = 0; i < dates.size(); i++) {
			List<Integer> ls = new ArrayList<>();
			ls = bikes.subList(i * 24, (i + 1) * 24);
			dailyInfo.put(dates.get(i), ls);
		}

		double[] dayAverage = new double[24];
		for (Date date : dailyInfo.keySet()) {
			List<Integer> dayList = dailyInfo.get(date);
			int dayPlus = maxStart - dayList.get(0);
			for (int i = 0; i < dayList.size(); i++) {
				dayList.set(i, dayList.get(i) + dayPlus);
				dayAverage[i] += dayList.get(i);
			}
		}
		double min=dayAverage[0];
		for (int i = 0; i < 24; i++) {
			dayAverage[i] /= dayCount;
			dayAverage[i] = ((int) (dayAverage[i] * 10)) / 10.0;
			if(dayAverage[i]<min) {
				min=dayAverage[i];
			}
		}
		for (int i = 0; i < 24; i++) {
			dayAverage[i]-=min;
			
			dayAverage[i]=((int) (dayAverage[i] * 10)) / 10.0;
			System.out.print( dayAverage[i] + " ");
		}

		return dayAverage;

	}

	public List<Integer> getSitesCircums(CircumState circumState, int id) {

		Map<Integer, Map<String, Object>> sitesInfo = MapperUtil.readIntMapMapData("./siteBikes.txt");

		Map<Date, CircumState> list = (Map<Date, CircumState>) MapperUtil.readMapData("./circumList.txt", Date.class,
				CircumState.class);

		Map<String, Object> site = sitesInfo.get(id);

		List<Integer> bikes = (List<Integer>) site.get("bikes");

		List<Integer> result = new ArrayList<>();

		int count = 0;
		for (Date d : list.keySet()) {
			CircumState cState = list.get(d);
			if (cState.equals(circumState)) {
				result.add(bikes.get(count));
			}
			count++;
		}
		return result;
	}

	/**
	 * 返回24小时乘以天数的环境状况文件
	 * 
	 * @return
	 */
	public Map<Date, CircumState> getCircums() {
		return MapperUtil.readMapData("./circumList.txt", Date.class, CircumState.class);
	}

	public Map<Integer, Map<String, Object>> getSiteBikes() {
		return MapperUtil.readIntMapMapData("./siteBikes.txt");
	}

	public List<Date> getDates() {
		Map<Date, CircumState> circums = getCircums();
		List<Date> dates = new ArrayList<>();

		Iterator<Date> dateKeys = circums.keySet().iterator();
		int count = 0;
		while (dateKeys.hasNext()) {
			if (count == 0) {
				dates.add(dateKeys.next());
			} else {
				dateKeys.next();
			}
			count++;
			if (count == 24) {
				count = 0;
			}
		}
		return dates;
	}

	public List<Integer> getEveryDaySitesCircums(int id, int hour) {

		Map<Integer, Map<String, Object>> sitesInfo = MapperUtil.readIntMapMapData("./siteBikes.txt");

		Map<String, Object> site = sitesInfo.get(id);

		List<Integer> bikes = (List<Integer>) site.get("bikes");

		List<Integer> result = new ArrayList<>();

		for (int i = hour; i < bikes.size(); i += 24) {
			result.add(bikes.get(i));
		}

		return result;
	}

	public static void main(String[] args) {

=======
		MapperUtil.writeListData("./circumList.txt", circums, CircumState.class);

		return dateFiles;
	}
	
	public List<Integer> getSitesCircums(CircumState circumState, int id) {
		
		Map<Integer, Map<String, Object>> sitesInfo = MapperUtil.readIntMapMapData("./siteBikes.txt");

		List<CircumState> list = MapperUtil.readListData("./circumList.txt", CircumState.class);
		
		Map<String, Object> site=sitesInfo.get(id);
		
		List<Integer> bikes=(List<Integer>) site.get("bikes");

		List<Integer> result=new ArrayList<>();
		for(int i=0;i<list.size();i++) {
			CircumState cState=list.get(i);
			if(cState.equals(circumState)) {
				result.add(bikes.get(i));
			}
		}
		return result;
	}
	
	public List<Integer> getEveryDaySitesCircums(int id,int hour) {
		
		Map<Integer, Map<String, Object>> sitesInfo = MapperUtil.readIntMapMapData("./siteBikes.txt");

		
		Map<String, Object> site=sitesInfo.get(id);
		
		List<Integer> bikes=(List<Integer>) site.get("bikes");

		List<Integer> result=new ArrayList<>();
		
		for(int i=hour;i<bikes.size();i+=24) {
			result.add(bikes.get(i));
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		SiteAnalyze siteAnalyze= new SiteAnalyze();
		for(int i=0;i<24;i++) {
			List<Integer> ls=siteAnalyze.getEveryDaySitesCircums(266,i);
			System.out.println(ls);
		}
		

		
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
	}

	public void createDefaultCircums() {
		Map<Integer, List<CircumState>> allCircums = new HashMap<>();

		for (int i = 0; i < 24; i++) {
			List<CircumState> lCircumStates = new ArrayList<>();
			for (int j = 0; j < 2; j++) {

				for (int k = 0; k < 2; k++) {

					for (int l = 0; l < 3; l++) {
						CircumState circumState = new CircumState();
						circumState.setHour(i);
						circumState.setWorkDay(j);
						circumState.setWeather(k);
						circumState.setTemp(l);
						lCircumStates.add(circumState);
					}
				}
			}
			allCircums.put(i, lCircumStates);
		}
		MapperUtil.writeMapListData("./defaultCircums.txt", allCircums, Integer.class, CircumState.class);
	}

}
