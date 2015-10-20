package com.qiubai.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.dao.NovelDao;
import com.qiubai.db.DbOpenHelper;
import com.qiubai.entity.Joke;
import com.qiubai.entity.Novel;
import com.qiubai.util.PropertiesUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NovelDaoImpl implements NovelDao {

	private PropertiesUtil propUtil;
	private SQLiteDatabase database;

	public NovelDaoImpl(Context context) {
		DbOpenHelper helper = new DbOpenHelper(context);
		propUtil = new PropertiesUtil(context);
		database = helper.getWritableDatabase();
	}

	@Override
	public List<Novel> getNovels(String[] selectionArgs) {
		List<Novel> list = new ArrayList<Novel>();
		String sql = "select * from tb_novel order by time desc";
		try {
			Cursor cursor = database.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Novel novel = new Novel();
				novel.setId(cursor.getInt(cursor.getColumnIndex("id")));
				novel.setBelong(cursor.getString(cursor.getColumnIndex("belong")));
				novel.setImage(cursor.getString(cursor.getColumnIndex("image")));
				novel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				novel.setDescription(cursor.getString(cursor.getColumnIndex("description")));
				novel.setContent(cursor.getString(cursor.getColumnIndex("content")));
				novel.setTime(cursor.getString(cursor.getColumnIndex("time")));
				novel.setComments(cursor.getInt(cursor.getColumnIndex("comments")));
				list.add(novel);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return list;
	}

	@Override
	public void addNovels(List<Novel> list) {
		database.beginTransaction();
		try {
			for(Novel novel: list){
				database.execSQL("insert into tb_novel(id, belong, image, title, description, content, "
						+ "time, comments) values(?,?,?,?,?,?,?,?)" , new Object[]{
						novel.getId(), novel.getBelong(), novel.getImage(), novel.getTitle(),
						novel.getDescription() ,novel.getContent(), novel.getTime(), novel.getComments()});
			}
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
		}
	}

	@Override
	public void emptyNovelTable() {
		database.execSQL("delete from tb_novel");
	}
}
