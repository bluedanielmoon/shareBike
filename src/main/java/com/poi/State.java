package com.poi;

import java.util.Date;
import com.pojo.BikeArea;

public class State {

		//玉祥门区域
		//public static BikeArea area=new BikeArea(108.918125, 34.270856, 108.923468, 34.2684);
		
		//从大兴立交到桥梓口
		//public static BikeArea area=new BikeArea(108.891913, 34.286154, 108.93251, 34.259512);
		
		
		public static BikeArea area=new BikeArea(108.891913, 34.286154, 108.996753, 34.240899);
		
		//三环线
		//public static BikeArea area=new BikeArea(108.841793,34.343364, 109.05431,34.185037);
		
		public static int collectThreadSize = 20;
		public static int collectMaxRunTime =60;//minutes
		public static int jobPrintGap=5;//seconds
		public static long schedulGap=60*60*1000;//milliSeconds
		public static int lngLatGap = 50;//meter

		public static void setArea(BikeArea area) {
			State.area = area;
		}
		
		/**
		 * 设置收集的线程数
		 * @param threadSize
		 */
		public static void setThreadSize(int threadSize) {
			State.collectThreadSize = threadSize;
		}
		
		/**
		 * 设置Collecor最大运行时间，单位(分钟)
		 * @param runTime
		 */
		public static void setRunTime(int runTime) {
			State.collectMaxRunTime = runTime;
		}
		
		/**
		 * 设置单车收集进度的间隔，单位(秒)
		 * @param jobPrintGap
		 */
		public static void setJobPrintGap(int jobPrintGap) {
			State.jobPrintGap = jobPrintGap;
		}

		/**
		 * 设置收集区域的间隔,单位(米)
		 * @param lngLatGap
		 */
		public static void setLngLatGap(int lngLatGap) {
			State.lngLatGap = lngLatGap;
		}
		

		
}
