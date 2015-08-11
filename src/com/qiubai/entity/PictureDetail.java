package com.qiubai.entity;

public class PictureDetail {

	private int id;
	private int pictureid;
	private String image;
	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPictureid() {
		return pictureid;
	}

	public void setPictureid(int pictureid) {
		this.pictureid = pictureid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "PictureDetail [id=" + id + ", pictureid=" + pictureid
				+ ", image=" + image + ", content=" + content + "]";
	}

}
