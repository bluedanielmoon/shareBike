package com.execute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helper.HoliDayHelper;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.CircumState;
import com.pojo.Lnglat;
import com.pojo.PredictResult;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;
import com.util.MathUtil;

/**
 * 对于数据，提取工作日，天气，温度等信息
 * 
 * @author daniel
 *
 */

@Component
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

	private CircumState analyzeHeader(BikeHeader header) {
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
	private void giteSiteBikes(List<BikePos> bikes, Map<Integer, Map<String, Object>> sitesInfos, int count) {

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
	private void initSiteAreas(Map<Integer, Map<String, Object>> sitesInfos, List<Site> sites, int count) {

		for (Site s : sites) {
			Lnglat lnglat = new Lnglat(s.getLng(), s.getLat());
			BikeArea area = CoordsUtil.getCenterArea(lnglat);

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
	public Map<Date, List<Map<String, Object>>> produceSiteBikes() {
		Date[] range = FilesUtil.getFileRange();
		Date start = range[0];
		Date enDate = range[1];

		Date temp = start;
		Calendar calendar = Calendar.getInstance();

		Map<Date, List<Map<String, Object>>> dateFiles = new TreeMap<>();
		List<Path> paths = FilesUtil.listAllFiles(true);
		FilesUtil.sortFiles(paths);
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

		Map<Date, CircumState> circums = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {

				return o1.compareTo(o2);
			}
		});
		for (int i = 0; i < paths.size(); i++) {
			Map<String, Object> bikeFile = FilesUtil.readFileInfo(paths.get(i).toString());

			BikeHeader header = (BikeHeader) bikeFile.get("header");
			CircumState circum = analyzeHeader(header);

			circums.put(header.getStartTime(), circum);

			List<BikePos> bikes = (List<BikePos>) bikeFile.get("bikes");
			giteSiteBikes(bikes, sitesInfos, i);

		}

		MapperUtil.writeIntMapMapData("./siteBikes.txt", sitesInfos);

		MapperUtil.writeMapData("./circumList.txt", circums, Date.class, CircumState.class);

		return dateFiles;
	}

	/**
	 * 根据某一个日期和该日期之前的天数获取siteBikes里面关于单车信息的数据
	 * 
	 * @param askDate
	 * @param daysBefore
	 */
	private Map<Integer, Map<Date, List<Integer>>> getDataByDateAndBefore(Date askDate, int daysBefore) {
		List<Date> askDates = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		Date startDate = null;
		for (int i = 0; i <= daysBefore; i++) {

			calendar.setTime(askDate);
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - (daysBefore - i));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			askDates.add(calendar.getTime());
			if (i == 0) {
				startDate = calendar.getTime();
			}
		}

		List<Site> sites = siteServ.getAllSites();
		Map<Integer, Map<String, Object>> sitesInfo = getSiteBikes();
		List<Date> dates = getDates();

		int dateCount = 0;
		for (int i = 0; i < dates.size(); i++) {
			if (dates.get(i).compareTo(startDate) == 0) {
				dateCount = i;
				break;
			}
		}
		Map<Integer, Map<Date, List<Integer>>> sitesNeed = new HashMap<>();
		for (Site site : sites) {
			Map<String, Object> siteData = sitesInfo.get(site.getId());
			List<Integer> bikes = (List<Integer>) siteData.get("bikes");

			Map<Date, List<Integer>> dailyInfo = new TreeMap<>();
			int count = 0;
			for (int i = dateCount; i <= dateCount + daysBefore; i++, count++) {
				List<Integer> ls = bikes.subList(i * 24, (i + 1) * 24);
				dailyInfo.put(askDates.get(count), ls);
			}
			sitesNeed.put(site.getId(), dailyInfo);
		}
		return sitesNeed;
	}

	public List<Integer> estimate(Date askDate, int siteID) {
		int daysBefore = 2;
		Map<Integer, Map<Date, List<Integer>>> sitesNeed = getDataByDateAndBefore(askDate, daysBefore);

		Map<Date, List<Integer>> siteBefore = sitesNeed.get(siteID);
		List<Integer> result = siteBefore.get(askDate);

		return result;

	}

	/**
	 * 给定一个日期，以及日期之前的天数，计算该日期前面若干天相同时段的活跃度
	 * 
	 * @param askDate    日期不管小时数
	 * @param daysBefore 表示最近 x 天
	 * @return
	 */
	public Map<Integer, double[]> siteActives(Date askDate, int daysBefore) {
		Map<Integer, Map<Date, List<Integer>>> sitesNeed = getDataByDateAndBefore(askDate, daysBefore);
		Map<Integer, double[]> sitesRates = new HashMap<>();

		for (Integer siteId : sitesNeed.keySet()) {
			Map<Date, List<Integer>> dailyInfo = sitesNeed.get(siteId);
			double[] dayRates = new double[24];

			for (Date date : dailyInfo.keySet()) {
				List<Integer> dayList = dailyInfo.get(date);
				List<Double> dayRate = analyzeActive(dayList);
				for (int i = 0; i < dayRate.size(); i++) {
					dayRates[i] += dayRate.get(i);
				}
			}
			for (int i = 0; i < dayRates.length; i++) {
				dayRates[i] /= dailyInfo.size();
				dayRates[i] = (int) (dayRates[i] * 100) / 100.0;
			}
			sitesRates.put(siteId, dayRates);
		}
		return sitesRates;
	}

	private List<Double> analyzeActive(List<Integer> dayList) {
		double temp = 0;
		int max = dayList.get(0);
		int min = dayList.get(0);
		for (int i = 0; i < dayList.size(); i++) {
			if (dayList.get(i) > max) {
				max = dayList.get(i);
			}
			if (dayList.get(i) < min) {
				min = dayList.get(i);
			}
		}
		int lag = max - min;
		List<Double> rates = new ArrayList<>();
		for (int i = 0; i < dayList.size() - 1; i++) {
			temp = (int) (Math.abs((double) (dayList.get(i) - dayList.get(i + 1))) / lag * 100) / 100.0;
			rates.add(temp);
		}
		return rates;
	}

	/**
	 * 给定一个站点ID，返回这个站点每一天的变化情况和平均变化情况的差
	 * 
	 * @param siteID
	 * @return
	 */
	public double[] analyzeSiteSimilar(int siteID) {
		// 对avg求取最大最小的差值，然后avgRatio代表每个值除以差值
		double[] avgs = analyzeSiteChange(siteID);
		double avgMax = MathUtil.maxVal(avgs);
		double avgMin = MathUtil.minVal(avgs);
		double avgLag = avgMax - avgMin;
		double[] avgRatio = new double[avgs.length];
		for (int i = 0; i < avgs.length; i++) {
			avgRatio[i] = avgs[i] / avgLag;
		}
		Map<Integer, Map<String, Object>> sitesInfo = getSiteBikes();
		Map<String, Object> siteData = sitesInfo.get(siteID);
		List<Integer> bikes = (List<Integer>) siteData.get("bikes");
		List<Date> dates = getDates();
		Map<Date, List<Integer>> dailyInfo = new TreeMap<>();
		for (int i = 0; i < dates.size(); i++) {
			dailyInfo.put(dates.get(i), bikes.subList(i * 24, (i + 1) * 24));
		}
		int[] dayTemp = new int[24];
		double[] dayRaio = new double[24];
		double[] dayMinus = new double[24];

		List<Double> similars = new ArrayList<>();
		double daySimi = 0;
		for (Date date : dailyInfo.keySet()) {
			List<Integer> dayList = dailyInfo.get(date);
			daySimi = 0;
			Integer dayMax = dayList.get(0);
			Integer dayMin = dayList.get(0);
			for (int i = 0; i < dayList.size(); i++) {
				dayTemp[i] = dayList.get(i);
				if (dayTemp[i] > dayMax) {
					dayMax = dayTemp[i];
				}
				if (dayTemp[i] < dayMin) {
					dayMin = dayTemp[i];
				}
			}
			double dayLag = dayMax - dayMin;
			for (int i = 7; i < dayList.size(); i++) {
				dayRaio[i] = dayTemp[i] / dayLag;
				dayMinus[i] = Math.abs(dayRaio[i] - avgRatio[i]);
				daySimi += dayMinus[i];

			}
			daySimi = ((int) (daySimi * 100)) / 100.0;
			similars.add(daySimi);
		}
		return null;
	}

	/**
	 * 给定一个站点ID，返回这个站点的历史平均变化率
	 * 
	 * @param siteID
	 * @return
	 */
	public double[] analyzeSiteChange(int siteID) {
		Map<Integer, Map<String, Object>> sitesInfo = getSiteBikes();
		Map<String, Object> siteData = sitesInfo.get(siteID);
		List<Integer> bikes = (List<Integer>) siteData.get("bikes");

		List<Date> dates = getDates();
		int dayCount = dates.size();
		Map<Date, List<Integer>> dailyInfo = new TreeMap<>();
		for (int i = 0; i < dates.size(); i++) {
			dailyInfo.put(dates.get(i), bikes.subList(i * 24, (i + 1) * 24));
		}

		double[] dayAverage = new double[24];
		for (Date date : dailyInfo.keySet()) {
			List<Integer> dayList = dailyInfo.get(date);
			for (int i = 0; i < dayList.size(); i++) {
				dayAverage[i] += dayList.get(i);
			}
		}
		double min = dayAverage[0];
		for (int i = 0; i < 24; i++) {
			dayAverage[i] /= dayCount;
			dayAverage[i] = ((int) (dayAverage[i] * 10)) / 10.0;
			if (dayAverage[i] < min) {
				min = dayAverage[i];
			}
		}
		for (int i = 0; i < 24; i++) {
			dayAverage[i] -= min;
			dayAverage[i] = ((int) (dayAverage[i] * 10)) / 10.0;
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
	 * 返回一天的环境情况，输入参数无视小时
	 * 
	 * @param date
	 * @return
	 */
	public List<CircumState> getDayCircums(Date date) {
		Map<Date, CircumState> allCircums = getCircums();

		List<CircumState> oneDay = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Date temp = null;
		for (int i = 0; i < 24; i++) {
			temp = calendar.getTime();
			oneDay.add(allCircums.get(temp));
			calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
		}
		return oneDay;
	}

	public Map<Integer, Map<String, Object>> getSiteBikes() {
		return MapperUtil.readIntMapMapData("./siteBikes.txt");
	}

	/**
	 * 返回24小时乘以天数的环境状况文件
	 * 
	 * @return
	 */
	public Map<Date, CircumState> getCircums() {
		return MapperUtil.readMapData("./circumList.txt", Date.class, CircumState.class);
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
	
	public PredictResult getSitePredict(int siteID) {
		List<PredictResult> results=readPredict();
		for(PredictResult pre:results) {
			if (pre.getId()==siteID) {
				return pre;
			}
		}
		return null;
	}
	
	public List<PredictResult> readPredict() {

		String fileName = "/Users/daniel/softs/pythonWorks/predict.json";
		Path path = Paths.get(fileName);
		if (Files.exists(path)) {
			List<PredictResult> results=new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			try {

				JsonNode root = mapper.readTree(path.toFile()).findValue("0");
				Iterator<Entry<String, JsonNode>> iterator = root.fields();
				while (iterator.hasNext()) {
					Entry<String, JsonNode> site = iterator.next();

					PredictResult pre = new PredictResult();
					pre.setId(Integer.parseInt(site.getKey()) + 1);
					Iterator<JsonNode> vals=site.getValue().elements();
					List<Integer> nums=new ArrayList<>();
					while(vals.hasNext()) {
						nums.add(vals.next().asInt());
					}
					pre.setResults(nums);
					results.add(pre);
				}
				return results;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		SiteAnalyze aSiteAnalyze = new SiteAnalyze();
		aSiteAnalyze.readPredict();
	}
}
