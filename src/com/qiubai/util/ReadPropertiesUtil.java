package com.qiubai.util;

import java.util.ResourceBundle;

public class ReadPropertiesUtil {
	
	public static String read(String sourceName, String key) {
		try {
			return ResourceBundle.getBundle(
					"com.qiubai.properties." + sourceName).getString(key);
		} catch (Exception e) {
			return null;
		}
	}
}
