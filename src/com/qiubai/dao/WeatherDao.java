package com.qiubai.dao;

public interface WeatherDao {
	/**
	 * 通过城市名称找到该城市的所有信息
	 * @param city
	 * @return
	 */
	public String getCityIdByCityName(String city);
}
