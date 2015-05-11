package com.qiubai.adapter;

import java.util.List;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class MainTabAdapter extends FragmentPagerAdapter{
	
	private List<Fragment> mFragments;
	private FragmentManager fragmentManager;
	public static String[] TITLES = new String[]{"热门","文字","图片"};
	public MainTabAdapter(FragmentManager fm,List<Fragment> mFragments) {
		super(fm);
		this.fragmentManager = fm;
		this.mFragments = mFragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}
	
	
	@Override
	public CharSequence getPageTitle(int position) {
		
		return TITLES[position];
	}
	
	

}
