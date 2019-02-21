package com.execute;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.init.State;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.Lnglat;
import com.pojo.MapSize;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;

@Component
public class FlowChecker {

	@Autowired
	SiteServ siteServ;

	@Autowired
	MapHelper helper;

	/**
	 * 通过历史上的单车信息判断某个站点流出，流入的站点信息
	 */
	public TreeMap<Integer, Integer> analyzeSiteFlowByAllHistory(int targetSiteID, int flowType) {
		int total = 0;
		TreeMap<Integer, Integer> sitesCount = new TreeMap<>();
		Date[] dates = FilesUtil.getFileRange();

		Date startDate = dates[0];
		Date endDate = dates[1];
		Date today = startDate;
		Calendar calendar = Calendar.getInstance();
		while (today.compareTo(endDate) <= 0) {
			calendar.setTime(today);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			Date st_time = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			Date end_time = calendar.getTime();
			Map<Integer, Map<Integer, List<BikePos>>> siteBikes = putBikesToSite(st_time, end_time);
			Map<Integer, Map<String, Integer>> findSites = analyzeSiteFlow(targetSiteID, flowType, siteBikes);

			for (Integer time : findSites.keySet()) {

				Map<String, Integer> siteIDs = findSites.get(time);
				total += siteIDs.size();
				for (String k : siteIDs.keySet()) {
					int siteID = siteIDs.get(k);
					if (sitesCount.containsKey(siteID)) {
						sitesCount.put(siteID, sitesCount.get(siteID) + 1);
					} else {
						sitesCount.put(siteID, 1);
					}
					Site site = siteServ.getSiteById(siteID);
				}
			}
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
			today = calendar.getTime();
		}
		return sitesCount;
	}

