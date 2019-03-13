package com.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.init.FileName;
import com.pojo.CircumState;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.DateUtil;
import com.util.MapperUtil;

@Component
public class SiteCircum {

	@Autowired
	SiteServ siteServ;
	
	@Autowired
	SiteAnalyze siteAnalyze;
	// work:1-work ,0-notWork
	// weather:1-canGo,0-cannot Go

	public List<Integer> analyzeWorkForAllSites() {
		List<Site> sites=siteServ.getAllSites();
		
		double score=0;
		List<Integer> underZero=new ArrayList<>();
		for(Site site:sites) {
			score=analyzeWork(site.getId());
			if (score>0&&score<1) {
				underZero.add(site.getId());
			}
		}
		return underZero;
	}
	
	public double analyzeWork(int siteID) {

		int[] workDays=getAvgWorkData(siteID,1);
		int[] noWorkDays=getAvgWorkData(siteID, 0);
		double[] compRatio=new double[24];
		double totalRatio=0;
		int count=0;
		for(int i=0;i<compRatio.length;i++) {
			if(workDays[i]!=0&&noWorkDays[i]!=0) {
				compRatio[i]=(double)workDays[i]/noWorkDays[i];
				totalRatio+=compRatio[i];
				count++;
			}
		}

		double avgRatio=0;
		if (count!=0) {
			avgRatio=(int)(totalRatio/count*100)/100.0;
		}
		return avgRatio;
//		System.out.println(Arrays.toString(workDays));
//		System.out.println(Arrays.toString(noWorkDays));
//		System.out.println(Arrays.toString(compRatio));
//		System.out.println(avgRatio);
	}
	
	public void getWeather(int siteID,Date askDate) {
		List<Integer> index=new ArrayList<>();
		
		Map<Date, CircumState> filters=filterCircums(false, false, false,
				false, 0, 0, 0, 0,index);
		Map<Integer, Map<String, Object>> sitesInfo = MapperUtil.readIntMapMapData(FileName.SITE_BIKES);

		Map<String, Object> site = sitesInfo.get(siteID);

		Map<Date, List<Integer>> seriesData=new TreeMap<>();
		Map<Date, Double> seriesTemp=new TreeMap<>();
		Iterator<Date> dateIter=filters.keySet().iterator();
		
		List<Integer> bikes = (List<Integer>) site.get("bikes");
		Date date=null;
		Date firstDate=null;
		int avgTemp=0;
		CircumState circum=null;
		for(int i=0;i<index.size();i+=24) {
			avgTemp=0;
			List<Integer> dayBikes=new ArrayList<>();
			for(int j=i;j<(i+24)&&j<index.size();j++) {
				dayBikes.add(bikes.get(j));
			}
			firstDate=dateIter.next();
			
			
			circum=filters.get(firstDate);
			avgTemp+=circum.getTemp();
			seriesData.put(firstDate, dayBikes);
			for(int j=0;j<23;j++) {
				date=dateIter.next();
				avgTemp+=filters.get(date).getTemp();
			}
			double temp=(int)(avgTemp/24.0*10)/10.0;
			System.out.println(temp);
			seriesTemp.put(firstDate, temp);
			if (DateUtil.isSameDay(firstDate, askDate)) {
				System.out.println(dayBikes);
			}
		}
	}
	public int[] getAvgWorkData(int siteID,int checkWork) {
		List<Integer> index=new ArrayList<>();
		boolean specHour=false;
		
		Map<Date, CircumState> filters=filterCircums(specHour, true, false,
				false, 0, checkWork, 0, 0,index);
		
		Map<Integer, Map<String, Object>> sitesInfo = MapperUtil.readIntMapMapData(FileName.SITE_BIKES);

		Map<String, Object> site = sitesInfo.get(siteID);

		Map<Date, List<Integer>> seriesData=new TreeMap<>();
		Iterator<Date> dateIter=filters.keySet().iterator();
		
		List<Integer> bikes = (List<Integer>) site.get("bikes");
		for(int i=0;i<index.size();i+=24) {
			List<Integer> dayBikes=new ArrayList<>();
			for(int j=i;j<(i+24)&&j<index.size();j++) {
				dayBikes.add(bikes.get(j));
			}
			seriesData.put(dateIter.next(), dayBikes);
			for(int j=0;j<23;j++) {
				dateIter.next();
			}
		}

		return checkAvrage(seriesData);
				
	}
	private int[] checkAvrage(Map<Date, List<Integer>> seriesData) {
		int[] total=new int[24];
		for(Date d:seriesData.keySet()) {
			List<Integer> bikes=seriesData.get(d);
			for(int i=0;i<bikes.size();i++) {
				total[i]+=bikes.get(i);
			}
			
		}

		double days=seriesData.size();
		int[] avg=new int[24];
		for(int i=0;i<24;i++) {
			avg[i]=(int)(total[i]/days);
		}
		return avg;
	}
	public Map<Date, CircumState>  filterCircums(boolean isHour,boolean isWork,boolean isWeath,boolean isTemp,
			int theHour,int theWork,int theWeather,int theTemp,List<Integer> indexList) {
		CircumState CircumState=null;
		Map<Date, CircumState> circums=siteAnalyze.getCircums();
		Map<Date, CircumState> filter=new TreeMap<>();
		
		int count=-1;
		for(Date date:circums.keySet()) {
			count++;
			CircumState=circums.get(date);
			if (isHour) {
				if (CircumState.getHour()!=theHour) {
					continue;
				}
			}
			if (isWork) {
				if (CircumState.getWorkDay()!=theWork) {
					continue;
				}
			}
			if (isWeath) {
				if (CircumState.getWeather()!=theWeather) {
					continue;
				}
			}
			if (isTemp) {
				if (CircumState.getTemp()!=theTemp) {
					continue;
				}
			}
			filter.put(date, CircumState);
			indexList.add(count);
		
		}
		return filter;
	}
	
//	public void analyzeWorkDay() {
//		
//	}
	
}
