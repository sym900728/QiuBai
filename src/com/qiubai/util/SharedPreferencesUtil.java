package com.qiubai.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("CommitPrefEdits")
public class SharedPreferencesUtil {

	private SharedPreferences sharedPreferences;
	private Context context;
	private static final String QIUBAIXML = "qiubai";
	private static final String JOKEXML = "joke";
	public SharedPreferencesUtil(Context context){
		this.context = context;
	}
	
	public boolean storeRefreshTime(String key, long value){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		return editor.commit();
	}
	
	public long getRefreshTime(String key){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, 0);
	}
	
	public boolean isFirstRun(String flag){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String str = sharedPreferences.getString(flag, null);
		if(str == null){
			storefirstRun(flag);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean storefirstRun(String flag){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(flag, "no");
		return editor.commit();
	}
	
	public String getProtocol(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String protocol = sharedPreferences.getString("protocol", null);
		if(protocol == null || "".equals(protocol)){
			return "http://";
		}
		return protocol;
	}
	
	/**
	 * store ip
	 * @param ip
	 * @return
	 */
	public boolean storeIp(String ip){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("ip", ip);
		return editor.commit();
	}
	
	/**
	 * get ip
	 * @return
	 */
	public String getIp(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String ip = sharedPreferences.getString("ip", null);
		if(ip == null || "".equals(ip)){
			return "192.168.31.129";
		}
		return ip;
	}
	
	/**
	 * store port
	 * @param port
	 * @return
	 */
	public boolean storePort(String port){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("port", port);
		return editor.commit();
	}
	
	/**
	 * get port
	 * @return
	 */
	public String getPort(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String port = sharedPreferences.getString("port", null);
		if(port == null || "".equals(port)){
			return "8080";
		}
		return port;
	}
	
	/**
	 * store token
	 * @param token
	 * @return true: success; false: fail
	 */
	public boolean storeToken(String token) {
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("token", token);
		return editor.commit();
	}
	
	/**
	 * get token
	 * @return token
	 */
	public String getToken(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String token = sharedPreferences.getString("token", null);
		return token;
	}
	
	/**
	 * remove token
	 * @return true: success; false: fail
	 */
	public boolean removeToken(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove("token");
		return editor.commit();
	}
	
	/**
	 * get font
	 * @return font
	 */
	public String getFont(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String font = sharedPreferences.getString("font", null);
		return font;
	}
	
	/**
	 * store font
	 * @param font
	 * @return true: success; false: fail
	 */
	public boolean storeFont(String font){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("font", font);
		return editor.commit();
	}
	
	/**
	 * get userid
	 * @return userid
	 */
	public String getUserid(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String userid = sharedPreferences.getString("userid", null);
		return userid;
	}
	
	/**
	 * store userid
	 * @param userid
	 * @return true: success; false: fail
	 */
	public boolean storeUserid(String userid){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("userid", userid);
		return editor.commit();
	}
	
	/**
	 * remove userid
	 * @return true: success; false: fail
	 */
	public boolean removeUserid(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove("userid");
		return editor.commit();
	}
	
	/**
	 * get nickname
	 * @return nickname
	 */
	public String getNickname(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String nickname = sharedPreferences.getString("nickname", null);
		return nickname;
	}
	
	/**
	 * store nickname
	 * @param nickname
	 * @return true: success; false: fail
	 */
	public boolean storeNickname(String nickname){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("nickname", nickname);
		return editor.commit();
	}
	
	/**
	 * get icon
	 * @return icon
	 */
	public String getIcon(){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		String icon = sharedPreferences.getString("icon", null);
		return icon;
	}
	
	/**
	 * store icon
	 * @param icon
	 * @return true: success; false: fail
	 */
	public boolean storeIcon(String icon){
		sharedPreferences = context.getSharedPreferences(QIUBAIXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("icon", icon);
		return editor.commit();
	}
	
	/**
	 * store joke zan: key => id, default no zan
	 * @param id
	 * @param flag
	 * @return
	 */
	public boolean storeJokeZan(String id, String flag){
		sharedPreferences = context.getSharedPreferences(JOKEXML, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(id, flag);
		return editor.commit();
	}
	
	/**
	 * get joke zan
	 * @param id
	 * @return flag  true: zan; false: no zan
	 */
	public String getJokeZan(String id){
		sharedPreferences = context.getSharedPreferences(JOKEXML, Context.MODE_PRIVATE);
		String flag = sharedPreferences.getString(id, null);
		return flag;
	}
}
