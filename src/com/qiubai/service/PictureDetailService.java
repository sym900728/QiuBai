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

import com.qiubai.entity.PictureDetail;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.PropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PictureDetailService {

	private String protocol;
	private String ip;
	private String port;
	private PropertiesUtil propUtil;
	private SharedPreferencesUtil spUtil;

	public PictureDetailService(Context context) {
		propUtil = new PropertiesUtil(context);
		spUtil = new SharedPreferencesUtil(context);
		protocol = spUtil.getProtocol();
		ip = spUtil.getIp();
		port = spUtil.getPort();
	}
	
	public String getPictureDetails(String pictureid){
		Map<String, String> params = new HashMap<String, String>();
		params.put("pictureid", pictureid);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + propUtil.readProperties("link.properties", "getPictureDetails") );
	}
	
	public List<PictureDetail> parsePictureDetailsJson(String json){
		//System.out.println(json);
		List<PictureDetail> list = new ArrayList<PictureDetail>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("pictureDetail");
			
			for(int i = 0; i < jsonArray.length(); i ++){
				PictureDetail pictureDetail = new PictureDetail();
				JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
				pictureDetail.setId(jsonObject2.getInt("id"));
				pictureDetail.setPictureid(jsonObject2.getInt("pictureid"));
				pictureDetail.setContent(jsonObject2.getString("content"));
				pictureDetail.setImage(jsonObject2.getString("image"));
				list.add(pictureDetail);
			}
		} catch (JSONException e) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				JSONObject jsonObject2 = jsonObject.getJSONObject("pictureDetail");
				PictureDetail pictureDetail = new PictureDetail();
				pictureDetail.setId(jsonObject2.getInt("id"));
				pictureDetail.setPictureid(jsonObject2.getInt("pictureid"));
				pictureDetail.setContent(jsonObject2.getString("content"));
				pictureDetail.setImage(jsonObject2.getString("image"));
				list.add(pictureDetail);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return list;
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
	
	public void storeImage(Bitmap bitmap, String path, String filename){
		try {
			File filepath = new File(propUtil.readProperties("config.properties", "picturedetails_picture_path") + "/" + path );
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
