package com.qiubai.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qiubai.dao.CityDao;
import com.qiubai.db.DbOpenHelper;
import com.qiubai.entity.City;

public class CityDaoImpl implements CityDao {

	private DbOpenHelper dbHelper = null;

	public CityDaoImpl(Context context) {
		dbHelper = new DbOpenHelper(context);
	}

	@Override
	public List<City> getAllCity() {
		List<City> list = new ArrayList<City>();
		City cityName;
		SQLiteDatabase database = null;
		try {
			String sql = "select * from city";

			database = dbHelper.getReadableDatabase();
			Cursor cursor = database.rawQuery(sql, null);

			while (cursor.moveToNext()) {
				cityName = new City();
				cityName.setProvince(cursor.getString(6));
				cityName.setTown(cursor.getString(4));
				cityName.setDistricten(cursor.getString(3));
				cityName.setProven(cursor.getString(5));
				list.add(cityName);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return list;
	}

}
