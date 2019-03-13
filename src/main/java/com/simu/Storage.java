package com.simu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simu.SimuManager;
import com.simu.Simulator;

@Component
public class Storage {

//	public Storage() {
//		new Thread(new reporter()).start();
//	}

	@Autowired
	public static ConcurrentHashMap<String, SimuManager> simulers = new ConcurrentHashMap<>();

	
	public static SimuManager getSimuler(String id) {

		if (simulers.containsKey(id)) {
			return simulers.get(id);
		}
		return null;
	}

	public class reporter implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(simulers.size() + "--------------");

			}
		}

	}

}
