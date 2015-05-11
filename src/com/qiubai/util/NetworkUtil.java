package com.qiubai.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	/**
	 * 判断手机是否连接网络
	 * @param context
	 * @return true: 连接网络了  false: 没有连接网络
	 */
	public static boolean isConnectInternet(Context context) {
		boolean netSataus = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) { 		//注意，这个判断一定要的哦，要不然会出错
		    netSataus = networkInfo.isAvailable();
		}
		return netSataus;		
	}
}
