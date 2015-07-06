package com.qiubai.entity;

public class Comment {

	private int id;
	private String belong;
	private int newsid;
	private String userid;
	private String content;
	private String time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
	public int getNewsid() {
		return newsid;
	}
	public void setNewsid(int newsid) {
		this.newsid = newsid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "Comment [id=" + id + ", belong=" + belong + ", newsid="
				+ newsid + ", userid=" + userid + ", content=" + content
				+ ", time=" + time + "]";
	}
	

}
