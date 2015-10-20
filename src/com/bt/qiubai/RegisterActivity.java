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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener, OnFocusChangeListener, OnTouchListener{
	
	private RelativeLayout register_title_back;
	private RelativeLayout register_user_register;
	private EditText register_email, register_nick_name, register_password;
	private ImageView register_email_iv_cancel, register_nick_name_iv_cancel, register_password_iv_cancel;
	private ImageView common_progress_dialog_iv_rotate;
	private ScrollView register_scroll;
	
	private GestureDetector gestureDetector;
	private UserService userService;
	
	private Dialog progressDialog;
	private Animation anim_rotate;
	
	private final static int REGISTER_SUCCESS = 1;
	private final static int REGISTER_EXIST = 2;
	private final static int REGISTER_ERROR = 3;
	private final static int REGISTER_FAIL = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.register_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.register_title);
		
		userService = new UserService(RegisterActivity.this);
		
		if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
		}
		
		progressDialog = new Dialog(RegisterActivity.this, R.style.CommonProgressDialog);
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
		
		register_title_back = (RelativeLayout) findViewById(R.id.register_title_back);
		register_title_back.setOnClickListener(this);
		register_user_register = (RelativeLayout) findViewById(R.id.register_user_register);
		register_user_register.setOnClickListener(this);
		
		register_email_iv_cancel = (ImageView) findViewById(R.id.register_email_iv_cancel);
		register_email_iv_cancel.setOnClickListener(this);
		register_nick_name_iv_cancel = (ImageView) findViewById(R.id.register_nick_name_iv_cancel);
		register_nick_name_iv_cancel.setOnClickListener(this);
		register_password_iv_cancel = (ImageView) findViewById(R.id.register_password_iv_cancel);
		register_password_iv_cancel.setOnClickListener(this);
		
		register_scroll = (ScrollView) findViewById(R.id.register_scroll);
		gestureDetector = new GestureDetector(RegisterActivity.this,onGestureListener);
		register_scroll.setOnTouchListener(this);
		
		register_email = (EditText) findViewById(R.id.register_email);
		register_email.setOnFocusChangeListener(this);
		register_email.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!"".equals(s.toString())){
					register_email_iv_cancel.setVisibility(View.VISIBLE);
				} else{
					register_email_iv_cancel.setVisibility(View.INVISIBLE);
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
		
		register_nick_name = (EditText) findViewById(R.id.register_nick_name);
		register_nick_name.setOnFocusChangeListener(this);
		register_nick_name.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!"".equals(s.toString())){
					register_nick_name_iv_cancel.setVisibility(View.VISIBLE);
				} else{
					register_nick_name_iv_cancel.setVisibility(View.INVISIBLE);
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
		
		register_password = (EditText) findViewById(R.id.register_password);
		register_password.setOnFocusChangeListener(this);
		register_password.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!"".equals(s.toString())){
					register_password_iv_cancel.setVisibility(View.VISIBLE);
				} else{
					register_password_iv_cancel.setVisibility(View.INVISIBLE);
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
		case R.id.register_email:
			if(hasFocus){
				if(!"".equals(register_email.getText().toString())){
					register_email_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				register_email_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.register_nick_name:
			if(hasFocus){
				if(!"".equals(register_nick_name.getText().toString())){
					register_nick_name_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				register_nick_name_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.register_password:
			if(hasFocus){
				if(!"".equals(register_password.getText().toString())){
					register_password_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				register_password_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_title_back:
			RegisterActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.register_user_register:
			if(verifyRegisterInformation()){
				common_progress_dialog_iv_rotate.startAnimation(anim_rotate);
				progressDialog.show();
				register();
			}
			break;
		case R.id.register_email_iv_cancel:
			register_email.setText("");
			break;
		case R.id.register_nick_name_iv_cancel:
			register_nick_name.setText("");
			break;
		case R.id.register_password_iv_cancel:
			register_password.setText("");
			break;
		}
	}
	
	/**
	 * verify user register information
	 * @return true: verify success; false: verify fail
	 */
	public boolean verifyRegisterInformation(){
		String regex = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(register_email.getText().toString());
		if("".equals(register_email.getText().toString().trim())){
			Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!matcher.matches() ){
			Toast.makeText(this, "请输入正确的邮箱格式", Toast.LENGTH_SHORT).show();
			return false;
		} else if("".equals(register_nick_name.getText().toString().trim()) ) {
			Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
			return false;
		} else if( register_nick_name.getText().toString().trim().length() > 10 || register_nick_name.getText().toString().trim().length() < 3){
			Toast.makeText(this, "请输入3~10位字符的昵称", Toast.LENGTH_SHORT).show();
			return false;
		} else if("".equals(register_password.getText()) ){
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if( register_password.getText().toString().length() > 20 || register_password.getText().toString().length() < 6 ){
			Toast.makeText(this, "请输入6~20位字符的密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * user register
	 */
	public void register(){
		new Thread(){
			public void run() {
				String result = userService.register(register_email.getText().toString(), register_nick_name.getText().toString().trim(), register_password.getText().toString());
				if("success".equals(result)){
					Message msg = registerHandler.obtainMessage(REGISTER_SUCCESS);
					registerHandler.sendMessage(msg);
				} else if("exist".equals(result)){
					Message msg = registerHandler.obtainMessage(REGISTER_EXIST);
					registerHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = registerHandler.obtainMessage(REGISTER_ERROR);
					registerHandler.sendMessage(msg);
				} else if("fail".equals(result)){
					Message msg = registerHandler.obtainMessage(REGISTER_FAIL);
					registerHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler registerHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_SUCCESS:
				Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
				finish();
				overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
				break;
			case REGISTER_EXIST:
				Toast.makeText(RegisterActivity.this, "邮箱已经被注册，请另选邮箱", Toast.LENGTH_SHORT).show();
				break;
			case REGISTER_ERROR:
				Toast.makeText(RegisterActivity.this, "注册异常", Toast.LENGTH_SHORT).show();
				break;
			case REGISTER_FAIL:
				Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
				break;
			}
			progressDialog.dismiss();
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			RegisterActivity.this.finish();
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
					RegisterActivity.this.finish();
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

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
}
