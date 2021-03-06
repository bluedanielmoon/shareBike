package com.util;

import java.util.List;

import com.pojo.BikeArea;
import com.pojo.BikePos;
import com.pojo.Lnglat;

public class CoordsUtil {
	private static final double EARTH_RADIUS = 6378137;// m
	private static final double METER_PER_LAT = Math.PI * EARTH_RADIUS / 180;
	private static final int DIGIT = 1000000;

	private static double ConvertDegreesToRadians(double degrees) {
		return degrees * Math.PI / 180.0;
	}

//	private static double ConvertRadiansToDegrees(double radian) {
//		return radian * 180.0 / Math.PI;
//	}

	/**
	 * 手里拿着高德坐标，现在要往百度地图画。百度相当于把坐标往左下角挪了，现在要挪回去
	 */
	public static double[] turnBaiDuCoord(double lng,double lat) {
		double baiLng=getLng(lat, lng, 605, true);
		double baiLat=getLat(lat, 633, false);
		
		return new double[] {baiLng,baiLat};
	}
	
	/**
	 * 手里拿着高德坐标，现在要往百度地图画。百度相当于把坐标往左下角挪了，现在要挪回去
	 */
	public static Lnglat turnBaiDuCoord(Lnglat ll) {
		double baiLng=getLng(ll.getLat(), ll.getLng(), 605, true);
		double baiLat=getLat(ll.getLat(), 633, false);
		return new Lnglat(baiLng, baiLat);
	}
	/**
	 * 手里拿着百度地图坐标，返回高德坐标
	 * @param lng
	 * @param lat
	 */
	
	public static double[] turnGaodeCoord(double lng,double lat) {
		double gaoLng=getLng(lat, lng, 605, false);
		double gaoLat=getLat(lat, 633, true);
		
		return new double[] {gaoLng,gaoLat};
	}
	
	public static Lnglat turnGaodeCoord(Lnglat ll) {
		double gaoLng=getLng(ll.getLat(), ll.getLng(), 605, false);
		double gaoLat=getLat(ll.getLat(), 633, true);
		return new Lnglat(gaoLng, gaoLat);
	}
	
	private static double haverSin(double theta) {
		double v = Math.sin(theta / 2);
		return v * v;
	}

	public static Lnglat getAreaCenter(BikeArea area) {
		Lnglat lnglat = new Lnglat();
		
		
		lnglat.setLng((long)((area.getStartLng() + area.getEndLng()) / 2*1000000)/1000000.0);
		lnglat.setLat((long)((area.getStartLat() + area.getEndLat()) / 2*1000000)/1000000.0);
		return lnglat;
	}
	
	
	public static BikeArea getCenterArea(Lnglat center) {
		int dist=50;
		BikeArea area=new BikeArea();
		double LngRight=getLng(center.getLat(), center.getLng(), dist, true);
		double LngLeft=getLng(center.getLat(), center.getLng(), dist, false);
		double latDown=getLat(center.getLat(), dist, true);
		double latUp=getLat(center.getLat(), dist, false);
		area.setStartLng(LngLeft);
		area.setStartLat(latUp);
		area.setEndLng(LngRight);
		area.setEndLat(latDown);
		return area;
	}

	

	public static boolean isInArea(BikeArea area, double lng, double lat) {
		double startLat = area.getStartLat();
		double startLng = area.getStartLng();
		double endLat = area.getEndLat();
		double endLng = area.getEndLng();
		if (lng > startLng && lng < endLng && lat < startLat && lat > endLat) {
			return true;
		} else {
			return false;
		}

	}
	
	public static Lnglat getRatioPos(Lnglat A,Lnglat B,double ratio) {
		double lng=	(long)(((1-ratio)*A.getLng()+ratio*B.getLng())*1000000)/1000000.0;	
		double lat=(long)(((1-ratio)*A.getLat()+ratio*B.getLat())*1000000)/1000000.0;
		return new Lnglat(lng, lat);
	}

