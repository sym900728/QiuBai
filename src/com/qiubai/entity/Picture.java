package com.qiubai.entity;

import java.io.Serializable;


public class Picture implements Serializable{
	
	private static final long serialVersionUID = 5716574130662417946L;
	
	private int id;
	private String belong;
	private String title;
	private String image;
	private String time;
	private int comments;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getComments() {
		return comments;
	}
	public void setComments(int comments) {
		this.comments = comments;
	}
	@Override
	public String toString() {
		return "Picture [id=" + id + ", belong=" + belong + ", title=" + title
				+ ", image=" + image + ", time=" + time + ", comments="
				+ comments + "]";
	}
	
}
