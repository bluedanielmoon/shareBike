package com.execute;

<<<<<<< HEAD
=======
import java.nio.file.Path;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

<<<<<<< HEAD
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poi.ConnectManager;
import com.pojo.AreaScore;
=======
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.AreaScore;
import com.pojo.Bike;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.Lnglat;
import com.pojo.MapSize;
import com.pojo.Poi;
import com.pojo.Point;
import com.pojo.Site;
import com.pojo.Varia;
import com.service.PoiServ;
import com.service.SiteServ;
import com.serviceImpl.PoiLocal;
<<<<<<< HEAD
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;
import com.util.PoiNameDecoder;
=======
import com.serviceImpl.PoiServImpl;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath.Step;
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;
import com.xju.App;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b

@Component
public class SiteChooser {
	
	@Autowired
	private SiteServ siteServ;

	private Integer getListMax(List<Integer> ls) {
		Integer max=0;
		for(Integer i:ls) {
			if(i>max) {
				max=i;
			}
		}
		return max;
	}
	
	/**
	 * 为了计算poi,把poi信息加入到地图信息中
	 * @param pois
	 * @param area
	 * @param maps
	 * @param dist
	 */
	private void putPoiCountToGrid(List<Poi> pois, BikeArea area, Map<String, Map<String, Object>> maps, int dist) {
		double lng = 0, lat = 0;
		lng = area.getStartLng();
		lat = area.getStartLat();
		String findID = "";
		for (Poi p : pois) {

			int lngDist = CoordsUtil.calcuDist(lng, lat, p.getLng(), lat);
			int latDist = CoordsUtil.calcuDist(lng, lat, lng, p.getLat());
			int lngPos = lngDist / dist;

			int latPos = latDist / dist;

			findID = latPos + "_" + lngPos;
			if (maps.containsKey(findID)) {
				
				Map<String, Object> small = maps.get(findID);
				if(small.containsKey("poi")) {
					int poiCount=(int) small.get("poi");
					small.put("poi", poiCount+1);
				}else {
					small.put("poi", 1);
				}
			}

		}

	}
	
	/**
	 * 进行分数基础的采集，并生成文件
	 */
	public void produceScoreFile(int dist) {
		MapHelper helper= new MapHelper();
		BikeArea area=State.getArea();
		MapSize size= new MapSize();
		Heater heater=new Heater();
		
		Calendar calendar=Calendar.getInstance();
		
		PoiServ poiServ=new PoiLocal();
		
		
		Map<String, Map<String, Object>> maps=helper.divideMapToGrid(area, dist, size);
		List<Poi> poiList=poiServ.getAllPois();
		
		putPoiCountToGrid(poiList, area, maps, dist);
		
		Date[] dates=FilesUtil.getFileRange();
		
		Date temp=dates[0];
		Date endDate=dates[1];
		Map<String, Varia> dayVaria=null;
		
		Map<String, AreaScore> scores=new TreeMap<>();
		List<Double> bkCounts = new ArrayList<>();
		while(temp.compareTo(endDate)<=0) {
			List<Map<String, Object>> dayBikes=FilesUtil.ListFilesInDay(temp);
			
			for (Map<String, Object> b : dayBikes) {
				BikeHeader header = (BikeHeader) b.get("header");
				bkCounts.add((double) header.getBikeCount());
			}
			if(dayVaria==null) {
				dayVaria=heater.addRecBikesCount(area, dist, maps, dayBikes);
			}else {
				heater.addBikeToVaria(dayVaria, area, dist, maps, dayBikes);
			}
			
			calendar.setTime(temp);
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+1);
			temp=calendar.getTime();
		}
		
		heater.calcuFlucByChance(dayVaria, bkCounts);
		

		for(String s:dayVaria.keySet()) {
			Varia varia=dayVaria.get(s);
			
			
			AreaScore score=new AreaScore();
			score.setFluc(varia.getFluc());
			List<Integer> nums=varia.getNumList();
			score.setMaxCount(getListMax(nums));
		
			Map<String, Object>info= maps.get(s);
			int poiCount=0;
			if(info.containsKey("poi")) {
				poiCount=(int) info.get("poi");
			}
			score.setPoiCount(poiCount);
			
			BikeArea smallarea=varia.getArea();
			score.setArea(smallarea);
			
			Lnglat gaode = CoordsUtil.getAreaCenter(smallarea);
			score.setCenter(gaode);
			scores.put(s, score);
			
		}
	
