package com.init;

import com.pojo.BikeArea;

public class State {

		public static int SITE_NORMAL_TYPE;
		public static int SITE_REMEND_TYPE;
	    
		public static int POI_BUS;
		public static int POI_SUBWAY;
		
		public static int TRUCK_TYPE=1;
		public static int TRUCK_SPEED;
		public static int TRUCK_CAPACITY=30;
		public static int TRICYCLE_TYPE=2;
		public static int TRICYCLE_SPEED;
		public static int TRICYCLE_CAPACITY=15;
		public static int MAN_TYPE=3;
		public static int MAN_SPEED;
		public static int MAN_CAPACITY=1;
		
		//从某个站点到另一个站点，路径时间乘以浪费系数来模拟实际的消耗时间
		public static double WASTETIME_MOVE_RATIO=1.5;
		public static int WASTETIME_LOAD=120;
		
		
		//任务类型，移动任务，搬运任务
		public static int LOAD_TASK=1;
		public static int WAIT_TASK=2;
		

		public static int NEAR_SITE_DIST = 3000;
		
		public static int WAIT_TIME=600;
		
		//对某一站点搬运的策略，load--搬走，unload--往下搬,ignore--不管
		public static int LOAD=1;
		public static int UNLOAD=2;
		public static int IGNORE=3;
		
		public static final int SITE_STORAGE_NUM=20;
		
		//某个地点单车小于等于这个数，不搬走
		public static int MIN_SITE_BIKE_COUNT=3;
		
		//单位单车的装卸在时间--秒
		public static int LOAD_UNIT_TIME=30;
		
		//某个点的单车是增长还是减少
		public static int GROW_TREND=1;
		public static int REDUCE_TREND=2;
		
		//SiteTypeJudger里面的父类型
		public static final int MOUNTAIN = 1;// 山峰类型
		public static final int VALLY = 2;// 山谷类型
		public static final int FLAT = 3;// 平原
		
		//SiteTypeJudger里面的淤积类型
		public static final int BUMP_BIG = 1;// 强淤积
		public static final int BUMP_MIDDLE = 2;// 中淤积
		public static final int BUMP_SMALL = 3;// 弱淤积
		public static final int BUMP_NONE = 4;// 无淤积
		
		
		public static final int FLOW_IN = 1;// 从某个站点流入
		public static final int FLOW_OUT = 2;// 从某个站点流出
		
		//流动占所有站点流动的比例，大于这个比例就会被选中
		public static double FLOW_RATIO = 0.08;
		
		//选择站点的时候距离和数量评比所占的比例
		public static double SITE_CHOOSE_RATIO = 0.5;
		public static double SITE_CHOOSE_BEST = 0.6;
		
		public static int DIVIDE_DIST=50;

		public static BikeArea AREA=new BikeArea(108.891913, 34.286154, 108.996753, 34.240899);
	    
		
		public State() {}

		public static int getSITE_NORMAL_TYPE() {
			return SITE_NORMAL_TYPE;
		}

		public static void setSITE_NORMAL_TYPE(int sITE_NORMAL_TYPE) {
			SITE_NORMAL_TYPE = sITE_NORMAL_TYPE;
		}

		public static int getSITE_REMEND_TYPE() {
			return SITE_REMEND_TYPE;
		}

		public static void setSITE_REMEND_TYPE(int sITE_REMEND_TYPE) {
			SITE_REMEND_TYPE = sITE_REMEND_TYPE;
		}

		public static int getPOI_BUS() {
			return POI_BUS;
		}

		public static void setPOI_BUS(int pOI_BUS) {
			POI_BUS = pOI_BUS;
		}

		public static int getPOI_SUBWAY() {
			return POI_SUBWAY;
		}

		public static void setPOI_SUBWAY(int pOI_SUBWAY) {
			POI_SUBWAY = pOI_SUBWAY;
		}

		public static BikeArea getAREA() {
			return AREA;
		}

		public static void setAREA(double[] list) {
			AREA=new BikeArea(list[0], list[1], list[2], list[3]);
		}

