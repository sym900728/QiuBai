package com.qiubai.service;

import android.content.Context;

import com.qiubai.dao.WeatherDao;
import com.qiubai.dao.impl.WeatherDaoImpl;

public class WeatherService {

	/**
	 * 通过城市名称找到该城市所对应的城市编号
	 * @param city
	 * @param context
	 * @return
	 */
	public String getCityByName(String city,Context context){
		WeatherDao weather = new WeatherDaoImpl(context);
		String cityCode = weather.getCityIdByCityName(city);
		return cityCode;
		
	}
}
