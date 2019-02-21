package com.execute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.init.State;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.MapSize;
import com.pojo.Point;
import com.pojo.Site;
import com.util.CoordsUtil;

/**
 * @author Administrator
 *
 */
@Component
public class MapHelper {
	private static int mapDivideDist = 550;
	// 100-500，200-600，300-700，400-800
	private static int minDist = 150;

	/**
	 * 设置分割地图的正方形大小（米），和聚类的距离
	 * 
	 * @param dist
	 */
	private static void setMapDivideDist(int dist,int divideDist) {
		minDist = dist;
		if(divideDist==0) {
			if (minDist <= 100) {
				mapDivideDist = 500;
			} else {
				mapDivideDist = 500 + (minDist - 100);
			}
		}else {
			mapDivideDist=divideDist;
		}

	}

	/**
	 * 对于划分成若干行和列的区域网格， 得到网格内的一段时间的单车分布
	 * 
	 * @param files
	 * @param divideAreas
	 */
	public Map<String, Map<String, Object>> getDurationBikesInMultiAreas(List<Map<String, Object>> files, BikeArea area,
			int rows, int cols) {

		Map<String, Map<String, Object>> divideAreas = divideMapToGrid(area, rows, cols);

		double startLng = area.getStartLng();
		double startlat = area.getStartLat();
		double endLng = area.getEndLng();
		double endLat = area.getEndLat();
		double colDist = Math.round((endLng - startLng) / cols * 1000000) / 1000000.0;
		double rowDist = Math.round((startlat - endLat) / rows * 1000000) / 1000000.0;

		int totalCount = 0, totalTemp = 0;
		for (Map<String, Object> file : files) {
			BikeHeader header = (BikeHeader) file.get("header");
			totalCount += header.getBikeCount();
			totalTemp += header.getWeather().getTempature();

			List<BikePos> bikes = (List<BikePos>) file.get("bikes");

			for (BikePos bike : bikes) {
				double lng = bike.getLng();
				double lat = bike.getLat();
				if (CoordsUtil.isInArea(area, lng, lat)) {

					int col = (int) Math.floor((lng - startLng) / colDist);
					int row = (int) Math.floor((startlat - lat) / rowDist);
					String id = row + "_" + col;

					if (divideAreas.containsKey(id)) {
						List<Point> ls = (List<Point>) divideAreas.get(id).get("bikes");
						Point point = new Point(new double[] { lng, lat }, bike.getBikeID(), 1);
						ls.add(point);

					}
				}
			}
		}

		Map<String, Object> header = new HashMap<>();
		header.put("bikesCount", totalCount);
		header.put("temperature", (totalTemp / files.size()));
		header.put("bikeRec", area);
		divideAreas.put("header", header);
		return divideAreas;
	}

	/**
	 * 获取某一时间范围内,某一区域内的单车分布数据 每个map中包含("header":BikeHeader,"bikes":List<BikePos>);
	 */
	public static List<Map<String, Object>> getDurationBikesInArea(List<Map<String, Object>> files, BikeArea area) {

		for (Map<String, Object> file : files) {
			BikeHeader header = (BikeHeader) file.get("header");
			List<BikePos> bikes = (List<BikePos>) file.get("bikes");

			// 过滤是否在区域内
			List<BikePos> inArea = new ArrayList<BikePos>();
			for (BikePos bike : bikes) {
				if (CoordsUtil.isInArea(area, bike.getLng(), bike.getLat())) {
					inArea.add(bike);
				}
			}
			header.setBikeRec(area);
			header.setBikeCount(inArea.size());
			file.put("bikes", inArea);
		}
		return files;

	}

