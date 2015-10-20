package com.qiubai.util;

import java.io.IOException;
import java.util.Properties;

import android.content.Context;

public class PropertiesUtil {
	
	private Context context;
	
	public PropertiesUtil(Context context){
		this.context = context;
	}
	
	public String readProperties(String fileName, String key) {
		Properties properties = new Properties();
		try {
			properties.load(context.getAssets().open(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.getProperty(key);
	}
	
}
