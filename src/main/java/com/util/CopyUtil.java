package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pojo.BikePos;

public class CopyUtil {

	public static <T> Object deepCopy(T obj) {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bout);
			oos.writeObject(obj);

			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bin);
			return ois.readObject();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	

	public static void main(String[] args) {
		Map<String, BikePos> map = new HashMap<>();
		BikePos b1 = new BikePos("1", 108.1, 34.1, 1);
		BikePos b2 = new BikePos("2", 108.2, 34.2, 1);
		
		Map<String, BikePos> map2 = new HashMap<>();
		BikePos b3 = new BikePos("3", 108.3, 34.3, 1);
		BikePos b4 = new BikePos("4", 108.4, 34.4, 1);

		map.put(b1.getBikeID(), b1);
		map.put(b2.getBikeID(), b2);
		map2.put(b3.getBikeID(), b3);
		map2.put(b4.getBikeID(), b4);
		
		List<Map<String, BikePos>> ls= new ArrayList<>();
		ls.add(map);
		ls.add(map2);
		// 应用hashmap的方法clone得到的是浅复制
		// HashMap<String, BikePos> map2=(HashMap<String, BikePos>) map.clone();
		// map2.get("1").setBikeID("6");
//		System.out.println(map);
//		System.out.println(map2);

		
		//深复制，必须实现serializable接口
//		Map<String, Object> map3 = (Map<String, Object>) deepCopy(ls);
//		BikePos pos = (BikePos) map3.get("1");
//		pos.setBikeID("6");
//		System.out.println(map);
//		System.out.println(map3);
		
		
		List<Map<String, BikePos>> ls2=(List<Map<String, BikePos>>) deepCopy(ls);
		ls2.get(0).get("1").setBikeID("7");
//		System.out.println(ls);
//		System.out.println(ls2);
	}

}
