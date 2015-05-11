package com.qiubai.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class GuideViewPagerAdapter extends PagerAdapter{
	
	private List<View> viewList;

	public GuideViewPagerAdapter(List<View> viewList) {
		this.viewList = viewList;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(viewList.get(position));
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager)container).addView(viewList.get(position));
		return viewList.get(position);
		
	}


}
