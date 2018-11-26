package com.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.execute.State;
import com.pojo.BikeArea;
import com.pojo.GaodeLine;
import com.pojo.GaodeStop;

public class BusUtil {
	//stopsfilter15Circle2.txt
	public static String BUS_STOP_FILE = "/Users/daniel/projects/busLine/poi_busStop.txt";
	public static String SUBWAY_FILE = "/Users/daniel/projects/busLine/poi_subway.txt";
	
	public static String BASE_DIR ="/Users/daniel/projects/busLine/";
	
	public static List<GaodeStop> readBusStops() {
		List<GaodeStop> stops=FilesUtil.readFromFile(BUS_STOP_FILE, GaodeStop.class);
		return stops;
	}
	
	public static List<GaodeStop> readSubStations() {
		List<GaodeStop> stops=FilesUtil.readFromFile(SUBWAY_FILE, GaodeStop.class);
		return stops;
	}
	
	//提取二环内的公交站
	public static void writeStopsInCircle2(){
		BikeArea area=State.getArea();
		String file=BASE_DIR+"stopsfilter15.txt";
		List<GaodeStop> lgs=FilesUtil.readFromFile(file, GaodeStop.class);
		List<GaodeStop> result=new ArrayList<GaodeStop>();
		for(GaodeStop s:lgs){
			if(CoordsUtil.isInArea(area, s.getLocation().getLng(), s.getLocation().getLat())){
				result.add(s);
			}
		}
		System.out.println("all stops"+lgs.size());
		System.out.println("in circle"+result.size());
		String outFile=BASE_DIR+"stopsfilter15Circle2.txt";
		FilesUtil.writeListToFile(outFile, result, GaodeStop.class);
		
		
	}
	
	private static boolean canAddStop(List<GaodeStop> ll,GaodeStop stop,int size){
		boolean canAdd=true;
		for(int i=0;i<ll.size();i++){
			GaodeStop s=ll.get(i);
			if(stop.getLocation().getLng()==s.getLocation().getLng()||
					stop.getLocation().getLat()==s.getLocation().getLat()||
					CoordsUtil.calcuDist(s.getLocation().getLng(),
					s.getLocation().getLat(), 
					stop.getLocation().getLng(),
					stop.getLocation().getLat())<size){
				canAdd=false;
				break;
			}
		}
		return canAdd;
		
	}
	
	public static void main(String[] args) {
		writeStopsInCircle2();
	}
	
	/**
	 * 提取所有的公交线路，但是公交站存在坐标不重合，所以通过距离筛除那些过近的点
	 */
	private static void getStops() {
		int size=15;
		
		Map<String, List<GaodeStop>> mp=new HashMap<String, List<GaodeStop>>();
		String file = BASE_DIR + "allLines.txt";
		List<GaodeLine> lb = FilesUtil.readFromFile(file, GaodeLine.class);
		for (GaodeLine line : lb) {
			if(line.getCompany().equals("西安市地下铁道有限责任公司")) {
				System.out.println(line);
				continue;
			}
			GaodeStop[] stops = line.getVia_stops();

			for (GaodeStop stop : stops) {
				String stopID=stop.getId();
				if(!mp.containsKey(stopID)){
					List<GaodeStop> ll=new ArrayList<GaodeStop>();
					ll.add(stop);
					mp.put(stopID, ll);
				}else{
					//过滤某个距离以下的点
					List<GaodeStop> ll=mp.get(stopID);
					if(canAddStop(ll, stop,size)){
						ll.add(stop);
					}
				}
			}
		}
		int all=0;
		List<GaodeStop> filterStops=new ArrayList<GaodeStop>();
		for(String s:mp.keySet()){
			List<GaodeStop> lp=mp.get(s);
			System.out.println(s+lp.get(0).getName()+"----  "+lp.size());
			all+=lp.size();
			filterStops.addAll(lp);
		}
		System.err.println("sites: "+mp.size());
		System.out.println(all);
		String fileOut=BASE_DIR+ "stopsfilter"+size+".txt";
		FilesUtil.writeListToFile(fileOut, filterStops, GaodeStop.class);

	}
}
