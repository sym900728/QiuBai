package com.bt.qiubai;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PictureTextActivity extends Activity implements OnClickListener{
	
	private RelativeLayout pt_title_back,pt_title_menu;
	private LinearLayout action_share,action_comment;
	
	private ViewPager viewpager;
	private List<View> list;  //表示装载滑动的布局
	private MyPagerAdpater myPagerAdpater;
	
	private Dialog actionDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pt_activity);
		
		pt_title_back = (RelativeLayout) findViewById(R.id.pt_title_back);
		pt_title_menu = (RelativeLayout) findViewById(R.id.pt_title_action_bar);
		pt_title_back.setOnClickListener(this);
		pt_title_menu.setOnClickListener(this);
		
		actionDialog = new Dialog(PictureTextActivity.this, R.style.CommonActionDialog);
		actionDialog.setContentView(R.layout.common_action_bar);
		actionDialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		
		action_share = (LinearLayout) actionDialog.findViewById(R.id.common_action_share);
		action_comment = (LinearLayout) actionDialog.findViewById(R.id.common_action_comment);
		action_share.setOnClickListener(this);
		action_comment.setOnClickListener(this);
		
		viewpager = (ViewPager) findViewById(R.id.pt_viewpager);
		createImageView();
		
		myPagerAdpater = new MyPagerAdpater();
		viewpager.setAdapter(myPagerAdpater);
		
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				System.out.println(arg0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
	}
	
	public void createImageView(){
		list = new ArrayList<View>();
		View v1 = getLayoutInflater().inflate(R.layout.pt_viewpager_item, null);
		View v2 = getLayoutInflater().inflate(R.layout.pt_viewpager_item, null);
		View v3 = getLayoutInflater().inflate(R.layout.pt_viewpager_item, null);
		View v4 = getLayoutInflater().inflate(R.layout.pt_viewpager_item, null);
		
		
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		
	}
	
	private class MyPagerAdpater extends PagerAdapter{
		
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager)container).addView(list.get(position));
			return list.get(position);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(list.get(position));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pt_title_back:
			PictureTextActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.pt_title_action_bar:
			actionDialog.show();
			break;
			
		case R.id.common_action_share:
			actionDialog.dismiss();
			break;
			
		case R.id.common_action_comment:
			actionDialog.dismiss();
			Intent intent = new Intent(PictureTextActivity.this, CommentActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		}
	}

	
	
}
