package com.qiubai.entity;

public class Novel {
	private int id;
	private String belong;
	private String image;
	private String title;
	private String description;
	private String content;
	private String time;
	private int comments;

	public Novel() {
	}

	public Novel(int id, String belong, String image, String title,
			String description, String content, String time, int comments) {
		this.id = id;
		this.belong = belong;
		this.image = image;
		this.title = title;
		this.description = description;
		this.content = content;
		this.time = time;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Novel [id=" + id + ", belong=" + belong + ", image=" + image
				+ ", title=" + title + ", description=" + description
				+ ", content=" + content + ", time=" + time + ", comments="
				+ comments + "]";
	}
	
}
