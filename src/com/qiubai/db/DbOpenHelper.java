package com.qiubai.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

	private static String name = "qiubai.db";// 表示数据库的名称
	private static int version = 1;// 表示数据的的版本号
	
	
	public DbOpenHelper(Context context) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table tb_picture(id integer primary key autoincrement, belong varchar(255),"
				+ "title varchar(255), image1 varchar(255), image2 varchar(255), image3 varchar(255),"
				+ "time varchar(255), comments integer, counts integer)");
		db.execSQL("create table tb_joke(id integer primary key autoincrement, belong varchar(255),"
				+ "title varchar(255), description varchar(255), content text, time varchar(255),"
				+ "zan integer, comments integer)");
		db.execSQL("create table tb_novel(id integer primary key autoincrement, belong varchar(255)"
				+ ", image varchar(255), title varchar(255), description varchar(255), content text"
				+ ", time varchar(255), comments integer)");
		db.execSQL("create table tb_video(id integer primary key autoincrement, belong varchar(255),"
				+ "title varchar(255), image varchar(255), video varchar(255), time varchar(255),"
				+ "comments integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
