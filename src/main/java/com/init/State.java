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
		
		public static int MOVE_TASK=1;
		public static int LOAD_TASK=2;
		
		public static int LOAD=1;
		public static int UNLOAD=2;
		
		public static int MIN_SITE_BIKE_COUNT=3;
		
		public static int LOAD_UNIT_TIME=10;
		

	    
		public static BikeArea AREA;
		
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
		
		

		
}
