package com.qiubai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qiubai.entity.Character;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;

public class CharacterService {

	private String protocol;
	private String ip;
	private String port;

	public CharacterService() {

	}

	/**
	 * @param map
	 * @return 从服务器获取取得文字版块的service
	 */
	public String getCharacters(Map<String, String> map) {
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
		return HttpUtil.doPost(map, protocol + ip + ":" + port
				+ ReadPropertiesUtil.read("link", "CHARACTER_URL"));
	}

	/**
	 * @param map
	 * @return 点赞和点吐槽时，往服务器中添加信息的service
	 */
	public String getaddSupportTread(Map<String, String> map) {
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
		return HttpUtil.doPost(map , protocol + ip + ":" + port
						+ ReadPropertiesUtil.read("link", "ADD_CHARACTER_SUPPORT_OPPOSE"));
	}

	/**
	 * 请求Character数据
	 * 
	 * @param json
	 * @return json数据的格式
	 */
	public List<Character> getCharacterByJson(String json) {
		// System.out.println(json);
		List<Character> listChar = new ArrayList<Character>();
		Character character = null;

		if (json != null || json.length() <= 0) {
			try {
				JSONObject jsonObjects = new JSONObject(json);
				if (jsonObjects != null) {

					JSONArray jsonObjs = jsonObjects.getJSONArray("character");
					if (jsonObjs != null) {
						for (int i = 0; i < jsonObjs.length(); i++) {
							JSONObject jsonObject = (JSONObject) jsonObjs
									.getJSONObject(i);

							character = new Character();
							character.setId(jsonObject.getInt("id"));
							character.setUserid(jsonObject.getString("userid"));
							character.setChar_title(jsonObject
									.getString("char_title"));
							character.setChar_context(jsonObject
									.getString("char_context"));
							character.setChar_support(jsonObject
									.getString("char_support"));
							character.setChar_oppose(jsonObject
									.getString("char_oppose"));
							character.setChar_time(jsonObject
									.getString("char_time"));
							character.setChar_comment(jsonObject
									.getString("char_comment"));
							listChar.add(character);
						}
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return listChar;

	}
}
