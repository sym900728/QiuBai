package com.qiubai.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bt.qiubai.R;

import android.content.Context;
import android.os.Environment;

public class DBManager {

	private final int BUFFER_SIZE = 400000;
	public static final String PACKAGE_NAME = "com.bt.qiubai";
	public static final String DB_NAME = "qiubai.db";
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME + "/databases";

	private Context context;

	public DBManager(Context context) {
		this.context = context;
	}


	/**
	 * 把raw中的数据库复制到database
	 */
	public void copyDatabase() {
		File file = new File(DB_PATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
		String dbfile = DB_PATH + "/" + DB_NAME;

		try {
			if (new File(dbfile).length() == 0) {
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				
				readDB(fos, buffer, R.raw.city);
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readDB(FileOutputStream fos,byte[] buffer,int db_id){
		int count;
		InputStream is;
		is = this.context.getResources().openRawResource(db_id);
		
		try {
			while((count=is.read(buffer))>0){
				fos.write(buffer, 0, count);
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
