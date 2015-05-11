package com.qiubai.entity;

public enum WeatherWindPower {
	power0("微风", "0"), 
	power1("3-4级", "1"), 
	power2("4-5级", "2"), 
	power3("5-6级","3"), 
	power4("6-7级", "4"), 
	power5("7-8级", "5"), 
	power6("8-9级", "6"), 
	power7("9-10级", "7"), 
	power8("10-11级", "8"), 
	power9("11-12级", "9");

	private String name;
	private String index;

	private WeatherWindPower(String name, String index) {
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
	 * 返回分力的中文名称
	 * 
	 * @param index
	 * @return 分力的中文名称
	 */
	public static String getWindPowerName(String index) {

		for (WeatherWindPower wwp : WeatherWindPower.values()) {
			if (wwp.getIndex().equals(index)) {
				return wwp.getName();
			}
		}

		return null;

	}

}
