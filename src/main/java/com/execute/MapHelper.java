package com.execute;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.pojo.Bike;
import com.pojo.BikeArea;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.ClusterPt;
import com.pojo.Point;
import com.util.CoordsUtil;
import com.util.DateUtil;
import com.util.FilesUtil;

/**
 * @author Administrator
 *
 */
@Component
public class MapHelper {
	private static int mapDivideDist = 550;
	private static int boder = 1;
	// 100-500，200-600，300-700，400-800
	private static int minDist = 150;

	/**
	 * 设置分割地图的正方形大小（米），和聚类的距离
	 * 
	 * @param dist
	 */
	private static void setMapDivideDist(int dist) {
		minDist = dist;
		if (minDist <= 100) {
			mapDivideDist = 500;
		} else {
			mapDivideDist = 500 + (minDist - 100);
		}

	}

	public static void main(String[] args) {
		 String start = "2018_11_1 3";
		 String end = "2018_11_1 3";
		 Date st = DateUtil.pareFileTime(start);
		 Date en = DateUtil.pareFileTime(end);
		 List<Map<String, Object>> maps = FilesUtil.readFilesToBikeMap(st,
		 en);
		BikeArea area = State.getArea();
		
		area.setStartLat(34.286231);
		area.setStartLng(108.903407);
		area.setEndLng(108.906916);
		area.setEndLat(34.284764);
		// getDurationBikesInArea(maps, area);

//		Map<String, Map<String, Object>> result=getDurationBikesInMultiAreas(maps, area, 1, 2);
//		int count=0;
//		for(String s:result.keySet()){
//			List<BikePos> ls=(List<BikePos>) result.get(s).get("bikes");
//			BikeArea bkarea=(BikeArea) result.get(s).get("area");
//			
//			System.out.println(bkarea+"  "+ls.size()+"  "+count++);
//		}
//		System.out.println(result.size());
		
	}
	
	/**
	 * 对于划分成若干行和列的区域网格， 得到网格内的一段时间的单车分布
	 * @param files
	 * @param divideAreas
	 */
	public  Map<String, Map<String, Object>> getDurationBikesInMultiAreas(List<Map<String, Object>> files,BikeArea area,int rows,int cols){
		
		Map<String, Map<String, Object>> divideAreas=divideMapToGrid(area, rows, cols);
		
		double startLng=area.getStartLng();
		double startlat=area.getStartLat();
		double endLng=area.getEndLng();
		double endLat=area.getEndLat();
		double colDist=Math.round((endLng-startLng)/cols*1000000)/1000000.0;
		double rowDist=Math.round((startlat-endLat)/rows*1000000)/1000000.0;

		for (Map<String, Object> file : files) {
			BikeHeader header = (BikeHeader) file.get("header");
			@SuppressWarnings("unchecked")
			List<BikePos> bikes = (List<BikePos>) file.get("bikes");

			for(BikePos bike:bikes){
				double lng=bike.getLng();
				double lat=bike.getLat();
				if(CoordsUtil.isInArea(area, lng, lat)){
					
					int col=(int) Math.floor((lng-startLng)/colDist);
					int row=(int) Math.floor((startlat-lat)/rowDist);
					String id=row+"_"+col;
					
					if(divideAreas.containsKey(id)){
						@SuppressWarnings("unchecked")
						List<BikePos> ls=(List<BikePos>) divideAreas.get(id).get("bikes");
						
						ls.add(bike);
						
					}
				}	
			}
		}
		return divideAreas;
	}

