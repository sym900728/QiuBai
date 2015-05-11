package com.qiubai.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.qiubai.dao.CityDao;
import com.qiubai.dao.impl.CityDaoImpl;
import com.qiubai.entity.City;

public class CityService {
	/**
	 * 返回city_table表中的城市列表
	 * 
	 * @return List<City>
	 */
	public List<City> getAllCity(Context context) {
		CityDao cityDao = new CityDaoImpl(context);
		List<City> list = cityDao.getAllCity();
		return list;

	}

}