	/**
	 * 检查输入的是否是适合形成中国大部分地区的矩形区域的坐标
	 * 
	 * @param area 4个参数，分别是左上角和右下角的经度+纬度
	 * @return
	 */
	public static boolean checkLnglat(double[] area) {
		if (area.length != 4) {
			return false;
		}
		if (area[0] < 73 || area[0] > 136 || area[2] < 73 || area[2] > 136) {
			return false;
		}
		if (area[1] < 3 || area[1] > 53 || area[3] < 3 || area[3] > 53) {
			return false;
		}
		if (area[0] >= area[2] || area[1] <= area[3]) {
			return false;
		}
		if (area[0] < area[1] || area[2] < area[3]) {
			return false;
		}
		return true;
	}

	/**
	 * 返回一堆点的中心
	 * 
	 * @param ls
	 * @return 经度在0,纬度在1的数组
	 */
	public static double[] calcuCenter(List<BikePos> ls) {
		double totalLng = 0, totalLat = 0;

		for (BikePos bk : ls) {
			totalLng += bk.getLng();
			totalLat += bk.getLat();
		}
		return new double[] { totalLng / ls.size(), totalLat / ls.size() };
	}
	
	public static double[] calcuCenterWithPower(List<BikePos> ls) {
		double totalLng = 0, totalLat = 0;
		
		double total=0;
		for (BikePos bk : ls) {
			double pow=Double.parseDouble(bk.getBikeID());
			total+=pow;
		}
		double temp=0;
		for (BikePos bk : ls) {
			double pow=Double.parseDouble(bk.getBikeID());
			temp=pow/total;
			totalLng += bk.getLng()*temp;
			totalLat += bk.getLat()*temp;
		}
		return new double[] { totalLng ,totalLat  };
	}
	
	public static double[] calcuLngLatCenter(List<Lnglat> ls) {
		double totalLng = 0, totalLat = 0;

		for (Lnglat bk : ls) {
			totalLng += bk.getLng();
			totalLat += bk.getLat();
		}
		return new double[] { totalLng / ls.size(), totalLat / ls.size() };
	}

	public static int calcuDist(double lng1, double lat1, double lng2, double lat2) {
		lng1 = ConvertDegreesToRadians(lng1);
		lat1 = ConvertDegreesToRadians(lat1);
		lng2 = ConvertDegreesToRadians(lng2);
		lat2 = ConvertDegreesToRadians(lat2);

		double vLon = Math.abs(lng1 - lng2);
		double vLat = Math.abs(lat1 - lat2);
		double h = haverSin(vLat) + Math.cos(lat1) * Math.cos(lat2) * haverSin(vLon);

		double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));
		return (int) Math.round(distance);
	}

	/**
	 * 给定一个纬度，获取该经度向上或者向下的下一个纬度
	 * 
	 * @param lat  纬度
	 * @param dist 移动的距离
	 * @param down 是否向下
	 * @return 得到的下一个纬度
	 */
	public static double getLat(double lat, double dist, boolean down) {
		double x = dist / METER_PER_LAT;
		return down ? (double) Math.round((lat - x) * DIGIT) / DIGIT : (double) Math.round((lat + x) * DIGIT) / DIGIT;

	}

	/**
	 * 给定一个纬度，一个经度，获取该纬度上向右或者向左一定距离的下一个经度
	 * 
	 * @param lat   纬度
	 * @param lng   经度
	 * @param dist  移动的距离（米）
	 * @param right 是否向右
	 * @return 得到的下一个经度
	 */
	public static double getLng(double lat, double lng, double dist, boolean right) {

		lat = ConvertDegreesToRadians(lat);
		double x = dist / (METER_PER_LAT * Math.cos(lat));

		return right ? (double) Math.round((lng + x) * DIGIT) / DIGIT : (double) Math.round((lng - x) * DIGIT) / DIGIT;
	}
}
