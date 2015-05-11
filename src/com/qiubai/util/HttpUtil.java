package com.qiubai.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	/**
	 * post request method
	 * @param map
	 * @param url
	 * @return
	 */
	public static String doPost(Map<String, String> map, String url) {
		
		String result = "error";
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			parameters.add(new BasicNameValuePair(key, map.get(key)));
		}

		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
		HttpPost post = new HttpPost(url);

		try {
			HttpEntity entity = new UrlEncodedFormEntity(parameters, "utf-8");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(),"utf-8");
				if("null".equals(result)){
					result = "nocontent";
				}
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT){
				result = "nocontent";
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		
	}
	
	/**
	 * get request method
	 * @param url
	 * @return
	 */
	public static String doGet(String url){
		String result = "error";

		try {
			HttpGet request = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT){
				result = "nocontent";
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
	}
	
}
