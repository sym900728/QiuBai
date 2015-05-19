package com.qiubai.service;

import java.util.HashMap;
import java.util.Map;

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
}
