package com.qiubai.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qiubai.entity.Comment;
import com.qiubai.entity.CommentWithUser;
import com.qiubai.entity.User;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;

public class CommentService {

	private String protocol;
	private String ip;
	private String port;
	
	public CommentService(){
		protocol = ReadPropertiesUtil.read("config", "protocol");
		ip = ReadPropertiesUtil.read("config", "ip");
		port = ReadPropertiesUtil.read("config", "port");
	}
	
	public String getComments(String belong, String newsid, String offset, String length){
		Map<String, String> params = new HashMap<String, String>();
		params.put("belong", belong);
		params.put("newsid", newsid);
		params.put("offset", offset);
		params.put("length", length);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "getComments"));
	}
	
	public List<CommentWithUser> parseCommentsJson(String json){
		List<CommentWithUser> comments = new ArrayList<CommentWithUser>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("commentWithUser");
			for (int i = 0; i < jsonArray.length(); i ++) {
				CommentWithUser cwu = new CommentWithUser();
				Comment comment = new Comment();
				User user = new User();
				JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
				JSONObject jsonObject3 = (JSONObject) jsonObject2.get("comment");
				JSONObject jsonObject4 = (JSONObject) jsonObject2.get("user");
				comment.setId(jsonObject3.getInt("id"));
				comment.setBelong(jsonObject3.getString("belong"));
				comment.setNewsid(jsonObject3.getInt("newsid"));
				comment.setUserid(jsonObject3.getString("userid"));
				comment.setContent(jsonObject3.getString("content"));
				comment.setTime(jsonObject3.getString("time"));
				user.setUserid(jsonObject3.getString("userid"));
				user.setNickname(jsonObject4.getString("nickname"));
				user.setIcon(jsonObject4.getString("icon"));
				cwu.setComment(comment);
				cwu.setUser(user);
				comments.add(cwu);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return comments;
	}
	
	public String addComment(String belong, String newsid, String userid, String token, String content){
		Map<String, String> params = new HashMap<String, String>();
		params.put("belong", belong);
		params.put("newsid", newsid);
		params.put("userid", userid);
		params.put("content", content);
		return HttpUtil.doPost(params, protocol + ip + ":" + port + ReadPropertiesUtil.read("link", "addComment") + token);
	}
	
}
