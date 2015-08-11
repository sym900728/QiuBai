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

public class PictureActivity extends Activity implements OnClickListener{
	
	private RelativeLayout picture_title_back, picture_title_action_bar;
	private RelativeLayout picture_title_rel_comment;
	private LinearLayout picture_action_share, picture_action_collect, picture_action_comment;
	
	
	private int picture_id = 0;
	
	private ViewPager picture_viewpager;
	private List<View> list;  //表示装载滑动的布局
	private MyPagerAdpater myPagerAdpater;
	
	private Dialog pictureActionDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_activity);
		
		picture_title_back = (RelativeLayout) findViewById(R.id.picture_title_back);
		picture_title_rel_comment = (RelativeLayout) findViewById(R.id.picture_title_rel_comment);
		picture_title_action_bar = (RelativeLayout) findViewById(R.id.picture_title_action_bar);
		picture_title_back.setOnClickListener(this);
		picture_title_rel_comment.setOnClickListener(this);
		picture_title_action_bar.setOnClickListener(this);
		
		pictureActionDialog = new Dialog(PictureActivity.this, R.style.CommonActionDialog);
		pictureActionDialog.setContentView(R.layout.picture_action_bar);
		pictureActionDialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		
		picture_action_share = (LinearLayout) pictureActionDialog.findViewById(R.id.picture_action_share);
		picture_action_collect = (LinearLayout) pictureActionDialog.findViewById(R.id.picture_action_collect);
		picture_action_comment = (LinearLayout) pictureActionDialog.findViewById(R.id.picture_action_comment);
		picture_action_share.setOnClickListener(this);
		picture_action_collect.setOnClickListener(this);
		picture_action_comment.setOnClickListener(this);
		
		picture_viewpager = (ViewPager) findViewById(R.id.picture_viewpager);
		createImageView();
		
		myPagerAdpater = new MyPagerAdpater();
		picture_viewpager.setAdapter(myPagerAdpater);
		
		picture_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
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
		case R.id.picture_title_back:
			PictureActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
			
		case R.id.picture_title_rel_comment:
			Intent intent_to_comment1 = new Intent(PictureActivity.this, CommentActivity.class);
			intent_to_comment1.putExtra("belong", "picture");
			intent_to_comment1.putExtra("newsid", picture_id);
			startActivity(intent_to_comment1);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
			
		case R.id.picture_title_action_bar:
			pictureActionDialog.show();
			break;
			
		case R.id.picture_action_share:
			pictureActionDialog.dismiss();
			break;
			
		case R.id.picture_action_collect:
			pictureActionDialog.dismiss();
			break;
			
		case R.id.picture_action_comment:
			pictureActionDialog.dismiss();
			Intent intent_to_comment2 = new Intent(PictureActivity.this, CommentActivity.class);
			intent_to_comment2.putExtra("belong", "picture");
			intent_to_comment2.putExtra("newsid", picture_id);
			startActivity(intent_to_comment2);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		}
	}
}
