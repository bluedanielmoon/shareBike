package com.poi;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.pojo.Bike;
import com.pojo.BikeHeader;
import com.pojo.Weather;
import com.util.FilesUtil;

public class ResultDealer {
	private static ReentrantLock lock = new ReentrantLock();

	public static void writeResult(BikeHeader header,Map<String, Bike> bikeData) {

		lock.lock();
		try {
			
			FilesUtil.writeBikeToFile(header, bikeData);
		} finally {
			lock.unlock();	
		}
	}
	
	
}
