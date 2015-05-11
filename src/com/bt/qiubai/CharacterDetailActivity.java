package com.bt.qiubai;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qiubai.util.SharedPreferencesUtil;

public class CharacterDetailActivity extends Activity implements OnClickListener, OnTouchListener{
	
	private RelativeLayout title_back,title_rel_right;
	private RelativeLayout cd_rel_comment, cd_rel_support, cd_rel_tread;
	private LinearLayout cd_dialog_font_super_large, cd_dialog_font_large, cd_dialog_font_middle,
		cd_dialog_font_small, cd_dialog_font_confirm, cd_dialog_font_cancel,
		cd_action_share, cd_action_comment, cd_action_font, cd_action_collect;
	private ScrollView cd_scroll;
	private TextView cd_tv_content, cd_tv_from, cd_tv_title, cd_tv_time, cd_tv_comment, cd_tv_support, cd_tv_tread;
	private ImageView cd_iv_support, cd_iv_tread;
	
	private ImageView cd_dialog_font_iv_super_large, cd_dialog_font_iv_large, cd_dialog_font_iv_middle, cd_dialog_font_iv_small;
	
	private int newsid;
	private Dialog actionDialog;
	private Dialog fontDialog;
	private GestureDetector gestureDetector;
	private Animation anim_support, anim_tread;
	
	private SharedPreferencesUtil spUtil = new SharedPreferencesUtil(CharacterDetailActivity.this);
	
