package com.simu;


public class TaskManager {
	
	
//
//
//	public void run() {
//		
//		long startSecond = System.currentTimeMillis() / 1000;
//		System.out.println("模拟开始,开始时间：" + startSecond);
//
//		while (true) {
//			if (paused) {
//
//				try {
//					synchronized (pauser) {
//						pauser.wait();
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			if (ended) {
//				break;
//			}
//			try {
//				Thread.sleep(100000);
//				
//				
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			
//		}
//		System.out.println("模拟结束");
//	}
//	public void pause() {
//		synchronized (pauser) {
//			paused = true;
//			System.out.println("模拟暂停");
//		}
//
//	}
//
//	public void resume() {
//		synchronized (pauser) {
//			paused = false;
//			pauser.notify();
//			System.out.println("模拟恢复");
//		}
//
//	}
//
//	public void stop() {
//		synchronized (pauser) {
//			ended = true;
//		}
//	}

	
}
