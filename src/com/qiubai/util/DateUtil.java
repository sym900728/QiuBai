package com.qiubai.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author MGC10 工具类，获得当前的时间
 * 
 */
public class DateUtil {
	public static String getCurrentTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(System.currentTimeMillis());
		return currentTime;
	}
	
	
	/**
	 * @param format
	 * @param index
	 * @return 明天或者后天的时间
	 */
	public static String getTomorrowTime(String format,int index){
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(System.currentTimeMillis()+1000*60*60*24*index);
		return currentTime;
	}

	/**
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTime() {
		String format = "yyyy-MM-dd HH:mm:ss";
		return getCurrentTime(format);
	}

	/**
	 * @return yyyyMMddHHmm
	 */
	public static String getCurrentByTime() {
		String format = "yyyyMMddHHmm";
		return getCurrentTime(format);
	}

	/**
	 * @return EEEE Monday
	 */
	public static String getCurrentWeekendTime() {
		String format = "EEEE";
		return getCurrentTime(format);
	}
	
	/**
	 * @return 明天的星期几
	 */
	public static String getTomorrowWeekendTime(){
		String format = "EEEE";
		return getTomorrowTime(format, 1);
	}
	
	/**
	 * @return 后天的星期几
	 */
	public static String getAfterWeekendTime(){
		String format = "EEEE";
		return getTomorrowTime(format, 2);
	}

	/**
	 * @return MM/dd (04/24)
	 */
	public static String getCurrentDayTime() {
		String format = "MM/dd";
		return getCurrentTime(format);
	}

	/**
	 * @param time
	 *            201504291100
	 * @return 04-29 11:00 发布
	 */
	public static String getWeatherPublishTime(String time) {
		// 201504291100
		String month = time.substring(4, 6);
		String day = time.substring(6, 8);
		String hour = time.substring(8, 10);
		String minute = time.substring(10);

		return month + "-" + day + " " + hour + ":" + minute + " " + "发布";
	}

	/**
	 * @return 1100
	 */
	public static long getCurrentHourMinute() {
		String format = "HHmm";
		return Long.parseLong(getCurrentTime(format));
	}

	/**
	 * @return 20150506
	 */
	public static String getCurrentDay() {
		String format = "yyyyMMdd";
		return getCurrentTime(format);
	}

}