	@SuppressLint("RtlHardcoded")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.cd_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.cd_title);
		
		Intent intent = getIntent();
		
		cd_tv_title = (TextView) findViewById(R.id.cd_tv_title);
		cd_tv_title.setText(intent.getStringExtra("fcd_char_title"));
		cd_tv_from = (TextView) findViewById(R.id.cd_tv_from);
		cd_tv_from.setText(intent.getStringExtra("fcd_user"));
		cd_tv_content = (TextView) findViewById(R.id.cd_tv_content);
		cd_tv_content.setText(intent.getStringExtra("fcd_context"));
		cd_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, getFont());
		cd_tv_time = (TextView) findViewById(R.id.cd_tv_time);
		cd_tv_time.setText(dealTime(intent.getStringExtra("fcd_char_time")));
		cd_tv_comment = (TextView) findViewById(R.id.cd_tv_comment);
		cd_tv_comment.setText("12832");
		cd_tv_support = (TextView) findViewById(R.id.cd_tv_support);
		cd_tv_support.setText(intent.getStringExtra("fcd_char_support"));
		cd_tv_tread = (TextView) findViewById(R.id.cd_tv_tread);
		cd_tv_tread.setText(intent.getStringExtra("fcd_char_oppose"));
		
		gestureDetector = new GestureDetector(CharacterDetailActivity.this,onGestureListener);
		anim_support = AnimationUtils.loadAnimation(this, R.anim.cd_support);
		anim_tread = AnimationUtils.loadAnimation(this, R.anim.cd_tread);
		
		cd_scroll = (ScrollView) findViewById(R.id.cd_scroll);
		cd_scroll.setOnTouchListener(this);
		
		actionDialog = new Dialog(CharacterDetailActivity.this, R.style.CommonActionDialog);
		actionDialog.setContentView(R.layout.cd_action_bar);
		actionDialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		
		cd_action_share = (LinearLayout) actionDialog.findViewById(R.id.cd_action_share);
		cd_action_share.setOnClickListener(this);
		cd_action_collect = (LinearLayout) actionDialog.findViewById(R.id.cd_action_collect);
		cd_action_collect.setOnClickListener(this);
		cd_action_comment = (LinearLayout) actionDialog.findViewById(R.id.cd_action_comment);
		cd_action_comment.setOnClickListener(this);
		cd_action_font = (LinearLayout) actionDialog.findViewById(R.id.cd_action_font);
		cd_action_font.setOnClickListener(this);
		
		fontDialog = new Dialog(CharacterDetailActivity.this, R.style.CommonDialog);
		fontDialog.setContentView(R.layout.cd_dialog_font);
		cd_dialog_font_iv_super_large = (ImageView) fontDialog.findViewById(R.id.cd_dialog_font_iv_super_large);
		cd_dialog_font_iv_large = (ImageView) fontDialog.findViewById(R.id.cd_dialog_font_iv_large);
		cd_dialog_font_iv_middle = (ImageView) fontDialog.findViewById(R.id.cd_dialog_font_iv_middle);
		cd_dialog_font_iv_small = (ImageView) fontDialog.findViewById(R.id.cd_dialog_font_iv_small);
		cd_dialog_font_super_large = (LinearLayout) fontDialog.findViewById(R.id.cd_dialog_font_super_large);
		cd_dialog_font_super_large.setOnClickListener(this);
		cd_dialog_font_large = (LinearLayout) fontDialog.findViewById(R.id.cd_dialog_font_large);
		cd_dialog_font_large.setOnClickListener(this);
		cd_dialog_font_middle = (LinearLayout) fontDialog.findViewById(R.id.cd_dialog_font_middle);
		cd_dialog_font_middle.setOnClickListener(this);
		cd_dialog_font_small = (LinearLayout) fontDialog.findViewById(R.id.cd_dialog_font_small);
		cd_dialog_font_small.setOnClickListener(this);
		cd_dialog_font_cancel = (LinearLayout) fontDialog.findViewById(R.id.cd_dialog_font_cancel);
		cd_dialog_font_cancel.setOnClickListener(this);
		cd_dialog_font_confirm = (LinearLayout) fontDialog.findViewById(R.id.cd_dialog_font_confirm);
		cd_dialog_font_confirm.setOnClickListener(this);
		
		title_back = (RelativeLayout) findViewById(R.id.detail_title_back);
		title_back.setOnClickListener(this);
		title_rel_right = (RelativeLayout) findViewById(R.id.title_rel_right);
		title_rel_right.setOnClickListener(this);
		cd_rel_comment = (RelativeLayout) findViewById(R.id.cd_rel_comment);
		cd_rel_comment.setOnClickListener(this);
		cd_rel_support = (RelativeLayout) findViewById(R.id.cd_rel_support);
		cd_rel_support.setOnClickListener(this);
		cd_rel_tread = (RelativeLayout) findViewById(R.id.cd_rel_tread);
		cd_rel_tread.setOnClickListener(this);
		cd_iv_support = (ImageView) findViewById(R.id.cd_iv_support);
		cd_iv_support.setTag("inactive");
		cd_iv_tread = (ImageView) findViewById(R.id.cd_iv_tread);
		cd_iv_tread.setTag("inactive");
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cd_action_share:
			actionDialog.dismiss();
			Intent intent_detail_to_pt = new Intent(CharacterDetailActivity.this, PictureTextActivity.class);
			startActivity(intent_detail_to_pt);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		case R.id.cd_action_collect:
			
			break;
		case R.id.cd_action_comment:
			actionDialog.dismiss();
			Intent intent_detail_to_comment = new Intent(CharacterDetailActivity.this, CommentActivity.class);
			intent_detail_to_comment.putExtra("newsid", 320);
			startActivity(intent_detail_to_comment);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		case R.id.cd_action_font:
			actionDialog.dismiss();
			initFontDialogRadio();
			fontDialog.show();
			break;
		case R.id.detail_title_back:
			CharacterDetailActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.title_rel_right:
			actionDialog.show();
			break;
		case R.id.cd_rel_comment:
			Intent intent_detail_to_comment_2 = new Intent(CharacterDetailActivity.this, CommentActivity.class);
			intent_detail_to_comment_2.putExtra("newsid", 320);
			startActivity(intent_detail_to_comment_2);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		case R.id.cd_rel_support:
			doSupport();
			break;
		case R.id.cd_rel_tread:
			doTread();
			break;
		case R.id.cd_dialog_font_cancel:
			fontDialog.dismiss();
			break;
		case R.id.cd_dialog_font_confirm:
			storeAndChangeFont();
			fontDialog.dismiss();
			break;
		case R.id.cd_dialog_font_super_large:
			selectFont(v);
			break;
		case R.id.cd_dialog_font_large:
			selectFont(v);
			break;
		case R.id.cd_dialog_font_middle:
			selectFont(v);
			break;
		case R.id.cd_dialog_font_small:
			selectFont(v);
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
	
	/**
	 * open font dialog, then set radio checked or not checked (default: all not checked)
	 */
	public void initFontDialogRadio(){
		Bitmap bitmap_on = BitmapFactory.decodeResource(getResources(), R.drawable.cd_radio_on);
		Bitmap bitmap_off = BitmapFactory.decodeResource(getResources(), R.drawable.cd_radio_off);
		cd_dialog_font_iv_super_large.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_super_large.setTag("unchecked");
		cd_dialog_font_iv_large.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_large.setTag("unchecked");
		cd_dialog_font_iv_middle.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_middle.setTag("unchecked");
		cd_dialog_font_iv_small.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_small.setTag("unchecked");
		float font = getFont();
		if(font == 21f){
			cd_dialog_font_iv_super_large.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_super_large.setTag("checked");
		} else if(font == 19f){
			cd_dialog_font_iv_large.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_large.setTag("checked");
		} else if(font == 17f){
			cd_dialog_font_iv_middle.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_middle.setTag("checked");
		} else {
			cd_dialog_font_iv_small.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_small.setTag("checked");
		}
	}
	
	/**
	 * store font and change content text font size
	 */
	public void storeAndChangeFont(){
		if("checked".equals((String) cd_dialog_font_iv_super_large.getTag())){
			spUtil.storeFont("21");
			cd_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f);
		} else if("checked".equals((String) cd_dialog_font_iv_large.getTag())){
			spUtil.storeFont("19");
			cd_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f);
		} else if("checked".equals((String) cd_dialog_font_iv_middle.getTag())){
			spUtil.storeFont("17");
			cd_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
		} else {
			spUtil.storeFont("15");
			cd_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
		}
	}
	
	/**
	 * select font (super large, large, middle, small)
	 * @param v view
	 */
	public void selectFont(View v){
		Bitmap bitmap_on = BitmapFactory.decodeResource(getResources(), R.drawable.cd_radio_on);
		Bitmap bitmap_off = BitmapFactory.decodeResource(getResources(), R.drawable.cd_radio_off);
		cd_dialog_font_iv_super_large.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_super_large.setTag("unchecked");
		cd_dialog_font_iv_large.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_large.setTag("unchecked");
		cd_dialog_font_iv_middle.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_middle.setTag("unchecked");
		cd_dialog_font_iv_small.setImageBitmap(bitmap_off);
		cd_dialog_font_iv_small.setTag("unchecked");
		switch (v.getId()) {
		case R.id.cd_dialog_font_super_large:
			cd_dialog_font_iv_super_large.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_super_large.setTag("checked");
			break;
		case R.id.cd_dialog_font_large:
			cd_dialog_font_iv_large.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_large.setTag("checked");
			break;
		case R.id.cd_dialog_font_middle:
			cd_dialog_font_iv_middle.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_middle.setTag("checked");
			break;
		case R.id.cd_dialog_font_small:
			cd_dialog_font_iv_small.setImageBitmap(bitmap_on);
			cd_dialog_font_iv_small.setTag("checked");
			break;
		}
	}
	
	/**
	 * do support
	 */
	public void doSupport(){
		String tag_support = (String) cd_iv_support.getTag();
		String tag_tread = (String) cd_iv_tread.getTag();
		if("inactive".equals(tag_support)){
			cd_iv_support.setTag("active");
			if("active".equals(tag_tread)){
				cd_tv_tread.setText(String.valueOf(Integer.parseInt(cd_tv_tread.getText().toString()) - 1 ));
				Bitmap bitmap_tread = BitmapFactory.decodeResource(getResources(), R.drawable.cd_tread_inactive);
				cd_iv_tread.setImageBitmap(bitmap_tread);
				cd_iv_tread.setTag("inactive");
			}
			cd_tv_support.setText(String.valueOf(Integer.parseInt(cd_tv_support.getText().toString()) + 1 ));
			Bitmap bitmap_support = BitmapFactory.decodeResource(getResources(), R.drawable.cd_support_active);
			cd_iv_support.setImageBitmap(bitmap_support);
			cd_iv_support.startAnimation(anim_support);
		}
	}
	
	/**
	 * do tread
	 */
	public void doTread(){
		String tag_support = (String) cd_iv_support.getTag();
		String tag_tread = (String) cd_iv_tread.getTag();
		if("inactive".equals(tag_tread)){
			cd_iv_tread.setTag("active");
			if("active".equals(tag_support)){
				cd_tv_support.setText(String.valueOf(Integer.parseInt(cd_tv_support.getText().toString()) - 1));
				Bitmap bitmap_support = BitmapFactory.decodeResource(getResources(), R.drawable.cd_support_inactive);
				cd_iv_support.setImageBitmap(bitmap_support);
				cd_iv_support.setTag("inactive");
			}
			cd_tv_tread.setText(String.valueOf(Integer.parseInt(cd_tv_tread.getText().toString()) + 1 ));
			Bitmap bitmap_tread = BitmapFactory.decodeResource(getResources(), R.drawable.cd_tread_active);
			cd_iv_tread.setImageBitmap(bitmap_tread);
			cd_iv_tread.startAnimation(anim_tread);
			
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			CharacterDetailActivity.this.finish();
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
					CharacterDetailActivity.this.finish();
					overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
					return true;
				}else if(e2.getX() - e1.getX() < -200){
					Intent intent = new Intent(CharacterDetailActivity.this, CommentActivity.class);
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
