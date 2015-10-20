package com.qiubai.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.dao.VideoDao;
import com.qiubai.db.DbOpenHelper;
import com.qiubai.entity.Picture;
import com.qiubai.entity.Video;
import com.qiubai.util.PropertiesUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class VideoDaoImpl implements VideoDao{
	
	private PropertiesUtil propUtil;
	private SQLiteDatabase database;

	public VideoDaoImpl(Context context) {
		DbOpenHelper helper = new DbOpenHelper(context);
		propUtil = new PropertiesUtil(context);
		database = helper.getWritableDatabase();
	}

	@Override
	public List<Video> getVideos(String[] selectionArgs) {
		List<Video> list = new ArrayList<Video>();
		String sql = "select * from tb_video order by time desc";
		try {
			Cursor cursor = database.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Video video = new Video();
				video.setId(cursor.getInt(cursor.getColumnIndex("id")));
				video.setBelong(cursor.getString(cursor.getColumnIndex("belong")));
				video.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				video.setImage(cursor.getString(cursor.getColumnIndex("image")));
				video.setVideo(cursor.getString(cursor.getColumnIndex("video")));
				video.setTime(cursor.getString(cursor.getColumnIndex("time")));
				video.setComments(cursor.getInt(cursor.getColumnIndex("comments")));
				list.add(video);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return list;
	}

	@Override
	public void addVideos(List<Video> list) {
		database.beginTransaction();
		try {
			for(Video video: list){
				database.execSQL("insert into tb_video(id, belong, title, image, video,"
						+ "time, comments) values(?,?,?,?,?,?,?)" , new Object[]{
						video.getId(), video.getBelong(), video.getTitle(), video.getImage(),
						video.getVideo(), video.getTime(), video.getComments()});
			}
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
		}
	}

	@Override
	public void emptyVideoTable() {
		database.execSQL("delete from tb_video");
	}

}
