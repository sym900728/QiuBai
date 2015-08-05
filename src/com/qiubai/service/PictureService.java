package com.qiubai.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qiubai.entity.Picture;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;

public class PictureService {

	private String protocol;
	private String ip;
	private String port;
	
	public PictureService() {
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
	}
	
	/**
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	public String getPictures(String offset, String length){
		Map<String, String> params = new HashMap<String, String>();
		params.put("offset", offset);
		params.put("length", length);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "getPictures"));
	}
	
	public List<Picture> parsePicturesJson(String json){
		List<Picture> list = new ArrayList<Picture>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("picture");
			
			for(int i = 0; i < jsonArray.length(); i ++){
				Picture picture = new Picture();
				JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
				picture.setId(jsonObject2.getInt("id"));
				picture.setBelong(jsonObject2.getString("belong"));
				picture.setTitle(jsonObject2.getString("title"));
				picture.setImage(jsonObject2.getString("image"));
				picture.setTime(jsonObject2.getString("time"));
				picture.setComments(jsonObject2.getInt("comments"));
				list.add(picture);
			}
		} catch (JSONException e) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				JSONObject jsonObject2 = jsonObject.getJSONObject("picture");
				Picture picture = new Picture();
				picture.setId(jsonObject2.getInt("id"));
				picture.setBelong(jsonObject2.getString("belong"));
				picture.setTitle(jsonObject2.getString("title"));
				picture.setImage(jsonObject2.getString("image"));
				picture.setTime(jsonObject2.getString("time"));
				picture.setComments(jsonObject2.getInt("comments"));
				list.add(picture);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return list;
	}
	
}
