package com.qiubai.entity;

import com.bt.qiubai.R;

public enum WeatherPhenomena {
	Sunny("晴", "00", R.drawable.day00, R.drawable.night00), 
	Cloudy("多云", "01", R.drawable.day01, R.drawable.night01), 
	Overcast("阴", "02", R.drawable.day02, R.drawable.night02),
	Shower("阵雨", "03", R.drawable.day03, R.drawable.night03),
	Thundershower("雷阵雨", "04", R.drawable.day04, R.drawable.night04),
	ThundershowerWithHail("雷阵雨伴有冰雹","05", R.drawable.day05, R.drawable.night05),
	Sleet("雨夹雪", "06", R.drawable.day06, R.drawable.night06),
	LightRain("小雨", "07", R.drawable.day07, R.drawable.night07),
	ModerateRain("中雨", "08",R.drawable.day08, R.drawable.night08),
	HeavyRain("大雨", "09", R.drawable.day09, R.drawable.night09),
	Storm("暴雨", "10", R.drawable.day10, R.drawable.night10),
	HeavyStorm("大暴雨", "11",R.drawable.day11, R.drawable.night11),
	SevereStorm("特大暴雨", "12", R.drawable.day12, R.drawable.night12),
	SnowFlurry("阵雨", "13", R.drawable.day13, R.drawable.night13),
	LightSnow("小雪", "14",R.drawable.day14, R.drawable.night14), 
	ModerateSnow("中雪", "15", R.drawable.day15, R.drawable.night15),
	HeavySnow("大雪", "16", R.drawable.day16, R.drawable.night16),
	SnowStorm("暴雪", "17",R.drawable.day17, R.drawable.night17),
	Foggy("雾", "18", R.drawable.day18, R.drawable.night18),
	IceRain("冻雨", "19", R.drawable.day19, R.drawable.night19),
	DustStorm("沙尘暴", "20",R.drawable.day20, R.drawable.night20),
	LightToModerateRain("小到中雨", "21",R.drawable.day21, R.drawable.night21),
	ModerateToHeavyRain("中到大雨", "22",R.drawable.day22, R.drawable.night22),
	HeavyRainToStorm("大到暴雨", "23", R.drawable.day23, R.drawable.night23),
	StormToHeavyStorm("暴雨到大暴雨", "24", R.drawable.day24, R.drawable.night24),
	HeavyToSevereStorm("大暴雨到特大暴雨","25", R.drawable.day25, R.drawable.night25),
	LightToModerateSnow("小到中雪", "26",R.drawable.day26, R.drawable.night26),
	ModerateToHeavySnow("中到大雪", "27",R.drawable.day27, R.drawable.night27),
	HeavySnowToSnowStorm("大到暴雪", "28",R.drawable.day28, R.drawable.night28),
	Dust("浮尘", "29", R.drawable.day29, R.drawable.night29),
	Sand("扬沙","30", R.drawable.day30, R.drawable.night30),
	SandStorm("强沙尘暴", "31", R.drawable.day31, R.drawable.night31),
	Haze("霾", "53", R.drawable.day53, R.drawable.night53),
	Unknown("无", "99", R.drawable.dayundefined, R.drawable.dayundefined);

	private String name;
	private String index;
	private int imageResource;
	private int nightImageResource;

	

	private WeatherPhenomena(String name, String index, int imageResource,
			int nightImageResource) {
		this.name = name;
		this.index = index;
		this.imageResource = imageResource;
		this.nightImageResource = nightImageResource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	public int getNightImageResource() {
		return nightImageResource;
	}

	public void setNightImageResource(int nightImageResource) {
		this.nightImageResource = nightImageResource;
	}

	/**
	 * 根据字符串返回相对应的天气现象
	 * 
	 * @param index
	 * @return
	 */
	public static String getPhenomenaName(String index) {

		for (WeatherPhenomena wp : WeatherPhenomena.values()) {
			if (wp.getIndex().equals(index)) {
				return wp.getName();
			}
		}
		return null;

	}
	
	/**
	 * 根据字符串返回相对应的天气现象图标
	 * @param index
	 * @return
	 */
	public static int getPhenomenaPicture(String index){
		for (WeatherPhenomena wp : WeatherPhenomena.values()) {
			if (wp.getIndex().equals(index)) {
				return wp.getImageResource();
			}
		}
		return 0;
	}
	
	/**
	 * 根据字符串返回相对应的天气现象图标
	 * @param index
	 * @return 晚上的天气图标
	 */
	public static int getNightPhenomenaPicture(String index){
		for (WeatherPhenomena wp : WeatherPhenomena.values()) {
			if (wp.getIndex().equals(index)) {
				return wp.getNightImageResource();
			}
		}
		return 0;
	}

}
