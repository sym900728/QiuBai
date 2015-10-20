package com.bt.qiubai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qiubai.service.UserService;
import com.qiubai.util.NetworkUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPasswordActivity extends Activity implements OnClickListener, OnTouchListener, OnFocusChangeListener{
	
	private ScrollView fpw_scroll;
	private RelativeLayout fpw_title_back, fpw_send;
	private TextView fpw_email;
	private ImageView fpw_email_iv_cancel, common_progress_dialog_iv_rotate;
	
	private GestureDetector gestureDetector;
	private UserService userService;
	
	private Dialog progressDialog;
	private Animation anim_rotate;
	
	private final static int FPW_SUCCESS = 1;
	private final static int FPW_ERROR = 2;
	private final static int FPW_FAIL = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.fpw_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.fpw_title);
		
		userService = new UserService(ForgetPasswordActivity.this);
		
		if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
		}
		
		fpw_scroll = (ScrollView) findViewById(R.id.fpw_scroll);
		gestureDetector = new GestureDetector(ForgetPasswordActivity.this,onGestureListener);
		fpw_scroll.setOnTouchListener(this);
		
		progressDialog = new Dialog(ForgetPasswordActivity.this, R.style.CommonProgressDialog);
		progressDialog.setContentView(R.layout.common_progress_dialog);
		progressDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams progressDialog_lp = progressDialog.getWindow().getAttributes();
		progressDialog_lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		progressDialog_lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        progressDialog.getWindow().setAttributes(progressDialog_lp);
        progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		common_progress_dialog_iv_rotate = (ImageView) progressDialog.findViewById(R.id.common_progress_dialog_iv_rotate);
		anim_rotate = AnimationUtils.loadAnimation(this, R.anim.common_rotate); 
		
		fpw_title_back = (RelativeLayout) findViewById(R.id.fpw_title_back);
		fpw_title_back.setOnClickListener(this);
		fpw_send = (RelativeLayout) findViewById(R.id.fpw_send);
		fpw_send.setOnClickListener(this);
		fpw_email_iv_cancel = (ImageView) findViewById(R.id.fpw_email_iv_cancel);
		fpw_email_iv_cancel.setOnClickListener(this);
		
		fpw_email = (TextView) findViewById(R.id.fpw_email);
		fpw_email.setOnFocusChangeListener(this);
		fpw_email.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!"".equals(s.toString())){
					fpw_email_iv_cancel.setVisibility(View.VISIBLE);
				} else{
					fpw_email_iv_cancel.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.fpw_email:
			if(hasFocus){
				if(!"".equals(fpw_email.getText().toString())){
					fpw_email_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				fpw_email_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fpw_title_back:
			ForgetPasswordActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.fpw_email_iv_cancel:
			fpw_email.setText("");
			break;
		case R.id.fpw_send:
			if(verifyForgetPasswordInformation()){
				common_progress_dialog_iv_rotate.startAnimation(anim_rotate);
				progressDialog.show();
				forgetPassword();
			}
			break;
		}
	}
	
	/**
	 * send email to server
	 */
	public void forgetPassword(){
		new Thread(){
			public void run() {
				String result = userService.forgetPassword(fpw_email.getText().toString());
				if("success".equals(result)){
					Message msg = forgetPasswrodHandler.obtainMessage(FPW_SUCCESS);
					forgetPasswrodHandler.sendMessage(msg);
				} else if ("error".equals(result)){
					Message msg = forgetPasswrodHandler.obtainMessage(FPW_ERROR);
					forgetPasswrodHandler.sendMessage(msg);
				} else if ("fail".equals(result)){
					Message msg = forgetPasswrodHandler.obtainMessage(FPW_FAIL);
					forgetPasswrodHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	/**
	 * verify user input information
	 * @return true: verify success; false: verify fail
	 */
	public boolean verifyForgetPasswordInformation(){
		String regex = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(fpw_email.getText().toString());
		if("".equals(fpw_email.getText().toString().trim()) ){
			Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!matcher.matches()){
			Toast.makeText(this, "请输入正确的邮箱格式", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler forgetPasswrodHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FPW_SUCCESS:
				Toast.makeText(ForgetPasswordActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				ForgetPasswordActivity.this.finish();
				overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
				break;
			case FPW_FAIL:
				Toast.makeText(ForgetPasswordActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
				break;
			case FPW_ERROR:
				Toast.makeText(ForgetPasswordActivity.this, "发送异常", Toast.LENGTH_SHORT).show();
				break;
			}
			progressDialog.dismiss();
		};
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ForgetPasswordActivity.this.finish();
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
					ForgetPasswordActivity.this.finish();
					overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
					return true;
				}else if(e2.getX() - e1.getX() < -200){					
					return false;
				}
			}else {
				return false;
			}
			return false;
		}
	};

}
