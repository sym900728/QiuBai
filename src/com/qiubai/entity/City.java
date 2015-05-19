package com.qiubai.entity;

public class City {
	/**
	 * 省份(江苏)
	 */
	private String province;
	/**
	 * 相对应的城市(镇江)
	 */
	private String town;

	/**
	 * 省份(江苏)jiangsu
	 */
	private String proven;

	/**
	 * 相对应的城市(镇江)zhengjiang
	 */
	private String districten;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getProven() {
		return proven;
	}

	public void setProven(String proven) {
		this.proven = proven;
	}

	public String getDistricten() {
		return districten;
	}

	public void setDistricten(String districten) {
		this.districten = districten;
	}

}
