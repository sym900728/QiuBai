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
import com.qiubai.util.ReadPropertiesUtil;

public class JokeService {
	
	private String protocol;
	private String ip;
	private String port;
	
	public JokeService(){
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
		
	}
	
	public String getJokes(String offset, String length){
		Map<String, String> params = new HashMap<String, String>();
		params.put("offset", offset);
		params.put("length", length);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "getJokes"));
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
				joke.setContent(jsonObject2.getString("content"));
				joke.setTime(jsonObject2.getString("time"));
				joke.setZan(jsonObject2.getInt("zan"));
				list.add(joke);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
