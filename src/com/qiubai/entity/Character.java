package com.qiubai.entity;

import java.util.Date;

public class Character {
	private int id;
	private String userid;
	private String char_title;
	private String char_context;
	private String char_support;
	private String char_oppose;
	private String char_comment;
	private String char_time;

	public Character() {
		super();
	}

	public Character(int id, String userid, String char_title,
			String char_context, String char_support, String char_oppose,
			String char_comment, String char_time) {
		super();
		this.id = id;
		this.userid = userid;
		this.char_title = char_title;
		this.char_context = char_context;
		this.char_support = char_support;
		this.char_oppose = char_oppose;
		this.char_comment = char_comment;
		this.char_time = char_time;
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

	public String getChar_title() {
		return char_title;
	}

	public void setChar_title(String char_title) {
		this.char_title = char_title;
	}

	public String getChar_context() {
		return char_context;
	}

	public void setChar_context(String char_context) {
		this.char_context = char_context;
	}

	public String getChar_support() {
		return char_support;
	}

	public void setChar_support(String char_support) {
		this.char_support = char_support;
	}

	public String getChar_oppose() {
		return char_oppose;
	}

	public void setChar_oppose(String char_oppose) {
		this.char_oppose = char_oppose;
	}

	public String getChar_comment() {
		return char_comment;
	}

	public void setChar_comment(String char_comment) {
		this.char_comment = char_comment;
	}

	public String getChar_time() {
		return char_time;
	}

	public void setChar_time(String char_time) {
		this.char_time = char_time;
	}

}
