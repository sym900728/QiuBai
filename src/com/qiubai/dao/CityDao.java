package com.qiubai.dao;

import java.util.List;

import com.qiubai.entity.City;

public interface CityDao {

	/**
	 * 取得town_table表中的所有城市的列表
	 * 
	 * @return List<CityName>
	 */
	public List<City> getAllCity();
}
