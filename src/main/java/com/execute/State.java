package com.execute;

import com.pojo.BikeArea;

public class State {
	//从大兴立交到二环东南角（二环线）
	private static BikeArea area = new BikeArea(108.891913, 34.286154,108.996753, 34.240899);

	public static BikeArea getArea() {
		return area;
	}

	public static void setArea(BikeArea area) {
		State.area = area;
	}

}
