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

import com.qiubai.entity.Picture;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;
import com.qiubai.util.StringUtil;

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
	
	public String getPictureDetails(String pictureid){
		Map<String, String> params = new HashMap<String, String>();
		params.put("pictureid", pictureid);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "getPictureDetails") );
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
