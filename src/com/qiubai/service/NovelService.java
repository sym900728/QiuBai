package com.qiubai.service;

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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qiubai.entity.Novel;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;
import com.qiubai.util.StringUtil;

public class NovelService {
	
	private String protocol;
	private String ip;
	private String port;
	
	public NovelService(){
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
	}
	
	public String getNovels(String offset, String length){
		Map<String, String> params = new HashMap<String, String>();
		params.put("offset", offset);
		params.put("length", length);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "getNovels"));
	}
	
	public List<Novel> parseNovelsJson(String json){
		List<Novel> list = new ArrayList<Novel>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("novel");
			
			for(int i = 0; i < jsonArray.length(); i ++){
				Novel novel = new Novel();
				JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
				novel.setBelong(jsonObject2.getString("belong"));
				novel.setId(jsonObject2.getInt("id"));
				novel.setComments(jsonObject2.getInt("comments"));
				novel.setDescription(jsonObject2.getString("description"));
				novel.setContent(jsonObject2.getString("content"));
				novel.setImage(jsonObject2.getString("image"));
				novel.setTime(jsonObject2.getString("time"));
				novel.setTitle(jsonObject2.getString("title"));
				list.add(novel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public Bitmap getImage(String uri){
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
}
