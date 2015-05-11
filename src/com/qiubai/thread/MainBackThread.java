package com.qiubai.thread;

import android.os.Handler;
import android.os.Message;

public class MainBackThread implements Runnable{
	
	private int mBackKeyPressedTimes = 0;
	private Handler mHandler;
	public MainBackThread(int mBackKeyPressedTimes,Handler mHandler){
		this.mHandler = mHandler;
		this.mBackKeyPressedTimes = mBackKeyPressedTimes;
	}
	
	@Override
	public void run() {
		
		try {
			Thread.sleep(2000);
			System.out.println(mBackKeyPressedTimes);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			mBackKeyPressedTimes=0;
			Message msg = new Message();
			msg.what = mBackKeyPressedTimes;
			mHandler.sendEmptyMessage(msg.what);
			System.out.println("finally:   "+mBackKeyPressedTimes);
		}
	}
}
