package com.qiubai.dao;

import java.util.Comparator;

import com.qiubai.entity.CitySortModel;

public class PinyinComparator implements Comparator<CitySortModel> {

	@Override
	public int compare(CitySortModel lhs, CitySortModel rhs) {
		if (lhs.getSortLetters().equals("@")
				|| rhs.getSortLetters().equals("#")) {
			return -1;
		} else if (lhs.getSortLetters().equals("#")
				|| rhs.getSortLetters().equals("@")) {
			return 1;
		} else {
			return lhs.getPinyinName().compareTo(rhs.getPinyinName());
		}
	}
}
