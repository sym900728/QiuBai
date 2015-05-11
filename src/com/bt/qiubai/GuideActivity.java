package com.bt.qiubai;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.adapter.GuideViewPagerAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class GuideActivity extends Activity{

	private List<View> dots;
	private ViewPager mViewPager;
	private GuideViewPagerAdapter mAdapter;
	private View[] view = new View[3];

	private int oldPosition = 0;// 记录上一次保存的点
	private int currentItem;// 当前页面
	
	private Button enterBut;

	private List<View> viewList = new ArrayList<View>();// 把需要滑动的页卡添加到这个list中

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
		
		// 添加当前的activity到activitylist中，方便退出

		// 显示的点
		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.guide_dot_1));
		dots.add(findViewById(R.id.guide_dot_2));
		dots.add(findViewById(R.id.guide_dot_3));

		// 得到viewpager的布局
		LayoutInflater layoutInflater = LayoutInflater.from(GuideActivity.this);

		view[0] = layoutInflater.inflate(R.layout.guide_activity_detail1, null);
		view[1] = layoutInflater.inflate(R.layout.guide_activity_detail2, null);
		view[2] = layoutInflater.inflate(R.layout.guide_activity_detail3, null);

		viewList.add(view[0]);
		viewList.add(view[1]);
		viewList.add(view[2]);
		
		enterBut = (Button) view[2].findViewById(R.id.guide_enter_btn);
		enterBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//设置已经启动了一次引导页面
				setGuided();
				
				Intent intent = new Intent(GuideActivity.this, MainActivity.class);
				
				startActivity(intent);
				GuideActivity.this.finish();//将引导界面从栈中消除
			}
		});

		// 找到ViewPager
		mViewPager = (ViewPager) findViewById(R.id.guide_viewPager);
		mAdapter = new GuideViewPagerAdapter(viewList);
		mViewPager.setAdapter(mAdapter);
		dots.get(0)
				.setBackgroundResource(R.drawable.guide_activity_dot_focused);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				dots.get(oldPosition).setBackgroundResource(
						R.drawable.guide_activity_dot_normal);
				dots.get(arg0).setBackgroundResource(
						R.drawable.guide_activity_dot_focused);

				oldPosition = arg0;
				currentItem = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}
	
	private static final String SHAREDPREFERENCES_FIRSTENTER = "qiubai";
	private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

	protected void setGuided() {
		SharedPreferences share = getSharedPreferences(SHAREDPREFERENCES_FIRSTENTER, 0);
		SharedPreferences.Editor editor = share.edit();
		editor.putString(KEY_GUIDE_ACTIVITY, "false");
		editor.commit();
		
	}


}
