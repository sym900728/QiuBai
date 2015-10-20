package com.qiubai.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.dao.PictureDao;
import com.qiubai.db.DbOpenHelper;
import com.qiubai.entity.Picture;
import com.qiubai.util.PropertiesUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PictureDaoImpl implements PictureDao{

	private PropertiesUtil propUtil;
	private SQLiteDatabase database;

	public PictureDaoImpl(Context context) {
		DbOpenHelper helper = new DbOpenHelper(context);
		propUtil = new PropertiesUtil(context);
		database = helper.getWritableDatabase();
	}
	
	@Override
	public void addPictures(List<Picture> list) {
		database.beginTransaction();
		try {
			for(Picture picture: list){
				database.execSQL("insert into tb_picture(id, belong, title, image1, image2, image3, "
						+ "time, comments, counts) values(?,?,?,?,?,?,?,?,?)" , new Object[]{
						picture.getId(), picture.getBelong(), picture.getTitle(), picture.getImage1(),
						picture.getImage2(), picture.getImage3(), picture.getTime(), 
						picture.getComments(), picture.getCounts()});
			}
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
		}
	}
	
	@Override
	public void emptyPictureTable() {
		database.execSQL("delete from tb_picture");
	}
	
	@Override
	public List<Picture> getPictures(String[] selectionArgs) {
		List<Picture> list = new ArrayList<Picture>();
		String sql = "select * from tb_picture order by time desc";
		try {
			Cursor cursor = database.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Picture picture = new Picture();
				picture.setId(cursor.getInt(cursor.getColumnIndex("id")));
				picture.setBelong(cursor.getString(cursor.getColumnIndex("belong")));
				picture.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				picture.setImage1(cursor.getString(cursor.getColumnIndex("image1")));
				picture.setImage2(cursor.getString(cursor.getColumnIndex("image2")));
				picture.setImage3(cursor.getString(cursor.getColumnIndex("image3")));
				picture.setTime(cursor.getString(cursor.getColumnIndex("time")));
				picture.setComments(cursor.getInt(cursor.getColumnIndex("comments")));
				picture.setCounts(cursor.getInt(cursor.getColumnIndex("counts")));
				list.add(picture);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return list;
	}



}
