package com.qiubai.entity;

public enum Weekend {
	Monday("星期一", "Monday"), 
	Tuesday("星期二", "Tuesday"), 
	Wednesday("星期三","Wednesday"), 
	Thursday("星期四", "Thursday"), 
	Friday("星期五", "Friday"), 
	Saturday("星期六", "Saturday"), 
	Sunday("星期天", "Sunday");

	private String name;
	private String index;

	private Weekend(String name, String index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @param index
	 * @return 星期几
	 */
	public static String getWeekName(String index) {

		for (Weekend weekend : Weekend.values()) {
			if (weekend.getIndex().equals(index)) {
				return weekend.getName();
			}
		}
		return index;

	}

}
