package com.qiubai.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qiubai.entity.User;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.util.StringUtil;

public class UserService {
	
	private String protocol;
	private String ip;
	private String port;
	
	public UserService(){
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
		
	}
	
	/**
	 * check user login via userid 
	 * @return true: user login (userid existed); false: user doesn't login (userid didn't exist)
	 */
	public boolean checkUserLogin(Context context){
		SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context);
		if("".equals(spUtil.getUserid()) || spUtil.getUserid() == null ){
			return false;
		} else {
			return true;
		}
	}
	
	public String login(String userid, String password){
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid", userid);
		params.put("password", password);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "login"));
	}
	
	public User parseLoginJson(String json){
		User user = new User();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			user.setUserid(jsonObject.getString("userid"));
			user.setNickname(jsonObject.getString("nickname"));
			user.setToken(jsonObject.getString("token"));
			user.setIcon(jsonObject.getString("icon"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public String register(String email, String nickname, String password){
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("nickname", nickname);
		params.put("password", password);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "register"));
	}
	
	public String forgetPassword(String email){
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid", email);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "forgetPassword"));
	}
	
	public boolean logout(Context context){
		SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context);
		File file = new File(ReadPropertiesUtil.read("config", "header_icon_path"));
		if(spUtil.removeToken() && spUtil.removeUserid()){
			if(file != null){
				file.delete();
			}
			return true;
		} else {
			return false;
		}
	}
	
	public String changeNickname(String userid, String token, String nickname){
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid", userid);
		params.put("nickname", nickname);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "changeNickname") + token);
	}
	
	public String changePassword(String userid, String token, String originPassword, String newPassword){
		Map<String, String> params = new HashMap<String, String>();
		params.put("userid", userid);
		params.put("originPassword", originPassword);
		params.put("newPassword", newPassword);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "changePassword") + token);
	}
	
	//public String collect(String userid, String token, )
	
	public String uploadIcon(File file, String token, String userid){
		String result = "error";
		String boundary = UUID.randomUUID().toString();
		String content_type = "multipart/form-data";
		String prefix = "--";
		String end = "\r\n";
		
		try {
			URL url = new URL(protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "uploadIcon") + token + "/" + userid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "utf-8");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", content_type + ";boundary="+ boundary);
			if(file != null){
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb1 = new StringBuffer();
				sb1.append(prefix + boundary + end);
				sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + end);
	            sb1.append("Content-Type: application/octet-stream; charset=utf-8" + end);
	            sb1.append(end);
	            dos.writeBytes(sb1.toString());
	            FileInputStream fis = new FileInputStream(file);
	            byte[] bytes = new byte[1024];
	            int len = 0;
	            while ((len = fis.read(bytes)) != -1) {
	                dos.write(bytes, 0, len);
	            }
	            fis.close();
	            dos.writeBytes((end + prefix + boundary + prefix + end).toString());
	            dos.flush();
	           
	            if (conn.getResponseCode() == HttpStatus.SC_OK) {
	            	
	                InputStream input = conn.getInputStream();
	                StringBuffer sb2 = new StringBuffer();
	                int ss;
	                while ((ss = input.read()) != -1) {
	                    sb2.append((char) ss);
	                }
	                result = sb2.toString();
	            } else {
	            }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Bitmap getHeaderIcon(String uri){
		Bitmap bitmap = null;
		try {
			HttpGet get = new HttpGet(StringUtil.changeBackslashToSlash(uri)); 
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				bitmap = BitmapFactory.decodeStream(is);
			}			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public void storeImage(Bitmap bitmap){
		try {
			File filepath = new File(ReadPropertiesUtil.read("config", "userinfo_path"));
			if (!filepath.exists()) {
				filepath.mkdirs();
			}
			FileOutputStream fileOS = new FileOutputStream(filepath + "/" + "header_icon.png");
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
			fileOS.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