	/**
	 * 将某一区域划分为多行多列的区域,边缘可能存在由于double计算带来的误差
	 * 
	 * @param area
	 * @param rows
	 * @param cols
	 */
	public static Map<String, Map<String, Object>> divideMapToGrid(BikeArea area, int rows, int cols) {
		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>();

		double startLng = area.getStartLng();
		double startlat = area.getStartLat();
		double endLng = area.getEndLng();
		double endLat = area.getEndLat();

		double colDist = Math.round((endLng - startLng) / cols * 1000000) / 1000000.0;
		double rowDist = Math.round((startlat - endLat) / rows * 1000000) / 1000000.0;

		if (rowDist == 0 || colDist == 0) {
			return maps;
		}
		int rowCount = 0, colCount = 0;

		double nowLng = startLng;
		double nowLat = startlat;
		do {
			do {
				double cornerLng = Math.round((nowLng + colDist) * 1000000) / 1000000.0;
				double cornerLat = Math.round((nowLat - rowDist) * 1000000) / 1000000.0;

				BikeArea small = new BikeArea(nowLng, nowLat, cornerLng, cornerLat);

				String areaID = rowCount + "_" + colCount;
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("area", small);
				mp.put("bikes", new ArrayList<Point>());
				maps.put(areaID, mp);

				nowLng = cornerLng;
				colCount++;
			} while (colCount < cols);

			nowLat = Math.round((nowLat - rowDist) * 1000000) / 1000000.0;
			nowLng = startLng;
			colCount = 0;
			rowCount++;
		} while (rowCount < rows);
		return maps;
	}

	/**
	 * 将聚类之后产生的点的集合，以平均值计算每个集合的中心点
	 * 
	 * @param packs
	 * @return
	 */
	public List<Point> calcuClusterCenter(List<Map<String, BikePos>> packs) {

		List<Point> result = new ArrayList<Point>();
		int idCount = 0;
		for (Map<String, BikePos> pack : packs) {
			Set<String> keys = pack.keySet();

			List<BikePos> ls = new ArrayList<BikePos>();
			for (String key : keys) {
				BikePos bike = pack.get(key);
				
				ls.add(bike);

			}
			double center[] = CoordsUtil.calcuCenter(ls);
			Point pt = new Point(new double[] { center[0], center[1] }, "" + idCount++, 1);
			result.add(pt);

		}
		return result;

	}
	
	public List<Point> calcuClusterCenter2(List<Map<String, BikePos>> packs) {

		List<Point> result = new ArrayList<Point>();
		int idCount = 0;
		for (Map<String, BikePos> pack : packs) {
			Set<String> keys = pack.keySet();

			List<BikePos> ls = new ArrayList<BikePos>();
			for (String key : keys) {
				BikePos bike = pack.get(key);
				ls.add(bike);

			}
			double center[] = CoordsUtil.calcuCenterWithPower(ls);
			Point pt = new Point(new double[] { center[0], center[1] }, "" + idCount++, 1);
			result.add(pt);

		}
		return result;

	}

	/**
	 * 根据输入的单车的数据和聚类的距离，进行近邻聚类，返回聚类的点的集合
	 * 
	 * @param bikes
	 * @param distance
	 * @return
	 */
	public List<Map<String, BikePos>> neighborCluster(List<BikePos> bikes, int distance,int divideDist,BikeArea... area) {

		Map<String, BikePos> pool = new HashMap<String, BikePos>();
		BikePos bp = null;
		for (int i = 0; i < bikes.size(); i++) {
			bp = bikes.get(i);
			pool.put(bp.getBikeID(), bp);
		}

		// 设置搜索距离
		setMapDivideDist(distance,divideDist);

		// 把收集区域的边界朝四个方向分别扩大若干米
		BikeArea bigArea=null;
		if(area.length>0) {
			bigArea=area[0];
		}else {
			int bigSize = 400;
			bigArea = getBiggerArea(State.AREA, bigSize);
		}
		

		MapSize size = new MapSize();

		// 把区域按照mapDivideDist分割为网状
		Map<String, Map<String, Object>> maps = divideMapToGrid(bigArea, mapDivideDist, size);
		
		// 判断每个单车属于哪一个网格，并添加进去
		pubBikesToGrid(bikes, bigArea, maps, mapDivideDist);

		// 开始聚类s
		List<Map<String, BikePos>> result = new ArrayList<Map<String, BikePos>>();
		while (pool.size() > 0) {

			Iterator<String> iter = pool.keySet().iterator();
			BikePos source = pool.get(iter.next());
			Map<String, BikePos> pack = new HashMap<String, BikePos>();
			pack.put(source.getBikeID(), source);
			List<BikePos> nextLook = new ArrayList<BikePos>();

			nextLook.add(source);

			while (nextLook.size() != 0) {
				List<BikePos> nearBks = new ArrayList<BikePos>();
				for (BikePos bk : nextLook) {
					nearBks.addAll(getNearBikes(bk, maps, size));
				}
				nextLook.clear();
				for (BikePos bk : nearBks) {
					if (!pack.containsKey(bk.getBikeID())) {
						pack.put(bk.getBikeID(), bk);
						nextLook.add(bk);
					}
				}

			}

			result.add(pack);

			for (String s : pack.keySet()) {
				pool.remove(s);
			}
			// System.out.println("第 "+packCount+" 个pack");
			// System.out.println("pack size:"+pack.size());
			// System.out.println("剩余pool size:"+pool.size()+"\n");
		}
		return result;
	}

