package com.bt.qiubai;

import com.qiubai.entity.Novel;
import com.qiubai.util.SharedPreferencesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class NovelActivity extends Activity implements OnTouchListener, OnClickListener{
	
	private ScrollView novel_scroll;
	private RelativeLayout novel_title_rel_back, novel_title_rel_comment, novel_title_rel_right;
	private TextView novel_tv_title, novel_tv_from, novel_tv_time, novel_tv_content, novel_tv_comment;
	private ImageView common_dialog_font_iv_super_large, common_dialog_font_iv_large, 
		common_dialog_font_iv_middle, common_dialog_font_iv_small;
	private LinearLayout common_action_share, common_action_collect, common_action_comment, common_action_font,
		common_dialog_font_super_large, common_dialog_font_large, common_dialog_font_middle,
		common_dialog_font_small, common_dialog_font_cancel, common_dialog_font_confirm;

	private Dialog actionDialog;
	private Dialog fontDialog;
	private GestureDetector gestureDetector;
	private SharedPreferencesUtil spUtil = new SharedPreferencesUtil(NovelActivity.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.novel_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.novel_title);
		
		Intent intent = getIntent();
		Novel novel = (Novel) intent.getSerializableExtra("novel");
		
		novel_scroll = (ScrollView) findViewById(R.id.novel_scroll);
		novel_scroll.setOnTouchListener(this);
		
		novel_tv_title = (TextView) findViewById(R.id.novel_tv_title);
		novel_tv_title.setText(novel.getTitle());
		novel_tv_from = (TextView) findViewById(R.id.novel_tv_from);
		novel_tv_from.setText("来自：" + novel.getBelong());
		novel_tv_time = (TextView) findViewById(R.id.novel_tv_time);
		novel_tv_time.setText(dealTime(novel.getTime()));
		novel_tv_content = (TextView) findViewById(R.id.novel_tv_content);
		novel_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, getFont());
		novel_tv_content.setText(novel.getContent());
		novel_tv_comment = (TextView) findViewById(R.id.novel_tv_comment);
		novel_tv_comment.setText(String.valueOf(novel.getComments()) + " 评论" );
		
		novel_title_rel_back = (RelativeLayout) findViewById(R.id.novel_title_rel_back);
		novel_title_rel_back.setOnClickListener(this);
		novel_title_rel_comment = (RelativeLayout) findViewById(R.id.novel_title_rel_comment);
		novel_title_rel_comment.setOnClickListener(this);
		novel_title_rel_right = (RelativeLayout) findViewById(R.id.novel_title_rel_right);
		novel_title_rel_right.setOnClickListener(this);
		
		gestureDetector = new GestureDetector(NovelActivity.this,onGestureListener);
		
		actionDialog = new Dialog(NovelActivity.this, R.style.CommonActionDialog);
		actionDialog.setContentView(R.layout.common_action_bar);
		actionDialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		common_action_share = (LinearLayout) actionDialog.findViewById(R.id.common_action_share);
		common_action_share.setOnClickListener(this);
		common_action_collect = (LinearLayout) actionDialog.findViewById(R.id.common_action_collect);
		common_action_collect.setOnClickListener(this);
		common_action_comment = (LinearLayout) actionDialog.findViewById(R.id.common_action_comment);
		common_action_comment.setOnClickListener(this);
		common_action_font = (LinearLayout) actionDialog.findViewById(R.id.common_action_font);
		common_action_font.setOnClickListener(this);
		
		fontDialog = new Dialog(NovelActivity.this, R.style.CommonDialog);
		fontDialog.setContentView(R.layout.common_dialog_font);
		common_dialog_font_iv_super_large = (ImageView) fontDialog.findViewById(R.id.common_dialog_font_iv_super_large);
		common_dialog_font_iv_large = (ImageView) fontDialog.findViewById(R.id.common_dialog_font_iv_large);
		common_dialog_font_iv_middle = (ImageView) fontDialog.findViewById(R.id.common_dialog_font_iv_middle);
		common_dialog_font_iv_small = (ImageView) fontDialog.findViewById(R.id.common_dialog_font_iv_small);
		common_dialog_font_super_large = (LinearLayout) fontDialog.findViewById(R.id.common_dialog_font_super_large);
		common_dialog_font_super_large.setOnClickListener(this);
		common_dialog_font_large = (LinearLayout) fontDialog.findViewById(R.id.common_dialog_font_large);
		common_dialog_font_large.setOnClickListener(this);
		common_dialog_font_middle = (LinearLayout) fontDialog.findViewById(R.id.common_dialog_font_middle);
		common_dialog_font_middle.setOnClickListener(this);
		common_dialog_font_small = (LinearLayout) fontDialog.findViewById(R.id.common_dialog_font_small);
		common_dialog_font_small.setOnClickListener(this);
		common_dialog_font_cancel = (LinearLayout) fontDialog.findViewById(R.id.common_dialog_font_cancel);
		common_dialog_font_cancel.setOnClickListener(this);
		common_dialog_font_confirm = (LinearLayout) fontDialog.findViewById(R.id.common_dialog_font_confirm);
		common_dialog_font_confirm.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.novel_title_rel_back:
			NovelActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.novel_title_rel_comment:
			Intent intent_to_comment = new Intent(NovelActivity.this, CommentActivity.class);
			intent_to_comment.putExtra("belong", "belong");
			intent_to_comment.putExtra("newsid", 320);
			startActivity(intent_to_comment);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		case R.id.novel_title_rel_right:
			actionDialog.show();
			break;
		case R.id.common_action_share:
			break;
		case R.id.common_action_collect:
			break;
		case R.id.common_action_comment:
			actionDialog.dismiss();
			Intent intent_to_comment2 = new Intent(NovelActivity.this, CommentActivity.class);
			intent_to_comment2.putExtra("belong", "belong");
			intent_to_comment2.putExtra("newsid", 320);
			startActivity(intent_to_comment2);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		case R.id.common_action_font:
			actionDialog.dismiss();
			initFontDialogRadio();
			fontDialog.show();
			break;
		case R.id.common_dialog_font_super_large:
			selectFont(v);
			break;
		case R.id.common_dialog_font_large:
			selectFont(v);
			break;
		case R.id.common_dialog_font_middle:
			selectFont(v);
			break;
		case R.id.common_dialog_font_small:
			selectFont(v);
			break;
		case R.id.common_dialog_font_cancel:
			fontDialog.dismiss();
			break;
		case R.id.common_dialog_font_confirm:
			storeAndChangeFont();
			fontDialog.dismiss();
			break;
		}
	}

	/**
	 * open font dialog, then set radio checked or not checked (default: all not checked)
	 */
	public void initFontDialogRadio(){
		Bitmap bitmap_on = BitmapFactory.decodeResource(getResources(), R.drawable.common_radio_on);
		Bitmap bitmap_off = BitmapFactory.decodeResource(getResources(), R.drawable.common_radio_off);
		common_dialog_font_iv_super_large.setImageBitmap(bitmap_off);
		common_dialog_font_iv_super_large.setTag("unchecked");
		common_dialog_font_iv_large.setImageBitmap(bitmap_off);
		common_dialog_font_iv_large.setTag("unchecked");
		common_dialog_font_iv_middle.setImageBitmap(bitmap_off);
		common_dialog_font_iv_middle.setTag("unchecked");
		common_dialog_font_iv_small.setImageBitmap(bitmap_off);
		common_dialog_font_iv_small.setTag("unchecked");
		float font = getFont();
		if(font == 21f){
			common_dialog_font_iv_super_large.setImageBitmap(bitmap_on);
			common_dialog_font_iv_super_large.setTag("checked");
		} else if(font == 19f){
			common_dialog_font_iv_large.setImageBitmap(bitmap_on);
			common_dialog_font_iv_large.setTag("checked");
		} else if(font == 17f){
			common_dialog_font_iv_middle.setImageBitmap(bitmap_on);
			common_dialog_font_iv_middle.setTag("checked");
		} else {
			common_dialog_font_iv_small.setImageBitmap(bitmap_on);
			common_dialog_font_iv_small.setTag("checked");
		}
	}
	
	/**
	 * store font and change content text font size
	 */
	public void storeAndChangeFont(){
		if("checked".equals((String) common_dialog_font_iv_super_large.getTag())){
			spUtil.storeFont("21");
			novel_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f);
		} else if("checked".equals((String) common_dialog_font_iv_large.getTag())){
			spUtil.storeFont("19");
			novel_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f);
		} else if("checked".equals((String) common_dialog_font_iv_middle.getTag())){
			spUtil.storeFont("17");
			novel_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
		} else {
			spUtil.storeFont("15");
			novel_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
		}
	}
	
	/**
	 * select font (super large, large, middle, small)
	 * @param v view
	 */
	public void selectFont(View v){
		Bitmap bitmap_on = BitmapFactory.decodeResource(getResources(), R.drawable.common_radio_on);
		Bitmap bitmap_off = BitmapFactory.decodeResource(getResources(), R.drawable.common_radio_off);
		common_dialog_font_iv_super_large.setImageBitmap(bitmap_off);
		common_dialog_font_iv_super_large.setTag("unchecked");
		common_dialog_font_iv_large.setImageBitmap(bitmap_off);
		common_dialog_font_iv_large.setTag("unchecked");
		common_dialog_font_iv_middle.setImageBitmap(bitmap_off);
		common_dialog_font_iv_middle.setTag("unchecked");
		common_dialog_font_iv_small.setImageBitmap(bitmap_off);
		common_dialog_font_iv_small.setTag("unchecked");
		switch (v.getId()) {
		case R.id.common_dialog_font_super_large:
			common_dialog_font_iv_super_large.setImageBitmap(bitmap_on);
			common_dialog_font_iv_super_large.setTag("checked");
			break;
		case R.id.common_dialog_font_large:
			common_dialog_font_iv_large.setImageBitmap(bitmap_on);
			common_dialog_font_iv_large.setTag("checked");
			break;
		case R.id.common_dialog_font_middle:
			common_dialog_font_iv_middle.setImageBitmap(bitmap_on);
			common_dialog_font_iv_middle.setTag("checked");
			break;
		case R.id.common_dialog_font_small:
			common_dialog_font_iv_small.setImageBitmap(bitmap_on);
			common_dialog_font_iv_small.setTag("checked");
			break;
		}
	}
	
	/**
	 * get font (get font then set the content font size)
	 * @return float type (if null default 17 float)
	 */
	public float getFont(){
		float font = 0f;
		if(spUtil.getFont() == null){
			spUtil.storeFont("17");
			font = 17f;
		} else {
			font = Float.parseFloat(spUtil.getFont());
		}
		return font;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			NovelActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float x = Math.abs(e2.getX() - e1.getX());
			float y = Math.abs(e2.getY() - e1.getY());
			if(y < x){
				if(e2.getX() - e1.getX() > 200){
					NovelActivity.this.finish();
					overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
					return true;
				}else if(e2.getX() - e1.getX() < -200){
					Intent intent = new Intent(NovelActivity.this, CommentActivity.class);
					intent.putExtra("belong", "novel");
					intent.putExtra("newsid", 320);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
					return true;
				}
			}else {
				return false;
			}
			
			return false;
		}
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	/**
	 * deal time string to YYYY/MM/DD
	 * @param str
	 * @return
	 */
	public String dealTime(String str){
		String year = (str.split(" ")[0]).split("-")[0];
		String month = (str.split(" ")[0]).split("-")[1];
		String day = (str.split(" ")[0]).split("-")[2];
		return year + "/" + month + "/" + day; 
	}
	
}
