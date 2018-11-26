package com.execute;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pojo.Bike;
import com.pojo.BikeHeader;
import com.pojo.Lnglat;
import com.pojo.LnglatTime;
import com.util.CoordsUtil;
import com.util.FilesUtil;
import com.util.MapperUtil;

public class BikeTracker {
	private final static String DEFAULT_FILE="/Users/daniel/projects/trackData/";
	private final int LAST_NOW_DISTANCE=100;

	public static void main(String[] args) {

		BikeTracker tracker = new BikeTracker();
		tracker.writeToFile();
	}
	
	public void writeToFile() {
		Map<String, List<LnglatTime>> data=getBikesToDay();
		String file=DEFAULT_FILE+"track1.txt";
		System.out.println("共有单车："+data.size());
		List<Integer> statis=getMoveCount(data);
		System.out.println(statis);
		MapperUtil.writeMapListData(file, data, LnglatTime.class);
	}
	
	public List<Integer> getMoveCount(Map<String, List<LnglatTime>> data){
		int[] counts=new int[1000];
		for(String s:data.keySet()) {
			List<LnglatTime> bkList=data.get(s);
			counts[bkList.size()-1]++;
		}
		List<Integer> result=new ArrayList<>();
		for(int i=0;i<counts.length;i++) {
			if(counts[i]!=0) {
				result.add(counts[i]);
			}else {
				boolean flag=true;
				for(int j=i;j<i+5&&j<1000;j++) {
					if(counts[j]!=0) {
						flag=false;
					}
				}
				if(flag){
					break;
				}else {
					result.add(counts[i]);
				}
			}
		}
		return result;
	}

	public Map<String, List<LnglatTime>> getBikesToDay() {
		List<Path> allFiles = FilesUtil.listAllFiles();
		Map<String, List<LnglatTime>> bikes = new HashMap<>();

		String bikeId = null;
		for (Path p : allFiles) {
			Map<String, Object> file = FilesUtil.readFileToBikeList(p.toString());

			BikeHeader header = (BikeHeader) file.get("header");
			List<Bike> fileBikes = (List<Bike>) file.get("bikes");
			Date time = header.getStartTime();

			for (Bike b : fileBikes) {
				bikeId = b.getDistId();
				if (bikes.containsKey(bikeId)) {
					List<LnglatTime> posList = bikes.get(bikeId);
					if(!posList.isEmpty()) {
						LnglatTime lastPos=posList.get(posList.size()-1);
						LnglatTime pos = new LnglatTime(Double.parseDouble(b.getDistX()), Double.parseDouble(b.getDistY()),
								time);
						//检查上次和本次的距离，如果距离很少，表示没有移动过
						if(CoordsUtil.calcuDist(lastPos.getLng(), lastPos.getLat(), pos.getLng(), pos.getLat())>LAST_NOW_DISTANCE) {
							posList.add(pos);
						}
					}else {
						LnglatTime pos = new LnglatTime(Double.parseDouble(b.getDistX()), Double.parseDouble(b.getDistY()),
								time);

						posList.add(pos);
					}

					
				} else {
					List<LnglatTime> posList = new ArrayList<>();
					LnglatTime pos = new LnglatTime(Double.parseDouble(b.getDistX()), Double.parseDouble(b.getDistY()),
							time);

					posList.add(pos);
					bikes.put(bikeId, posList);
				}
			}
		}
		return bikes;

	}
}
