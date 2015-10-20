package com.qiubai.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qiubai.entity.Joke;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.PropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;

import android.content.Context;

public class JokeService {
	
	private String protocol;
	private String ip;
	private String port;
	private PropertiesUtil propUtil;
	private SharedPreferencesUtil spUtil;
	
	public JokeService(Context context){
		propUtil = new PropertiesUtil(context);
		spUtil = new SharedPreferencesUtil(context);
		protocol = spUtil.getProtocol();
		ip = spUtil.getIp();
		port = spUtil.getPort();
	}
	
	public String getJokes(String offset, String length){
		Map<String, String> params = new HashMap<String, String>();
		params.put("offset", offset);
		params.put("length", length);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + propUtil.readProperties("link.properties", "getJokes"));
	}
	
	public List<Joke> parseJokesJson(String json){
		List<Joke> list = new ArrayList<Joke>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("joke");
			
			for(int i = 0; i < jsonArray.length(); i ++){
				Joke joke = new Joke();
				JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
				joke.setBelong(jsonObject2.getString("belong"));
				joke.setId(jsonObject2.getInt("id"));
				joke.setComments(jsonObject2.getInt("comments"));
				joke.setTitle(jsonObject2.getString("title"));
				joke.setDescription(jsonObject2.getString("description"));
				joke.setContent(jsonObject2.getString("content"));
				joke.setTime(jsonObject2.getString("time"));
				joke.setZan(jsonObject2.getInt("zan"));
				list.add(joke);
			}
		} catch (JSONException e) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				JSONObject jsonObject2 = jsonObject.getJSONObject("joke");
				Joke joke = new Joke();
				joke.setBelong(jsonObject2.getString("belong"));
				joke.setId(jsonObject2.getInt("id"));
				joke.setComments(jsonObject2.getInt("comments"));
				joke.setTitle(jsonObject2.getString("title"));
				joke.setDescription(jsonObject2.getString("description"));
				joke.setContent(jsonObject2.getString("content"));
				joke.setTime(jsonObject2.getString("time"));
				joke.setZan(jsonObject2.getInt("zan"));
				list.add(joke);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} 
		return list;
	}
	
	public String setZan(String id, String flag){
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("flag", flag);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + propUtil.readProperties("link.properties", "setZan"));
	}
	
	public String getJokeComments(String id){
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + propUtil.readProperties("link.properties", "getJokeComments"));
	}
	
}
