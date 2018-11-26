//package com.execute;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.springframework.stereotype.Component;
//
//import com.pojo.BikeArea;
//import com.pojo.BikeHeader;
//import com.pojo.Lnglat;
//import com.pojo.MapSize;
//import com.pojo.Point;
//import com.util.CoordsUtil;
//
///**
// * @author Administrator
// *
// */
//@Component
//public class CoordsHelper {
//
//	/**
//	 * 将某一区域划分为多行多列的区域,边缘可能存在由于double计算带来的误差
//	 * 
//	 * @param area
//	 * @param rows
//	 * @param cols
//	 */
//	public static Map<String, Map<String, Object>> divideMapToGrid(BikeArea area, int rows, int cols) {
//		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>();
//
//		double startLng = area.getStartLng();
//		double startlat = area.getStartLat();
//		double endLng = area.getEndLng();
//		double endLat = area.getEndLat();
//
//		double colDist = Math.round((endLng - startLng) / cols * 1000000) / 1000000.0;
//		double rowDist = Math.round((startlat - endLat) / rows * 1000000) / 1000000.0;
//
//		if (rowDist == 0 || colDist == 0) {
//			return maps;
//		}
//		int rowCount = 0, colCount = 0;
//
//		double nowLng = startLng;
//		double nowLat = startlat;
//		do {
//			do {
//				double cornerLng = Math.round((nowLng + colDist) * 1000000) / 1000000.0;
//				double cornerLat = Math.round((nowLat - rowDist) * 1000000) / 1000000.0;
//
//				BikeArea small = new BikeArea(nowLng, nowLat, cornerLng, cornerLat);
//
//				String areaID = rowCount + "_" + colCount;
//				Map<String, Object> mp = new HashMap<String, Object>();
//				mp.put("area", small);
//				mp.put("bikes", new ArrayList<Point>());
//				maps.put(areaID, mp);
//
//				nowLng = cornerLng;
//				colCount++;
//			} while (colCount < cols);
//
//			nowLat = Math.round((nowLat - rowDist) * 1000000) / 1000000.0;
//			nowLng = startLng;
//			colCount = 0;
//			rowCount++;
//		} while (rowCount < rows);
//		return maps;
//	}
//
//	/**
//	 * 将聚类之后产生的点的集合，以平均值计算每个集合的中心点
//	 * 
//	 * @param packs
//	 * @return
//	 */
//	public List<Point> calcuClusterCenter(List<Map<String, Lnglat>> packs) {
//
//		List<Point> result = new ArrayList<Point>();
//		int idCount = 0;
//		for (Map<String, Lnglat> pack : packs) {
//			Set<String> keys = pack.keySet();
//
//			List<Lnglat> ls = new ArrayList<Lnglat>();
//			for (String key : keys) {
//				Lnglat bike = pack.get(key);
//				ls.add(bike);
//
//			}
//			double center[] = CoordsUtil.calcuLngLatCenter(ls);
//			Point pt = new Point(new double[] { center[0], center[1] }, "" + idCount++, 1);
//			result.add(pt);
//
//		}
//		return result;
//
//	}
//
//	/**
//	 * 根据输入的单车的数据和聚类的距离，进行近邻聚类，返回聚类的点的集合
//	 * 
//	 * @param bikes
//	 * @param distance
//	 * @return
//	 */
//	public List<Map<String, Lnglat>> neighborCluster(List<Lnglat> points,BikeArea area,int divideDist, int distance) {
//
//		Map<Integer, Lnglat> pool = new HashMap<Integer, Lnglat>();
//		Lnglat bp = null;
//		for (int i = 0; i < points.size(); i++) {
//			bp = points.get(i);
//			pool.put(i, bp);
//		}
//
//		MapSize size = new MapSize();
//		
//		
//
//		// 把区域按照mapDivideDist分割为网状
//		Map<String, Map<String, Object>> maps = divideMapToGrid(area, divideDist, size);
//
//		pubLngLatToGrid(points, area, maps, divideDist);
//
//		System.out.println(pool.size() + "     开始聚类");
//		// 开始聚类s
//		List<Map<String, Lnglat>> result = new ArrayList<Map<String, Lnglat>>();
//		while (pool.size() > 0) {
//
//			Iterator<Integer> iter = pool.keySet().iterator();
//			Lnglat source = pool.get(iter.next());
//			Map<String, Lnglat> pack = new HashMap<String, Lnglat>();
//			pack.put(source.getBikeID(), source);
//			List<Lnglat> nextLook = new ArrayList<Lnglat>();
//
//			nextLook.add(source);
//
//			while (nextLook.size() != 0) {
//				List<Lnglat> nearBks = new ArrayList<Lnglat>();
//				for (Lnglat bk : nextLook) {
//					Lnglat lnglat=new Lnglat(bk.getLng(), bk.getLat());
//					nearBks.addAll(getNearLngLats(lnglat, maps, size, divideDist, distance));
//				}
//				nextLook.clear();
//				for (Lnglat bk : nearBks) {
//					if (!pack.containsKey(bk.getBikeID())) {
//						pack.put(bk.getBikeID(), bk);
//						nextLook.add(bk);
//					}
//				}
//
//			}
//
//			result.add(pack);
//
//			for (String s : pack.keySet()) {
//				pool.remove(s);
//			}
//			// System.out.println("第 "+packCount+" 个pack");
//			// System.out.println("pack size:"+pack.size());
//			// System.out.println("剩余pool size:"+pool.size()+"\n");
//		}
//		System.out.println("聚类结果：" + result.size());
//		return result;
//	}
//
//	/**
//	 * 在地图网格中找到离该坐标距离以内的坐标集合
//	 * 
//	 * @param bike
//	 * @param maps
//	 * @param size
//	 * @return
//	 */
//	public List<Lnglat> getNearLngLats(Lnglat lnglat, Map<String, Map<String, Object>> maps, MapSize size,int divideDist,int compareDist) {
//		BikeArea area = State.getArea();
//		double lng = area.getStartLng();
//		double lat = area.getStartLat();
//		String findID = "";
//		int lngDist = CoordsUtil.calcuDist(lng, lat, lnglat.getLng(), lat);
//		int latDist = CoordsUtil.calcuDist(lng, lat, lng, lnglat.getLat());
//		int lngPos = lngDist / divideDist;
//
//		int latPos = latDist / divideDist;
//		findID = latPos + "_" + lngPos;
//		List<String> toFindIDs = getAroundAreas(findID, size.getRow(), size.getCol());
//		List<Lnglat> result = new ArrayList<Lnglat>();
//		for (String s : toFindIDs) {
//
//			Map<String, Object> around = maps.get(s);
//			List<Lnglat> list = (List<Lnglat>) around.get("coords");
//			int calcuDist = 0;
//			for (Lnglat pos : list) {
//				calcuDist = CoordsUtil.calcuDist(pos.getLng(), pos.getLat(), lnglat.getLng(), lnglat.getLat());
//				if (0<calcuDist&&calcuDist <= compareDist) {
//					result.add(pos);
//				}
//			}
//		}
//		return result;
//
//	}
//
//	public Map<String, List<Lnglat>> getBikesWithinDist(List<Lnglat> points, Map<String, Map<String, Object>> maps,
//			MapSize size,int divideDist,int compareDist) {
//
//		Map<String, List<Lnglat>> resultList = new HashMap<String, List<Lnglat>>();
//		for (Lnglat b : points) {
//			Lnglat lnglat=new Lnglat(b.getLng(), b.getLat());
//			List<Lnglat> result = getNearLngLats(lnglat, maps, size,divideDist,compareDist);
//			resultList.put(b.getBikeID(), result);
//		}
//		return resultList;
//	}
//
//	/**
//	 * 给定rows，cols,给个ID,取地图网格中对应这个id的区域的周围的区域
//	 * 
//	 * @param areaID
//	 * @param rows
//	 * @param cols
//	 * @return
//	 */
//	public List<String> getAroundAreas(String areaID, int rows, int cols) {
//		String strPos[] = areaID.split("_");
//		int row = Integer.parseInt(strPos[0]);
//		int col = Integer.parseInt(strPos[1]);
//		List<String> around = new ArrayList<String>();
//		for (int i = row - 1, j = col - 1;;) {
//			if (i <= row + 1) {
//				if (j <= col + 1) {
//					if (i >= 0 && j >= 0 && i < rows && j < cols) {
//						around.add(i + "_" + j);
//					}
//					j++;
//				} else {
//					j = col - 1;
//					i++;
//				}
//			} else {
//				break;
//			}
//		}
//		return around;
//
//	}
//
//	/**
//	 * 把单车的数据添加到map里面，map以区域ID获取区域，每个区域可以得到area边界，和内部的bikes列表
//	 * 
//	 * @param bikes
//	 * @param area
//	 * @param maps
//	 * @param dist
//	 */
//	public void pubLngLatToGrid(List<Lnglat> points, BikeArea area, Map<String, Map<String, Object>> maps, int dist) {
//		double lng = 0, lat = 0;
//		lng = area.getStartLng();
//		lat = area.getStartLat();
//		String findID = "";
//		for (Lnglat p : points) {
//
//			int lngDist = CoordsUtil.calcuDist(lng, lat, p.getLng(), lat);
//			int latDist = CoordsUtil.calcuDist(lng, lat, lng, p.getLat());
//			int lngPos = lngDist / dist;
//
//			int latPos = latDist / dist;
//
//			findID = latPos + "_" + lngPos;
//			if (maps.containsKey(findID)) {
//				Map<String, Object> small = maps.get(findID);
//				List<Lnglat> list = (List<Lnglat>) small.get("coords");
//
//				list.add(p);
//			}
//
//		}
//
//	}
//
//	/**
//	 * 将大区域分割为正方形的小区域，
//	 * 
//	 * @param area
//	 * @param dist
//	 * @param size
//	 * @return 外围string是每个区域的,内围string有area,coords，存储区域坐标值和单车数据
//	 */
//	public Map<String, Map<String, Object>> divideMapToGrid(BikeArea area, int dist, MapSize size) {
//		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>();
//
//		int rows = 0, cols = 0;
//		double nowLng = area.getStartLng();
//		double nowLat = area.getStartLat();
//		do {
//			do {
//				double cornerLng = CoordsUtil.getLng(nowLat, nowLng, dist, true);
//				double cornerLat = CoordsUtil.getLat(nowLat, dist, true);
//				BikeArea small = new BikeArea(nowLng, nowLat, cornerLng, cornerLat);
//
//				String areaID = rows + "_" + cols;
//				Map<String, Object> mp = new HashMap<String, Object>();
//				mp.put("area", small);
//				mp.put("coords", new ArrayList<Lnglat>());
//				maps.put(areaID, mp);
//
//				nowLng = cornerLng;
//				cols++;
//			} while (nowLng <= area.getEndLng());
//			nowLat = CoordsUtil.getLat(nowLat, dist, true);
//			nowLng = area.getStartLng();
//			cols = 0;
//			rows++;
//		} while (nowLat >= area.getEndLat());
//		size.setRow(rows);
//		size.setCol(maps.size() / rows);
//		return maps;
//	}
//
//}
