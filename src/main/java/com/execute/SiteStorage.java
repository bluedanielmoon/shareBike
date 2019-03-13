package com.execute;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.init.State;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.MathUtil;

@Component
public class SiteStorage {

	@Autowired
	SiteServ siteServ;
	
	@Autowired
	SiteAnalyze siteAnalyze;
	
	/**
	 * 更改数据库的站点数量数据
	 */
	public void changeSiteStorage() {
		List<Site> sites=siteServ.getAllSites();
		Map<Integer, Integer> siteStorage=decideStorage();
		
		int siteID=0;
		for(Site site:sites) {
			siteID=site.getId();
			if (siteStorage.containsKey(siteID)) {
				site.setVolume(siteStorage.get(siteID));
				
			}else {
				site.setVolume(State.SITE_STORAGE_NUM);
			}

			siteServ.updateSite(site);
		}
	}
	
	public Map<Integer, Integer> decideStorage() {

		Map<Integer, Map<String, Object>> sitesInfo = siteAnalyze.getSiteBikes();
		Map<Integer, Integer> siteStorage=new HashMap<>();
		for(Integer siteID:sitesInfo.keySet()) {
			Map<String, Object> siteData = sitesInfo.get(siteID);
			List<Integer> bikes = (List<Integer>) siteData.get("bikes");
			double siteScore=calcuOneSite(bikes);
			int siteNum= (int) Math.round(siteScore*2);
			siteNum=(siteNum/10+1)*10;
			siteStorage.put(siteID, siteNum);
		}
		return siteStorage;
	}
	
	private double calcuOneSite(List<Integer> bikes) {
		
		int i_max=MathUtil.getListMax(bikes);
		double max=i_max;
		int divideRange=10;
		int[] bottle=new int[divideRange];
		int[] bottleScore=new int[divideRange];
		
		double step=max/divideRange;
		for(Integer i:bikes) {
			double botIndex=i/step;
			int index=(int) Math.round(botIndex);
			if (index==divideRange) {
				index-=1;
			}
			bottleScore[index]+=i;
			bottle[index]++;
		}
		
		int totalNum=bikes.size();
		double[] bottleRates=new double[divideRange];
		double[] bottleAvg=new double[divideRange];
		double[] score=new double[divideRange];
		double totalScore=0;
		for(int i=0;i<divideRange;i++) {
			bottleRates[i]=(int)((double)bottle[i]/totalNum*10000)/10000.0;
			bottleAvg[i]=(int)((double)bottleScore[i]/bottle[i]*100)/100.0;
			score[i]=bottleRates[i]*bottleAvg[i];
			totalScore+=score[i];
		}
//		System.out.println(Arrays.toString(bottleRates));
//		System.out.println(Arrays.toString(bottleAvg));
//		System.out.println(Arrays.toString(score));
//		System.out.println(totalScore);
//		System.out.println();
		return totalScore;

		
		
	}
	
}
