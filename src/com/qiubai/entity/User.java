package com.qiubai.entity;

public class User {
	
	private String userid;
	private String nickname;
	private String password;
	private String icon;
	private String token;
	
	public User(){};
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "User [userid=" + userid + ", nickname=" + nickname
				+ ", password=" + password + ", icon=" + icon + ", token="
				+ token + "]";
	}

}