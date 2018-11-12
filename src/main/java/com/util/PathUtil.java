package com.util;

import java.util.Date;

public class PathUtil {

	public static String getFileByTime(String time) {
		String fileName = null;
		if (time.equals("latest")) {
			fileName = FilesUtil.checkLastestFile();
		} else {

			String timePath = DateUtil.parseTimeToFile(time);
			fileName = FilesUtil.DEFAULT_BIKE_FILE + timePath + ".txt";
		}
		return fileName;
	}

	public static String getFileByTime(Date time) {
		String timePath = DateUtil.timeToPath(time);

		timePath = FilesUtil.DEFAULT_BIKE_FILE + timePath + ".txt";
		return timePath;
	}
}
