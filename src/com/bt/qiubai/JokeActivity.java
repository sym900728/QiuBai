package com.bt.qiubai;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
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
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qiubai.entity.Joke;
import com.qiubai.util.DensityUtil;
import com.qiubai.util.SharedPreferencesUtil;

public class JokeActivity extends Activity implements OnTouchListener, OnClickListener{

	private ScrollView joke_scroll;
	private RelativeLayout joke_title_rel_back, joke_title_rel_comment, joke_title_rel_right;
	private RelativeLayout joke_rel_zan;
	private TextView joke_tv_comment, joke_tv_from, joke_tv_time, joke_tv_content;
	private LinearLayout common_action_share, common_action_collect, common_action_comment,
		common_action_font;
	private ImageView common_dialog_font_iv_super_large, common_dialog_font_iv_large,
		common_dialog_font_iv_middle, common_dialog_font_iv_small;
	private ImageView joke_iv_zan;
	private LinearLayout common_dialog_font_super_large, common_dialog_font_large,
		common_dialog_font_middle, common_dialog_font_small, common_dialog_font_cancel,
		common_dialog_font_confirm;
	
	private Dialog actionDialog;
	private Dialog fontDialog;
	private boolean isShowZan = true, isZanAnimating = false;
	private GestureDetector gestureDetector;
	private SharedPreferencesUtil spUtil = new SharedPreferencesUtil(JokeActivity.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.joke_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.joke_title);
		
		joke_title_rel_back = (RelativeLayout) findViewById(R.id.joke_title_rel_back);
		joke_title_rel_back.setOnClickListener(this);
		joke_title_rel_comment = (RelativeLayout) findViewById(R.id.joke_title_rel_comment);
		joke_title_rel_comment.setOnClickListener(this);
		joke_title_rel_right = (RelativeLayout) findViewById(R.id.joke_title_rel_right);
		joke_title_rel_right.setOnClickListener(this);
		joke_rel_zan = (RelativeLayout) findViewById(R.id.joke_rel_zan);
		joke_rel_zan.setOnClickListener(this);
		joke_iv_zan = (ImageView) findViewById(R.id.joke_iv_zan);
		joke_iv_zan.setTag("inactive");
		
		joke_scroll = (ScrollView) findViewById(R.id.joke_scroll);
		joke_scroll.setOnTouchListener(this);
		
		gestureDetector = new GestureDetector(JokeActivity.this,onGestureListener);
		
		Intent intent = getIntent();
		Joke joke = (Joke) intent.getSerializableExtra("joke");
		
		joke_tv_comment = (TextView) findViewById(R.id.joke_tv_comment);
		joke_tv_comment.setText(String.valueOf(joke.getComments()) + " 评论");
		joke_tv_from = (TextView) findViewById(R.id.joke_tv_from);
		joke_tv_from.setText("来自：" + joke.getBelong());
		joke_tv_time = (TextView) findViewById(R.id.joke_tv_time);
		joke_tv_time.setText(dealTime(joke.getTime()));
		joke_tv_content = (TextView) findViewById(R.id.joke_tv_content);
		joke_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, getFont());
		joke_tv_content.setText(joke.getContent());
		
		actionDialog = new Dialog(JokeActivity.this, R.style.CommonActionDialog);
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
		
		fontDialog = new Dialog(JokeActivity.this, R.style.CommonDialog);
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
		case R.id.joke_title_rel_back:
			JokeActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.joke_title_rel_comment:
			Intent intent_to_comment = new Intent(JokeActivity.this, CommentActivity.class);
			intent_to_comment.putExtra("belong", "joke");
			intent_to_comment.putExtra("newsid", 320);
			startActivity(intent_to_comment);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		case R.id.joke_title_rel_right:
			actionDialog.show();
			break;
		case R.id.joke_rel_zan:
			Bitmap bitmap_inactive = BitmapFactory.decodeResource(getResources(), R.drawable.joke_zan_inactive);
			Bitmap bitmap_activie = BitmapFactory.decodeResource(getResources(), R.drawable.joke_zan_active);
			if("inactive".equals(joke_iv_zan.getTag().toString())){
				joke_iv_zan.setImageBitmap(bitmap_activie);
				joke_iv_zan.setTag("active");
			} else {
				joke_iv_zan.setImageBitmap(bitmap_inactive);
				joke_iv_zan.setTag("inactive");
			}
			break;
		case R.id.common_action_comment:
			actionDialog.dismiss();
			Intent intent_to_comment2 = new Intent(JokeActivity.this, CommentActivity.class);
			intent_to_comment2.putExtra("belong", "joke");
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
	 * move zan
	 */
	public void moveZan(){
		if(isShowZan && !isZanAnimating){
			android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) joke_rel_zan.getLayoutParams();
			lp.rightMargin = DensityUtil.dip2px(JokeActivity.this, 10);
			joke_rel_zan.setLayoutParams(lp);
			ObjectAnimator hideZanAnimator = ObjectAnimator.ofFloat(joke_rel_zan, "translationX", 0, DensityUtil.dip2px(this, 60));
			hideZanAnimator.setDuration(600);
			hideZanAnimator.setInterpolator(new LinearInterpolator());
			hideZanAnimator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					isZanAnimating = true;
				}
				@Override
				public void onAnimationRepeat(Animator animation) {
				}
				@Override
				public void onAnimationEnd(Animator animation) {
					isZanAnimating = false;
					isShowZan = false;
				}
				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			hideZanAnimator.start();
			
		} else if(!isShowZan && !isZanAnimating){
			android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) joke_rel_zan.getLayoutParams();
			lp.rightMargin = - DensityUtil.dip2px(JokeActivity.this, 50);
			joke_rel_zan.setLayoutParams(lp);
			ObjectAnimator showZanAnimator = ObjectAnimator.ofFloat(joke_rel_zan, "translationX", 0, - DensityUtil.dip2px(this, 60));
			showZanAnimator.setDuration(600);
			showZanAnimator.setInterpolator(new LinearInterpolator());
			showZanAnimator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					isZanAnimating = true;
				}
				@Override
				public void onAnimationRepeat(Animator animation) {
				}
				@Override
				public void onAnimationEnd(Animator animation) {
					isZanAnimating = false;
					isShowZan = true;
				}
				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			showZanAnimator.start();
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
	 * store font and change content text font size
	 */
	public void storeAndChangeFont(){
		if("checked".equals((String) common_dialog_font_iv_super_large.getTag())){
			spUtil.storeFont("21");
			joke_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f);
		} else if("checked".equals((String) common_dialog_font_iv_large.getTag())){
			spUtil.storeFont("19");
			joke_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f);
		} else if("checked".equals((String) common_dialog_font_iv_middle.getTag())){
			spUtil.storeFont("17");
			joke_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
		} else {
			spUtil.storeFont("15");
			joke_tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
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
			JokeActivity.this.finish();
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
					JokeActivity.this.finish();
					overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
					return true;
				}else if(e2.getX() - e1.getX() < -200){
					Intent intent = new Intent(JokeActivity.this, CommentActivity.class);
					intent.putExtra("belong", "joke");
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
		
		public boolean onDoubleTap(MotionEvent e) {
			moveZan();
			return false;
		}
	};
	
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
