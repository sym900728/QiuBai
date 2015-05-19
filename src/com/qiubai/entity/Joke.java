package com.qiubai.entity;

import java.io.Serializable;

public class Joke implements Serializable{

	private static final long serialVersionUID = -7727946098042790466L;
	
	private int id;
	private String belong;
	private String content;
	private String time;
	private int zan;
	private int comments;

	public Joke(){}
	
	public Joke(int id, String belong, String content, String time, int zan,
			int comments) {
		this.id = id;
		this.belong = belong;
		this.content = content;
		this.time = time;
		this.zan = zan;
		this.comments = comments;
	}

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

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Joke [id=" + id + ", belong=" + belong + ", content=" + content
				+ ", time=" + time + ", zan=" + zan + ", comments=" + comments
				+ "]";
	}

}