	/**
	 * 在地图网格中找到离该单车距离以内的单车
	 * 
	 * @param bike
	 * @param maps
	 * @param size
	 * @return
	 */
	public List<BikePos> getNearBikes(BikePos bike, Map<String, Map<String, Object>> maps, MapSize size) {
		BikeArea area = State.AREA;
		double lng = area.getStartLng();
		double lat = area.getStartLat();
		String findID = "";
		int lngDist = CoordsUtil.calcuDist(lng, lat, bike.getLng(), lat);
		int latDist = CoordsUtil.calcuDist(lng, lat, lng, bike.getLat());
		int lngPos = lngDist / mapDivideDist;

		int latPos = latDist / mapDivideDist;
		findID = latPos + "_" + lngPos;
		int border=(minDist/mapDivideDist)+1;
		List<String> toFindIDs = getAroundAreas(findID, size.getRow(), size.getCol(), border);
		List<BikePos> result = new ArrayList<BikePos>();
		for (String s : toFindIDs) {

			Map<String, Object> around = maps.get(s);
			List<BikePos> list = (List<BikePos>) around.get("bikes");
			int calcuDist = 0;
			for (BikePos pos : list) {
				calcuDist = CoordsUtil.calcuDist(pos.getLng(), pos.getLat(), bike.getLng(), bike.getLat());
				if (calcuDist <= minDist) {
					result.add(pos);
				}
			}
		}
		return result;

	}

	public Map<String, List<BikePos>> getBikesWithinDist(List<BikePos> bikes, Map<String, Map<String, Object>> maps,
			MapSize size) {

		Map<String, List<BikePos>> resultList = new HashMap<String, List<BikePos>>();
		for (BikePos b : bikes) {
			List<BikePos> result = getNearBikes(b, maps, size);
			resultList.put(b.getBikeID(), result);
		}
		return resultList;
	}

	/**
	 * 采集数据时，边界上的有些单车数据会超过划定的范围
	 * 
	 * @param area
	 * @return
	 */
	public BikeArea getBiggerArea(BikeArea area, int borderDist) {
		double startLng = area.getStartLng();
		double startLat = area.getStartLat();
		double endLng = area.getEndLng();
		double endLat = area.getEndLat();
		double bigStartLng = CoordsUtil.getLng(startLat, startLng, borderDist, false);
		double bigStartLat = CoordsUtil.getLat(startLat, borderDist, false);
		double bigEndLng = CoordsUtil.getLng(endLat, endLng, borderDist, true);
		double bigEndLat = CoordsUtil.getLat(endLat, borderDist, true);

		return new BikeArea(bigStartLng, bigStartLat, bigEndLng, bigEndLat);
	}

