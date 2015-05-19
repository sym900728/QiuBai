package com.qiubai.entity;

public class Weather {
	private String dayWeatherPhenomena;// 白天天气现象
	private String nightWeatherPhenomena;// 晚上天气现象
	private String dayTemperature;// 白天温度
	private String nightTemperature;// 晚上温度
	private String dayWind;// 白天分向
	private String nightWind;// 晚上分向
	private String dayWindPower;// 白天分力
	private String nightWindPower;// 晚上分力

	private int dayphenIcon;// 白天空气图标
	private int nightphenIcon;// 晚上空气图标

	public Weather() {
		super();
	}

	public Weather(String dayWeatherPhenomena, String nightWeatherPhenomena,
			String dayTemperature, String nightTemperature, String dayWind,
			String nightWind, String dayWindPower, String nightWindPower,
			int dayphenIcon, int nightphenIcon) {
		super();
		this.dayWeatherPhenomena = dayWeatherPhenomena;
		this.nightWeatherPhenomena = nightWeatherPhenomena;
		this.dayTemperature = dayTemperature;
		this.nightTemperature = nightTemperature;
		this.dayWind = dayWind;
		this.nightWind = nightWind;
		this.dayWindPower = dayWindPower;
		this.nightWindPower = nightWindPower;
		this.dayphenIcon = dayphenIcon;
		this.nightphenIcon = nightphenIcon;
	}

	public String getDayWeatherPhenomena() {
		return dayWeatherPhenomena;
	}

	public void setDayWeatherPhenomena(String dayWeatherPhenomena) {
		this.dayWeatherPhenomena = dayWeatherPhenomena;
	}

	public String getNightWeatherPhenomena() {
		return nightWeatherPhenomena;
	}

	public void setNightWeatherPhenomena(String nightWeatherPhenomena) {
		this.nightWeatherPhenomena = nightWeatherPhenomena;
	}

	public String getDayTemperature() {
		return dayTemperature;
	}

	public void setDayTemperature(String dayTemperature) {
		this.dayTemperature = dayTemperature;
	}

	public String getNightTemperature() {
		return nightTemperature;
	}

	public void setNightTemperature(String nightTemperature) {
		this.nightTemperature = nightTemperature;
	}

	public String getDayWind() {
		return dayWind;
	}

	public void setDayWind(String dayWind) {
		this.dayWind = dayWind;
	}

	public String getNightWind() {
		return nightWind;
	}

	public void setNightWind(String nightWind) {
		this.nightWind = nightWind;
	}

	public String getDayWindPower() {
		return dayWindPower;
	}

	public void setDayWindPower(String dayWindPower) {
		this.dayWindPower = dayWindPower;
	}

	public String getNightWindPower() {
		return nightWindPower;
	}

	public void setNightWindPower(String nightWindPower) {
		this.nightWindPower = nightWindPower;
	}

	public int getDayphenIcon() {
		return dayphenIcon;
	}

	public void setDayphenIcon(int dayphenIcon) {
		this.dayphenIcon = dayphenIcon;
	}

	public int getNightphenIcon() {
		return nightphenIcon;
	}

	public void setNightphenIcon(int nightphenIcon) {
		this.nightphenIcon = nightphenIcon;
	}

}
