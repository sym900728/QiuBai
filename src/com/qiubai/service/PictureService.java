package com.qiubai.service;

import java.util.Map;

import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;

public class PictureService {

	private String protocol;
	private String ip;
	private String port;
	
	public PictureService() {
		super();
	}
	
	/**
	 * @param map
	 * @return 从服务器获得图片版块的service
	 */
	public String getPictures(Map<String, String> map){
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
		return HttpUtil.doPost(map, protocol + ip + ":" + port
				+ ReadPropertiesUtil.read("link", "Picture"));
	}
	
	
}
