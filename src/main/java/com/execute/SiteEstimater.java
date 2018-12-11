package com.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.init.State;
import com.pojo.CircumState;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.MathUtil;

@Component
public class SiteEstimater {
	
	

	@Autowired
	private SiteAnalyze analyze;
	
	@Autowired
	private SiteServ siteServ;
	
	
	/**
	 * 给一组数字，给一个分割数，将该组数字最大值最小值分割为divide个，然后
	 * 计算每个区间的分布数量，求取最大分布的数量，然后将numList的属于该区间的
	 * 数求平均数
	 * @param numList
	 * @param divide
	 * @return
	 */
	public double getDataFocus(List<Double> numList) {
		if(numList.size()==0) {
			return 0;
		}		
		numList.sort(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				
				return o1.compareTo(o2);
			}
		});
		int divide=(numList.size()+1)/2;
		double max=numList.get(numList.size()-1);
		double min=numList.get(0);
		double unitLag=(max-min)/divide;
		int[] distri=new int[divide+1];
		if(unitLag<=0) {
			return 0;
		}
		for(Double  d:numList) {
			int index=(int) ((d-min)/unitLag);
			distri[index]++;
		}
		int maxDistri=0;
		int comp=0;
		for(int i=0;i<distri.length;i++) {
			if(distri[i]>comp) {
				comp=distri[i];
				maxDistri=i;
			}
		}
		double rangeLeft=min+maxDistri*unitLag;
		double rangeRight=min+(maxDistri+1)*unitLag;
		List<Double> inRange=new ArrayList<>();
		double totalInrange=0;
		for(Double d:numList) {
			if((d-rangeLeft)>0.00001&&(rangeRight-d)>0.00001) {
				inRange.add(d);
				totalInrange+=d;
			}
		}
		if(inRange.size()>0) {
			return (int)(totalInrange/inRange.size()*100)/100.0;
		}else {
			return 0;
		}
	}
	
	public Map<Integer, Map<String, Object>> estimateAllSites(int theHour) {
		List<Site> allSites=siteServ.getAllSites();
		Map<Integer, Map<String, Object>> result=new HashMap<>();
		
		for(int i=0;i<allSites.size();i++) {
			Site xSite=allSites.get(i);
			Map<String, Object> estResult=estimateByHistory(xSite, theHour, null, null);
			result.put(xSite.getId(), estResult);
		}
		return result;
	}

	/**
	 * 
	 * @param site
	 * @param theHour
	 * @param date
	 * @param circum
	 * @return Map<String, Object> trend(增还是减),rate(变化的大小),confidence(自信程度)
	 */
	public Map<String, Object> estimateByHistory(Site site,int theHour,Date date,CircumState circum) {
		
		boolean doDate=true;
		if(date==null) {
			doDate=false;
		}
		boolean docircum=true;
		if(circum==null) {
			docircum=false;
		}
		
		int nextHour=theHour+1;
		List<Integer> HourHistory = analyze.getEveryDaySitesCircums(site.getId(), theHour);
		List<Integer> nextHourHistory = analyze.getEveryDaySitesCircums(site.getId(), nextHour);
		Map<Date, CircumState> circumStates = analyze.getCircums();

		int count = HourHistory.size();
		List<Integer> growList=new ArrayList<>();
		List<Double> growRate=new ArrayList<>();
		List<Integer> reduceList=new ArrayList<>();
		List<Double> reduceRate=new ArrayList<>();
		Calendar calendar=Calendar.getInstance();
		Iterator<Date> dates=circumStates.keySet().iterator();
		Date temp=null;
		for (int i = 0; i < count; i++) {
			int hourCount = HourHistory.get(i);
			int nextCount = nextHourHistory.get(i);
			CircumState nowCircum=null;
			boolean goOn=true;
			
			for(int j=0;j<24;j++) {
				temp=dates.next();
				calendar.setTime(temp);
				if(calendar.get(Calendar.HOUR_OF_DAY)==theHour) {
					nowCircum=circumStates.get(temp);
				}				
				//是否只考虑参数日期之前的
				if(doDate) {
					if(temp.after(date)) {
						goOn=false;
					}
				}else {
					goOn=true;
				}
				
			}
			if(!goOn) {
				break;
			}
			//是否考虑环境因素
			if(docircum) {
				if(nowCircum.equalsNoTemp(circum)) {
					if(hourCount<nextCount) {
						growList.add(i);
						double rate=(double)(nextCount-hourCount)/hourCount;
						growRate.add(rate);
					}else if(hourCount>nextCount){
						reduceList.add(i);
						double rate=(double)(hourCount-nextCount)/hourCount;
						reduceRate.add(rate);
					}		
				}
			}else {
				if(hourCount<nextCount) {
					growList.add(i);
					double rate=(double)(nextCount-hourCount)/hourCount;
					growRate.add(rate);
				}else if(hourCount>nextCount){
					reduceList.add(i);
					double rate=(double)(hourCount-nextCount)/hourCount;
					reduceRate.add(rate);
				}	
			}
		}
		double growFocus=getDataFocus(growRate);
		double reduceFocus=getDataFocus(reduceRate);
		int total=growList.size()+reduceList.size();
		double growConfidence=(double)(growList.size())/total;
		double reduceConfidence=(double)(reduceList.size())/total;
//		System.out.println("增加趋势的天数:"+growList.size()+"  百分比"+growConfidence);
//		System.out.println("growFocus"+growFocus);
//		System.out.println("growRate"+growRate);
//		System.out.println("减少趋势的天数:"+reduceList.size()+"  百分比"+reduceConfidence);
//		System.out.println("reduceFocus"+reduceFocus);
//		System.out.println("reduceRate"+reduceRate);
		Map<String, Object> score=new HashMap<>();
		if(growList.size()>reduceList.size()) {
			score.put("trend",State.GROW_TREND );
			score.put("rate",growFocus );
			score.put("confidence",growConfidence );
		}else {
			score.put("trend",State.REDUCE_TREND );
			score.put("rate",reduceFocus );
			score.put("confidence",reduceConfidence );
		}
		
		return score;
	}
}
