package com.qiubai.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.dao.JokeDao;
import com.qiubai.db.DbOpenHelper;
import com.qiubai.entity.Joke;
import com.qiubai.util.PropertiesUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class JokeDaoImpl implements JokeDao{

	private PropertiesUtil propUtil;
	private SQLiteDatabase database;
	
	public JokeDaoImpl(Context context){
		DbOpenHelper helper = new DbOpenHelper(context);
		propUtil = new PropertiesUtil(context);
		database = helper.getWritableDatabase();
	}
	
	@Override
	public List<Joke> getJokes(String[] selectionArgs) {
		List<Joke> list = new ArrayList<Joke>();
		String sql = "select * from tb_joke order by time desc";
		try {
			Cursor cursor = database.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Joke joke = new Joke();
				joke.setId(cursor.getInt(cursor.getColumnIndex("id")));
				joke.setBelong(cursor.getString(cursor.getColumnIndex("belong")));
				joke.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				joke.setDescription(cursor.getString(cursor.getColumnIndex("description")));
				joke.setContent(cursor.getString(cursor.getColumnIndex("content")));
				joke.setTime(cursor.getString(cursor.getColumnIndex("time")));
				joke.setZan(cursor.getInt(cursor.getColumnIndex("zan")));
				joke.setComments(cursor.getInt(cursor.getColumnIndex("comments")));
				list.add(joke);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return list;
	}

	@Override
	public void addJokes(List<Joke> list) {
		database.beginTransaction();
		try {
			for(Joke joke: list){
				database.execSQL("insert into tb_joke(id, belong, title, description, content, "
						+ "time, zan, comments) values(?,?,?,?,?,?,?,?)" , new Object[]{
						joke.getId(), joke.getBelong(), joke.getTitle(), joke.getDescription(),
						joke.getContent(), joke.getTime(), joke.getZan(), joke.getComments()});
			}
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
		}
	}

	@Override
	public void emptyJokeTable() {
		database.execSQL("delete from tb_joke");
	}

	
}
