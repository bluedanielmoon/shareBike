package com.simu;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.pojo.Dispatcher;
import com.pojo.Lnglat;


public class SimuManager implements Runnable{

	public ConcurrentHashMap<String, Simulator> simulers = new ConcurrentHashMap<>();
	public BlockingQueue<Simulator> waitQueue=new LinkedBlockingQueue<>();
	public BlockingQueue<Simulator> runQueue=new LinkedBlockingQueue<>();
	private List<Dispatcher> dispatchers;
	private List<Lnglat> startPos=new ArrayList<>();
	private Date date; 
	private int startHour; 
	private int endHour;
	private Path filePath;
	private int simuCount=1;
	
	private boolean finished=false;
	
	public SimuManager(List<Dispatcher> dispatchers, Date date, int startHour, int endHour,Path filePath) {
		this.dispatchers=dispatchers;
		for(Dispatcher disp:dispatchers) {
			Lnglat lnglat=new Lnglat(disp.getLng(), disp.getLat());
			startPos.add(lnglat);
		}
		this.date=date;
		this.startHour=startHour;
		this.endHour=endHour;
		this.filePath=filePath;
	}

	public Simulator getSimuler(String id) {

		if (simulers.containsKey(id)) {
			return simulers.get(id);
		}
		return null;
	}

	@Override
	public void run() {
		while (!finished) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (waitQueue.isEmpty()) {
				System.out.println("等待队列关闭");
				finished=true;
			}else {
				if (runQueue.size()==0) {
					System.out.println("新线程开启。。。。。。");
					Simulator simulator = null;
					try {
						simulator = waitQueue.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//初始化调度车信息
					for (int i = 0; i < dispatchers.size(); i++) {
						Dispatcher disp=dispatchers.get(i);
						Lnglat pos=startPos.get(i);
						disp.setLng(pos.getLng());
						disp.setLat(pos.getLat());
						disp.setStorage(0);
					}
					simuCount++;
					simulator.init(dispatchers, date, startHour, endHour,false,filePath,simuCount);
					new Thread(simulator).start();
					runQueue.add(simulator);
				}
			}
			
		}
		
		
	}

}
