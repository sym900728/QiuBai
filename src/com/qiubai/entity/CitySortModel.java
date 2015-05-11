package com.qiubai.entity;

public class CitySortModel {
	/**
	 * 显示的数据
	 */
	private String name;
	/**
	 * 显示数据拼音的首字母
	 */
	private String sortLetters;

	/**
	 * 拼音全称
	 */
	private String pinyinName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getPinyinName() {
		return pinyinName;
	}

	public void setPinyinName(String pinyinName) {
		this.pinyinName = pinyinName;
	}

}
