package com.qiubai.dao.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qiubai.dao.WeatherDao;
import com.qiubai.db.DbOpenHelper;

public class WeatherDaoImpl implements WeatherDao {
	private DbOpenHelper dbHelper = null;

	public WeatherDaoImpl(Context context) {
		dbHelper = new DbOpenHelper(context);
	}

	@Override
	public String getCityIdByCityName(String city) {

		String cityName = null;
		SQLiteDatabase database = null;
		try {
			String sql = "select * from city where districtcn =" + "'" + city
					+ "'";

			database = dbHelper.getReadableDatabase();

			Cursor cursor = database.rawQuery(sql, null);

			if (cursor != null) {
				cursor.moveToFirst();
				cityName = cursor.getString(cursor.getColumnIndex("areaid"));

			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return cityName;
	}

}
