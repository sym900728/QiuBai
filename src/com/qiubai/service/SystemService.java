package com.qiubai.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SystemService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