	/**
	 * 给定rows，cols,给个ID,取地图网格中对应这个id的区域的周围的区域
	 * 
	 * @param areaID
	 * @param rows
	 * @param cols
	 * @return
	 */
	public List<String> getAroundAreas(String areaID, int rows, int cols, int border) {
		String strPos[] = areaID.split("_");
		int row = Integer.parseInt(strPos[0]);
		int col = Integer.parseInt(strPos[1]);
		List<String> around = new ArrayList<String>();
		for (int i = row - border, j = col - border;;) {
			if (i <= row + border) {
				if (j <= col + border) {
					if (i >= 0 && j >= 0 && i < rows && j < cols) {
						around.add(i + "_" + j);
					}
					j++;
				} else {
					j = col - border;
					i++;
				}
			} else {
				break;
			}
		}
		return around;

	}
	
	public List<String> getSiteAroundAreas(BikeArea area,Site site,MapSize size,int divideDist) {
		double lng = area.getStartLng();
		double lat = area.getStartLat();
		String findID = "";
		int lngDist = CoordsUtil.calcuDist(lng, lat, site.getLng(), lat);
		int latDist = CoordsUtil.calcuDist(lng, lat, lng, site.getLat());
		int lngPos = lngDist / divideDist;

		int latPos = latDist / divideDist;
		findID = latPos + "_" + lngPos;
		int border=1;
		
		String strPos[] = findID.split("_");
		int row = Integer.parseInt(strPos[0]);
		int col = Integer.parseInt(strPos[1]);
		List<String> around = new ArrayList<String>();
		for (int i = row - border, j = col - border;;) {
			if (i <= row + border) {
				if (j <= col + border) {
					if (i >= 0 && j >= 0 && i < size.getRow() && j < size.getCol()) {
						around.add(i + "_" + j);
					}
					j++;
				} else {
					j = col - border;
					i++;
				}
			} else {
				break;
			}
		}
		return around;

	}

	/**
	 * 把单车的数据添加到map里面，map以区域ID获取区域，每个区域可以得到area边界，和内部的bikes列表
	 * 
	 * @param bikes
	 * @param area
	 * @param maps
	 * @param dist
	 */
	public void pubBikesToGrid(List<BikePos> bikes, BikeArea area, Map<String, Map<String, Object>> maps, int dist) {
		double lng = 0, lat = 0;
		lng = area.getStartLng();
		lat = area.getStartLat();
		String findID = "";
		for (BikePos b : bikes) {

			int lngDist = CoordsUtil.calcuDist(lng, lat, b.getLng(), lat);
			int latDist = CoordsUtil.calcuDist(lng, lat, lng, b.getLat());
			int lngPos = lngDist / dist;

			int latPos = latDist / dist;

			findID = latPos + "_" + lngPos;
			if (maps.containsKey(findID)) {
				Map<String, Object> small = maps.get(findID);
				List<BikePos> list = (List<BikePos>) small.get("bikes");

				list.add(b);
			}

		}

	}

	/**
	 * 把区域的边界扩大一点，然后将大区域分割为正方形的小区域，
	 * 
	 * @param area
	 * @param dist
	 * @param size
	 * @return 外围string是每个区域的,内围string有area,bikes，存储区域坐标值和单车数据
	 */
	public Map<String, Map<String, Object>> divideMapToGrid(BikeArea area, int dist, MapSize size) {
		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>();

		int rows = 0, cols = 0;
		double nowLng = area.getStartLng();
		double nowLat = area.getStartLat();
		do {
			do {
				double cornerLng = CoordsUtil.getLng(nowLat, nowLng, dist, true);
				double cornerLat = CoordsUtil.getLat(nowLat, dist, true);
				BikeArea small = new BikeArea(nowLng, nowLat, cornerLng, cornerLat);

				String areaID = rows + "_" + cols;
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("area", small);
				mp.put("bikes", new ArrayList<BikePos>());
				maps.put(areaID, mp);

				nowLng = cornerLng;
				cols++;
			} while (nowLng <= area.getEndLng());
			nowLat = CoordsUtil.getLat(nowLat, dist, true);
			nowLng = area.getStartLng();
			cols = 0;
			rows++;
		} while (nowLat >= area.getEndLat());
		size.setRow(rows);
		size.setCol(maps.size() / rows);
		return maps;
	}

}