		public static int getTRUCK_TYPE() {
			return TRUCK_TYPE;
		}

		public static void setTRUCK_TYPE(int tRUCK_TYPE) {
			TRUCK_TYPE = tRUCK_TYPE;
		}

		public static int getTRUCK_SPEED() {
			return TRUCK_SPEED;
		}

		public static void setTRUCK_SPEED(int tRUCK_SPEED) {
			TRUCK_SPEED = tRUCK_SPEED;
		}

		public static int getTRUCK_CAPACITY() {
			return TRUCK_CAPACITY;
		}

		public static void setTRUCK_CAPACITY(int tRUCK_CAPACITY) {
			TRUCK_CAPACITY = tRUCK_CAPACITY;
		}

		public static int getTRICYCLE_TYPE() {
			return TRICYCLE_TYPE;
		}

		public static void setTRICYCLE_TYPE(int tRICYCLE_TYPE) {
			TRICYCLE_TYPE = tRICYCLE_TYPE;
		}

		public static int getTRICYCLE_SPEED() {
			return TRICYCLE_SPEED;
		}

		public static void setTRICYCLE_SPEED(int tRICYCLE_SPEED) {
			TRICYCLE_SPEED = tRICYCLE_SPEED;
		}

		public static int getTRICYCLE_CAPACITY() {
			return TRICYCLE_CAPACITY;
		}

		public static void setTRICYCLE_CAPACITY(int tRICYCLE_CAPACITY) {
			TRICYCLE_CAPACITY = tRICYCLE_CAPACITY;
		}

		public static int getMAN_TYPE() {
			return MAN_TYPE;
		}

		public static void setMAN_TYPE(int mAN_TYPE) {
			MAN_TYPE = mAN_TYPE;
		}

		public static int getMAN_SPEED() {
			return MAN_SPEED;
		}

		public static void setMAN_SPEED(int mAN_SPEED) {
			MAN_SPEED = mAN_SPEED;
		}

		public static int getMAN_CAPACITY() {
			return MAN_CAPACITY;
		}

		public static void setMAN_CAPACITY(int mAN_CAPACITY) {
			MAN_CAPACITY = mAN_CAPACITY;
		}

		public static int getLOAD_TASK() {
			return LOAD_TASK;
		}

		public static void setLOAD_TASK(int lOAD_TASK) {
			LOAD_TASK = lOAD_TASK;
		}

		public static int getLOAD() {
			return LOAD;
		}

		public static void setLOAD(int lOAD) {
			LOAD = lOAD;
		}

		public static int getUNLOAD() {
			return UNLOAD;
		}

		public static void setUNLOAD(int uNLOAD) {
			UNLOAD = uNLOAD;
		}

		public static int getIGNORE() {
			return IGNORE;
		}

		public static void setIGNORE(int iGNORE) {
			IGNORE = iGNORE;
		}

		public static int getMIN_SITE_BIKE_COUNT() {
			return MIN_SITE_BIKE_COUNT;
		}

		public static void setMIN_SITE_BIKE_COUNT(int mIN_SITE_BIKE_COUNT) {
			MIN_SITE_BIKE_COUNT = mIN_SITE_BIKE_COUNT;
		}

		public static int getLOAD_UNIT_TIME() {
			return LOAD_UNIT_TIME;
		}

		public static void setLOAD_UNIT_TIME(int lOAD_UNIT_TIME) {
			LOAD_UNIT_TIME = lOAD_UNIT_TIME;
		}

		public static int getGROW_TREND() {
			return GROW_TREND;
		}

		public static void setGROW_TREND(int gROW_TREND) {
			GROW_TREND = gROW_TREND;
		}

		public static int getREDUCE_TREND() {
			return REDUCE_TREND;
		}

		public static void setREDUCE_TREND(int rEDUCE_TREND) {
			REDUCE_TREND = rEDUCE_TREND;
		}

		public static void setAREA(BikeArea aREA) {
			AREA = aREA;
		}
		
		

		
}