	/**
	 * 把单车的数据归给站点
	 * 
	 * @param end_time2
	 * @param st_time2
	 * 
	 * @param area
	 * @param dist
	 * @param map
	 * @param bikes
	 * @return Map<String, Varia> 键值为地图方块ID，代表横纵信息，Varia代表里面的信息
	 */
	public Map<Integer, Map<Integer, List<BikePos>>> putBikesToSite(Date st_time, Date end_time) {
		BikeArea area = State.AREA;
		int mapDivideDist = 50;
		MapSize mapSize = new MapSize();
		Map<String, Map<String, Object>> map = helper.divideMapToGrid(area, mapDivideDist, mapSize);
		List<Path> files = FilesUtil.listFilesInDuration(st_time, end_time, true);

		FilesUtil.sortFiles(files);
		Map<Integer, Map<Integer, List<BikePos>>> result = new HashMap<>();
		List<Site> sites = siteServ.getAllSites();
		for (Site site : sites) {
			Map<Integer, List<BikePos>> hourBikes = new HashMap<>();
			result.put(site.getId(), hourBikes);

		}
		int hour = 0;
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < files.size(); i++) {
			Map<String, Object> bikeInfo = FilesUtil.readFileInfo(files.get(i).toString());
			BikeHeader header = (BikeHeader) bikeInfo.get("header");
			calendar.setTime(header.getStartTime());
			hour = calendar.get(Calendar.HOUR_OF_DAY);

			List<BikePos> ls = (List<BikePos>) bikeInfo.get("bikes");
			helper.pubBikesToGrid(ls, area, map, mapDivideDist);
			for (Site site : sites) {
				List<BikePos> hourSitebks = new ArrayList<>();
				Map<Integer, List<BikePos>> siteBikes = result.get(site.getId());
				siteBikes.put(hour, hourSitebks);

				BikeArea siteArea = CoordsUtil.getCenterArea(new Lnglat(site.getLng(), site.getLat()));
				List<String> toFindIDs = helper.getSiteAroundAreas(area, site, mapSize, mapDivideDist);
				for (String s : toFindIDs) {
					List<BikePos> list = (List<BikePos>) map.get(s).get("bikes");
					for (BikePos pos : list) {
						if (CoordsUtil.isInArea(siteArea, pos.getLng(), pos.getLat())) {
							List<BikePos> bikesInSite = siteBikes.get(hour);
							bikesInSite.add(pos);
						}
					}
				}
			}

			// 清空每个文件放进去的数据
			for (String rec : map.keySet()) {
				Map<String, Object> small = map.get(rec);
				List<BikePos> list = (List<BikePos>) small.get("bikes");
				list.clear();
			}
		}
		return result;
	}

	/**
	 * 分析某个站点的单车流动，
	 */
	public Map<Integer, Map<String, Integer>> analyzeSiteFlow(int siteID, int flowType,
			Map<Integer, Map<Integer, List<BikePos>>> allSiteData) {
		Map<Integer, List<BikePos>> targetSiteData = allSiteData.get(siteID);

		Map<Integer, List<String>> movedOut = new HashMap<>();
		// 时间，找到的单车ID,找到的站点ID，
		Map<Integer, Map<String, Integer>> findSites = new HashMap<>();
		boolean flowOut = false;
		if (flowType == State.FLOW_OUT) {
			flowOut = true;
		}
		// 只关注一次流动，因为当二次移动到别的站，就不属于本站的流出了
		for (int i = 0; i < targetSiteData.size(); i++) {
			List<BikePos> hourList = targetSiteData.get(i);
			List<BikePos> nextHourList = targetSiteData.get(i + 1);
			List<String> flowList = null;
			if (flowOut) {
				flowList = getItemInOneNotTwo(hourList, nextHourList);
			} else {
				flowList = getItemInOneNotTwo(nextHourList, hourList);
			}
			// 存在流出的单车
			if (flowList.size() > 0) {
				if (flowOut) {
					// System.out.println("流出单车"+i+"-----"+flowList.size());
				} else {
					// System.out.println("流入单车"+i+"-----"+flowList.size());
				}

				movedOut.put(i, flowList);
			}
		}
		boolean found = false;

		for (Integer time : movedOut.keySet()) {
			List<String> finds = movedOut.get(time);
			for (String find : finds) {
				found = false;
				// 对每个要找的单车ID开始在所有站点里寻找
				for (Integer otherID : allSiteData.keySet()) {
					if (siteID == otherID) {
						continue;
					}
					Map<Integer, List<BikePos>> otherSiteData = allSiteData.get(otherID);
					for (Integer otherTime : otherSiteData.keySet()) {

						// 只寻找两个小时以内的，因为超出可能就不是第一次流转了
						if ((otherTime - time) <= 2 && (otherTime - time) >= 0) {

							List<BikePos> otherList = otherSiteData.get(otherTime);
							for (BikePos bk : otherList) {
								if (bk.getBikeID().equals(find)) {
									found = true;
									if (findSites.containsKey(otherTime)) {
										Map<String, Integer> findSiteIDs = findSites.get(otherTime);
										findSiteIDs.put(find, otherID);
									} else {
										Map<String, Integer> findSiteIDs = new HashMap<>();
										findSiteIDs.put(find, otherID);
										findSites.put(otherTime, findSiteIDs);
									}
									break;
								}
							}
						}
						if (found) {
							break;
						}
					}
					if (found) {
						break;
					}
				}
			}

		}

		return findSites;
	}
	/**
	 * 得到在第一个序列里而不在第二个序列里，第一个序列中的项
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static List<String> getItemInOneNotTwo(List<BikePos> l1, List<BikePos> l2) {

		int jCount = 0;
		List<String> result = new ArrayList<>();
		if (l1 == null || l2 == null || l1.isEmpty()) {
			return result;
		}
		if (l2.isEmpty()) {
			for (BikePos bike : l1) {
				result.add(bike.getBikeID());
			}
			return result;
		}
		l1.sort(new Comparator<BikePos>() {

			@Override
			public int compare(BikePos b1, BikePos b2) {
				// TODO Auto-generated method stub
				return b1.getBikeID().compareTo(b2.getBikeID());
			}
		});
		l2.sort(new Comparator<BikePos>() {

			@Override
			public int compare(BikePos b1, BikePos b2) {
				// TODO Auto-generated method stub
				return b1.getBikeID().compareTo(b2.getBikeID());
			}
		});
		for (int i = 0; i < l1.size(); i++) {
			BikePos item = l1.get(i);
			for (int j = jCount; j < l2.size(); j++) {
				if (l2.get(j).getBikeID().compareTo(item.getBikeID()) > 0) {
					result.add(item.getBikeID());
					jCount = j;
					break;
				} else if (l2.get(j).getBikeID().compareTo(item.getBikeID()) == 0) {
					j++;
					jCount = j;
					break;
				}
			}
			if (jCount == l2.size()) {
				for (int k = i + 1; k < l1.size(); k++) {
					result.add(l1.get(k).getBikeID());
				}
				break;
			}
		}
		return result;
	}

	/**
	 * 倒序排列，出现次数最多是第一个
	 * 
	 * @param siteCounts
	 * @return
	 */
	public List<Site> sortAnalyzeResult(TreeMap<Integer, Integer> siteCounts) {
		// 这里借用Site来临时存储具有ID和出现次数的一个list,为了对其进行排序
		List<Site> sortCounts = new ArrayList<>();
		for (Integer siteID : siteCounts.keySet()) {
			Site site = new Site();
			site.setId(siteID);
			site.setVolume(siteCounts.get(siteID));
			sortCounts.add(site);
		}

		sortCounts.sort(new Comparator<Site>() {
			public int compare(Site o1, Site o2) {
				return Integer.compare(o2.getVolume(), o1.getVolume());
			}
		});
		return sortCounts;
	}

	public void calcuSortWrite(int targetID,int flowType) {
		// 左侧siteID,右侧出现次数
		TreeMap<Integer, Integer> siteCounts = analyzeSiteFlowByAllHistory(targetID, flowType);

		List<Site> sortCounts = sortAnalyzeResult(siteCounts);
		if(flowType==State.FLOW_OUT) {
			MapperUtil.writeListData("/Users/daniel/projects/bikeFlow/flowOut_" + targetID + ".txt", sortCounts, Site.class);
		}else {
			MapperUtil.writeListData("/Users/daniel/projects/bikeFlow/flowIn_" + targetID + ".txt", sortCounts, Site.class);
		}

		
	}
	
	private List<Site> readFlow(int targetID,int flowType) {
		String fileName=null;
		if(flowType==State.FLOW_OUT) {
			fileName="/Users/daniel/projects/bikeFlow/flowOut_" + targetID + ".txt";
		}else {
			fileName="/Users/daniel/projects/bikeFlow/flowIn_" + targetID + ".txt";
		}
		Path path=Paths.get(fileName);
		if(Files.exists(path)) {
			return MapperUtil.readListData(path.toString(), Site.class);
		}else {
			return null;
		}
		
	}
	/**
	 * 根据站点流动的分析文件选出流动最多的若干站点作为展示
	 * @param targetID
	 * @param flowType
	 */
	public List<Integer> getSiteFlow(int targetID,int flowType) {
		List<Site> filesites=readFlow(targetID,flowType);
		List<Integer> topSites=new ArrayList<>();
		if(filesites==null) {
			return topSites;
		}
		int totalCount=0;
		for(Site site:filesites){
			totalCount+=site.getVolume();
		}
		double ratio=0;
		
		for(Site site:filesites) {
			ratio=(double)site.getVolume()/totalCount;
			if(ratio-State.FLOW_RATIO>0.000001) {
				topSites.add(site.getId());
			}
		}
		return topSites;
	}
	
	public Map<Integer, List<Integer>> getAllSitesFlow(int flowType){
		Map<Integer, List<Integer>> allFlows=new TreeMap<>();
		List<Site> sites=siteServ.getAllSites();
		int tempId=0;
		for(Site site:sites) {
			tempId=site.getId();
			List<Integer> tops=getSiteFlow(tempId, flowType);
			if(tops!=null&&tops.size()>0) {
				allFlows.put(tempId, tops);
			}
			
		}
		return allFlows;
	}
	/**
	 * 对所有站点进行分析，生产迁移文件
	 */
	public void produceAllSiteFlows(int flag) {
		List<Site> sites=siteServ.getAllSites();
		for(Site site:sites) {
			calcuSortWrite(site.getId(), flag);
			
		}
	}

}
