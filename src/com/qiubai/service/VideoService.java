package com.qiubai.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qiubai.entity.Video;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.PropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class VideoService {

	private String protocol;
	private String ip;
	private String port;
	private PropertiesUtil propUtil;
	private SharedPreferencesUtil spUtil;
	
	public VideoService(Context context){
		propUtil = new PropertiesUtil(context);
		spUtil = new SharedPreferencesUtil(context);
		protocol = spUtil.getProtocol();
		ip = spUtil.getIp();
		port = spUtil.getPort();
		
	}
	
	/**
	 * @param offset
	 * @param length
	 * @return
	 */
	public String getVideos(String offset, String length){
		Map<String, String> params = new HashMap<String, String>();
		params.put("offset", offset);
		params.put("length", length);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + propUtil.readProperties("link.properties", "getVideos"));
	}
	
	public List<Video> parseVideosJson(String json){
		List<Video> list = new ArrayList<Video>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("video");
			
			for(int i = 0; i < jsonArray.length(); i ++){
				Video video = new Video();
				JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
				video.setId(jsonObject2.getInt("id"));
				video.setBelong(jsonObject2.getString("belong"));
				video.setTitle(jsonObject2.getString("title"));
				video.setImage(jsonObject2.getString("image"));
				video.setVideo(jsonObject2.getString("video"));
				video.setTime(jsonObject2.getString("time"));
				video.setComments(jsonObject2.getInt("comments"));
				list.add(video);
			}
		} catch (JSONException e) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				JSONObject jsonObject2 = jsonObject.getJSONObject("video");
				Video video = new Video();
				video.setId(jsonObject2.getInt("id"));
				video.setBelong(jsonObject2.getString("belong"));
				video.setTitle(jsonObject2.getString("title"));
				video.setImage(jsonObject2.getString("image"));
				video.setVideo(jsonObject2.getString("video"));
				video.setTime(jsonObject2.getString("time"));
				video.setComments(jsonObject2.getInt("comments"));
				list.add(video);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return list;
	}
	
	public String getVideoComments(String id){
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + propUtil.readProperties("link.properties", "getVideoComments"));
	}
	
	public Bitmap getImage(String uri){
		Bitmap bitmap = null;
		try {
			HttpGet get = new HttpGet(protocol + ip + ":" + port + StringUtil.changeBackslashToSlash(uri));
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
	
	public String getVideo(String uri){
		return protocol + ip + ":" + port + uri;
	}
	
	/**
	 * store image
	 * @param bitmap
	 * @param filename
	 */
	public void storeImage(Bitmap bitmap, String filename){
		try {
			File filepath = new File(propUtil.readProperties("config.properties", "videos_picture_path"));
			if (!filepath.exists()) {
				filepath.mkdirs();
			}
			FileOutputStream fileOS = new FileOutputStream(filepath + "/" + filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
			fileOS.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