		MapperUtil.writeMapData("/Users/daniel/projects/score.txt", scores, AreaScore.class);


	}
	
	public class MaxScore{
		private double max;

		public double getMax() {
			return max;
		}

		public void setMax(double max) {
			this.max = max;
		}
		
	}
	
	/**
	 * 读取分数信息文件，并对其进行评分
	 * @return
	 */
	public Map<String, Map<String, Object>> judgeScore(MaxScore max,double flucSca,double parkSca,double poiSca) {
		Map<String, AreaScore> scores=MapperUtil.readMapData("/Users/daniel/projects/score.txt", AreaScore.class);
		
		if(scores.size()==0) {
			return null;
		}
		
		double minFluc=1.0;
		double maxFluc=scores.get("0_0").getFluc();
		int maxCount=0;
		int minCount=0;
		int poiMax=0;
		int poiMin=0;
		for(String s:scores.keySet()) {
			AreaScore score=scores.get(s);
			double fluc=score.getFluc();
			if((fluc-maxFluc)>0.00001) {
				maxFluc=fluc;
			}
			int count=score.getMaxCount();
			
			if(count>maxCount) {
				maxCount=count;
			}
			int poi=score.getPoiCount();
			if(poi>poiMax) {
				poiMax=poi;
			}

					
		}
		double lag_fluc=maxFluc-minFluc;

		double lag_count=maxCount-minCount;
		
		double lag_poi=poiMax-poiMin;
		
		Map<String, Map<String, Object>> totalScore=new HashMap<>();
		
		double temp=0;
		double maxScore=0;
		for(String s:scores.keySet()) {
			AreaScore score=scores.get(s);
			double fluc=score.getFluc();
			
			double s_fluc=(fluc-minFluc)/lag_fluc;

			int count=score.getMaxCount();
			
			double s_count=(count-minCount)/lag_count;
			
			int poi=score.getPoiCount();
			
			double s_poi=(poi-poiMin)/lag_poi;
			
		
			Map<String, Object> scoreResult=new HashMap<>();
			scoreResult.put("area", score.getArea());
			
			Lnglat cen=score.getCenter();
			scoreResult.put("center",cen);
			
			temp=s_fluc*flucSca+s_count*parkSca+s_poi*poiSca;
			if(temp>maxScore) {
				maxScore=temp;
			}
			scoreResult.put("score",temp);
			
			totalScore.put(s, scoreResult);
		}
		max.setMax(maxScore);

		return totalScore;
		
	}
	/**
	 * 对经过打分的点，根据输入的rate比例进行数量上的筛除，除掉低分点
	 * @param totalScore
	 * @param maxScore
	 * @param rate
	 * @return
	 */
	public Map<Double, Lnglat> chooseSite(Map<String, Map<String, Object>> totalScore,MaxScore maxScore,int rate,boolean turnBaidu) {
		
		TreeMap<Double, Lnglat> allSites=new TreeMap<>();
		List<Double> scores=new ArrayList<>();
		
		for(String s:totalScore.keySet()) {
			Map<String, Object> rec=totalScore.get(s);
			
			double score=(double) rec.get("score");
			Lnglat cen =(Lnglat) rec.get("center");
			if(turnBaidu) {
				allSites.put(score, CoordsUtil.turnBaiDuCoord(cen));
			}else {
				allSites.put(score, cen);
			}
			
			scores.add(score);
		}
		scores.sort(new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				return Double.compare(o1, o2);
			}
		});
		
		int count=(int) (((double)rate)/100.0*scores.size());
		
		Double valve=scores.get((scores.size()-count));
		SortedMap<Double, Lnglat> hightSites= allSites.subMap(valve, maxScore.getMax());
		return hightSites;
	}
	
	/**
	 * 对根据分数阈值选取的若干个点进行组合，形成分散分布的点
	 * @param sites
	 */
<<<<<<< HEAD
	public List<Point> mergeSites(Map<Double, Lnglat> sites,int compareDist) {
		int divideDist=50;
		
=======
	public List<Point> mergeSites(Map<Double, Lnglat> sites,int compareDist,int divideDist) {
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		MapHelper helper=new MapHelper();
		BikeArea area=State.getArea();
		List<BikePos> list= new ArrayList<>();
		int count=0;
		for(Double d:sites.keySet()) {
			Lnglat lnglat=sites.get(d);
			BikePos nPos=new BikePos(d+"", lnglat.getLng(), lnglat.getLat(), 1);
			list.add(nPos);
			count++;
		}
		List<Map<String, BikePos>> clusterResult=helper.neighborCluster(list, compareDist, divideDist, area);
		

		List<Point> centers=helper.calcuClusterCenter(clusterResult);
		
		for(Point p:centers) {
			double[] ll=p.getLnglat();
			double[] bdCoords=CoordsUtil.turnBaiDuCoord(ll[0],ll[1]);
			p.setLnglat(bdCoords);
			
		}
		return centers;
	}
	
<<<<<<< HEAD
	public List<Site> writeToDatabase(List<Point> sites) {
		
		PoiNameDecoder namer=new PoiNameDecoder();
		CloseableHttpClient client=ConnectManager.getClient();
		double[] lnglat=null;
		if(sites.size()==0) {
			return null;
=======
	public boolean writeToDatabase(List<Point> sites) {
		
		double[] lnglat=null;
		if(sites.size()==0) {
			return false;
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		}
		List<Site>  lSites=new ArrayList<>();
		for(Point p:sites) {
			lnglat=p.getLnglat();
			lnglat=CoordsUtil.turnGaodeCoord(lnglat[0], lnglat[1]);
<<<<<<< HEAD
			String siteName=namer.getPoiName(lnglat, client);
			if(siteName==null) {
				siteName="未知";
			}
			Site site=new Site(siteName, 20, 1, lnglat[0], lnglat[1]);
			
			lSites.add(site);
		}
		siteServ.clearTable();
		siteServ.patchaddSites(lSites);
		
		return lSites;
=======
			Site site=new Site("123", 20, 1, lnglat[0], lnglat[1]);
			lSites.add(site);
		}
		return siteServ.patchaddSites(lSites);
>>>>>>> 1111742a70d2946eb3ba3757488a034a11ddc91b
		
	}

	public static void main(String[] args) {
		

		
	}
}
