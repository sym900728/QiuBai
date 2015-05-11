package com.qiubai.entity;

public enum WeatherWind {
	NoWind("无持续分向", "0"), 
	Northeast("东北风", "1"), 
	East("东风", "2"), 
	Southeast("东南风", "3"), 
	South("南风", "4"), 
	Southwest("西南风", "5"), 
	West("西风","6"), 
	NorthWest("西北风", "7"), 
	North("北风", "8"), 
	WhirlWind("旋转风", "9");

	private String name;
	private String index;

	private WeatherWind(String name, String index) {
		this.name = name;
		this.index = index;
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

	/**
	 * 返回分向的中文名称
	 * 
	 * @param index
	 * @return
	 */
	public static String getWindName(String index) {

		for (WeatherWind weatherWind : WeatherWind.values()) {
			if (weatherWind.getIndex().equals(index)) {
				return weatherWind.getName();
			}
		}
		return index;

	}

}
