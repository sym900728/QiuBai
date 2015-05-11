package com.qiubai.entity;

public class Picture {
	private int id;
	private String userid;
	private String pic_title;
	private String pic_time;
	private String pic_address;
	private String pic_describe;

	public Picture() {
		super();
	}

	public Picture(int id, String userid, String pic_title, String pic_time,
			String pic_address, String pic_describe) {
		super();
		this.id = id;
		this.userid = userid;
		this.pic_title = pic_title;
		this.pic_time = pic_time;
		this.pic_address = pic_address;
		this.pic_describe = pic_describe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPic_title() {
		return pic_title;
	}

	public void setPic_title(String pic_title) {
		this.pic_title = pic_title;
	}

	public String getPic_time() {
		return pic_time;
	}

	public void setPic_time(String pic_time) {
		this.pic_time = pic_time;
	}

	public String getPic_address() {
		return pic_address;
	}

	public void setPic_address(String pic_address) {
		this.pic_address = pic_address;
	}

	public String getPic_describe() {
		return pic_describe;
	}

	public void setPic_describe(String pic_describe) {
		this.pic_describe = pic_describe;
	}

}
