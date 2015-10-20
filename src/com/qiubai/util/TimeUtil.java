package com.qiubai.util;

public class TimeUtil {

	/**
	 * 
	 * @param time
	 * @return
	 */
	public static boolean compareTime(long time) {
		Long l = System.currentTimeMillis();
		if (l - time > 130000) {
			return true;
		} else {
			return false;
		}
	}

}
