package com.execute;

public class ProgPorter implements Runnable{

	private int printPeriod;
	private boolean shut=false;
	/**
	 * 
	 * @param printPeriod 打印的间隔（毫秒）
	 */
	public ProgPorter(int printPeriod) {
		this.printPeriod=printPeriod;
	}
	@Override
	public void run() {
		while(!shut){
			try {
				Thread.sleep(printPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	public void shutDown(){
		shut=true;
	}

}