	/**
	 * 获取某一时间范围内,某一区域内的单车分布数据
	 * 每个map中包含("header":BikeHeader,"bikes":List<BikePos>);
	 */
	public static List<Map<String, Object>> getDurationBikesInArea(
			List<Map<String, Object>> files, BikeArea area) {

		for (Map<String, Object> file : files) {
			BikeHeader header = (BikeHeader) file.get("header");
			@SuppressWarnings("unchecked")
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
	
	public static void calcuGridID(BikeArea area, int rows, int cols,double lng,double lat){
		
		
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
		
		double startLng=area.getStartLng();
		double startlat=area.getStartLat();
		double endLng=area.getEndLng();
		double endLat=area.getEndLat();
		
		
		double colDist=Math.round((endLng-startLng)/cols*1000000)/1000000.0;
		double rowDist=Math.round((startlat-endLat)/rows*1000000)/1000000.0;
		
		if(rowDist==0||colDist==0){
			return maps;
		}
		int rowCount=0,colCount=0;
		
		double nowLng = startLng;
		double nowLat =startlat;
		do {
			do {
				double cornerLng = Math.round((nowLng+colDist)*1000000)/1000000.0;
				double cornerLat = Math.round((nowLat-rowDist)*1000000)/1000000.0;
				
				BikeArea small = new BikeArea(nowLng, nowLat, cornerLng,
						cornerLat);
				
				String areaID = rowCount + "_" + colCount;
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("area", small);
				mp.put("bikes", new ArrayList<BikePos>());
				maps.put(areaID, mp);

				nowLng = cornerLng;
				colCount++;
			} while (colCount < cols);
			
			nowLat =  Math.round((nowLat-rowDist)*1000000)/1000000.0;
			nowLng = startLng;
			colCount = 0;
			rowCount++;
		} while (rowCount <rows);
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
		for (Map<String, BikePos> pack : packs) {
			Set<String> keys = pack.keySet();

			List<BikePos> ls = new ArrayList<BikePos>();
			for (String key : keys) {
				BikePos bike = pack.get(key);
				ls.add(bike);

			}
			double center[] = CoordsUtil.calcuCenter(ls);
			Point pt = new Point(new double[] { center[0], center[1] }, "1", 1);
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
	public List<Map<String, BikePos>> neighborCluster(List<BikePos> bikes,
			int distance) {

		Map<String, BikePos> pool = new HashMap<String, BikePos>();
		BikePos bp = null;
		for (int i = 0; i < bikes.size(); i++) {
			bp = bikes.get(i);
			pool.put(bp.getBikeID(), bp);
		}

		// 设置搜索距离
		setMapDivideDist(distance);

		// 把收集区域的边界朝四个方向分别扩大若干米
		BikeArea bigArea = getBiggerArea(State.getArea());

		mapSize size = new mapSize();

		// 把区域按照mapDivideDist分割为网状
		Map<String, Map<String, Object>> maps = divideMapToGrid(bigArea,
				mapDivideDist, size);

		// 判断每个单车属于哪一个网格，并添加进去
		pubBikesToGrid(bikes, bigArea, maps, mapDivideDist);

		System.out.println(pool.size() + "     开始聚类");
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
		System.out.println("聚类结果：" + result.size());
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
	@SuppressWarnings("unchecked")
	public List<BikePos> getNearBikes(BikePos bike,
			Map<String, Map<String, Object>> maps, mapSize size) {
		BikeArea area = State.getArea();
		double lng = area.getStartLng();
		double lat = area.getStartLat();
		String findID = "";
		int lngDist = CoordsUtil.calcuDist(lng, lat, bike.getLng(), lat);
		int latDist = CoordsUtil.calcuDist(lng, lat, lng, bike.getLat());
		int lngPos = lngDist / mapDivideDist;

		int latPos = latDist / mapDivideDist;
		findID = latPos + "_" + lngPos;
		List<String> toFindIDs = getAroundAreas(findID, size.getRow(),
				size.getCol(), boder);
		List<BikePos> result = new ArrayList<BikePos>();
		for (String s : toFindIDs) {

			Map<String, Object> around = maps.get(s);
			List<BikePos> list = (List<BikePos>) around.get("bikes");
			int calcuDist = 0;
			for (BikePos pos : list) {
				calcuDist = CoordsUtil.calcuDist(pos.getLng(), pos.getLat(),
						bike.getLng(), bike.getLat());
				if (calcuDist <= minDist) {
					result.add(pos);
				}
			}
		}
		return result;

	}

	public Map<String, List<BikePos>> getBikesWithinDist(List<BikePos> bikes,
			Map<String, Map<String, Object>> maps, mapSize size) {

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
	public BikeArea getBiggerArea(BikeArea area) {
		int borderDist = 400;
		double startLng = area.getStartLng();
		double startLat = area.getStartLat();
		double endLng = area.getEndLng();
		double endLat = area.getEndLat();
		double bigStartLng = CoordsUtil.getLng(startLat, startLng, borderDist,
				false);
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
	public List<String> getAroundAreas(String areaID, int rows, int cols,
			int border) {
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

	/**
	 * 把单车的数据添加到map里面，map以区域ID获取区域，每个区域可以得到area边界，和内部的bikes列表
	 * 
	 * @param bikes
	 * @param area
	 * @param maps
	 * @param dist
	 */
	public void pubBikesToGrid(List<BikePos> bikes, BikeArea area,
			Map<String, Map<String, Object>> maps, int dist) {
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
			Map<String, Object> small = maps.get(findID);
			@SuppressWarnings("unchecked")
			List<BikePos> list = (List<BikePos>) small.get("bikes");

			list.add(b);
		}

	}

	/**
	 * 把区域的边界扩大一点，然后将大区域分割为正方形的小区域，
	 * 
	 * @param area
	 * @param dist
	 * @param size
	 * @return
	 */
	public Map<String, Map<String, Object>> divideMapToGrid(BikeArea area,
			int dist, mapSize size) {
		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>();

		int rows = 0, cols = 0;
		double nowLng = area.getStartLng();
		double nowLat = area.getStartLat();
		do {
			do {
				double cornerLng = CoordsUtil
						.getLng(nowLat, nowLng, dist, true);
				double cornerLat = CoordsUtil.getLat(nowLat, dist, true);
				BikeArea small = new BikeArea(nowLng, nowLat, cornerLng,
						cornerLat);

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

	class mapSize {
		private int row;
		private int col;

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public void setCol(int col) {
			this.col = col;
		}

	}

}