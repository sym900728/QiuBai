package com.bt.qiubai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qiubai.entity.User;
import com.qiubai.service.UserService;
import com.qiubai.util.NetworkUtil;
import com.qiubai.util.SharedPreferencesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener, OnTouchListener, OnFocusChangeListener{
	
	private RelativeLayout login_title_back;
	private RelativeLayout login_layout_to_register, login_rel_forget_password;
	private LinearLayout login_login, login_account_qq, login_account_sina;
	private ScrollView login_scroll;
	private EditText login_user_email,login_user_password;
	private ImageView login_user_email_iv_cancel, login_user_password_iv_cancel;
	private ImageView common_progress_dialog_iv_rotate;
	
	private GestureDetector gestureDetector;
	private SharedPreferencesUtil spUtil = new SharedPreferencesUtil(LoginActivity.this);
	private UserService userService;
	
	private Dialog progressDialog;
	private Animation anim_rotate;
	
	private final static int LOGIN_SUCCESS = 1;
	private final static int LOGIN_FAIL = 2;
	private final static int LOGIN_ERROR = 3;
	private final static int LOGIN_ICON_SUCCESS = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.login_title);  
		
		userService = new UserService(LoginActivity.this);
		
		if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
		}
		
		progressDialog = new Dialog(LoginActivity.this, R.style.CommonProgressDialog);
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
		
		login_title_back = (RelativeLayout) findViewById(R.id.login_title_back);
		login_title_back.setOnClickListener(this);
		
		login_layout_to_register = (RelativeLayout) findViewById(R.id.login_layout_to_register);
		login_layout_to_register.setOnClickListener(this);
		
		login_account_qq = (LinearLayout) findViewById(R.id.login_account_qq);
		login_account_qq.setOnClickListener(this);
		
		login_account_sina = (LinearLayout) findViewById(R.id.login_account_sina);
		login_account_sina.setOnClickListener(this);
		
		login_scroll = (ScrollView) findViewById(R.id.login_scroll);
		gestureDetector = new GestureDetector(LoginActivity.this,onGestureListener);
		login_scroll.setOnTouchListener(this);
		
		login_user_email_iv_cancel = (ImageView) findViewById(R.id.login_user_email_iv_cancel);
		login_user_email_iv_cancel.setOnClickListener(this);
		login_user_password_iv_cancel = (ImageView) findViewById(R.id.login_user_password_iv_cancel);
		login_user_password_iv_cancel.setOnClickListener(this);
		
		login_user_email = (EditText) findViewById(R.id.login_user_email);
		login_user_email.setOnFocusChangeListener(this);
		login_user_email.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!"".equals(s.toString())){
					login_user_email_iv_cancel.setVisibility(View.VISIBLE);
				} else {
					login_user_email_iv_cancel.setVisibility(View.INVISIBLE);
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
		
		login_user_password = (EditText) findViewById(R.id.login_user_password);
		login_user_password.setOnFocusChangeListener(this);
		login_user_password.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!"".equals(s.toString())){
					login_user_password_iv_cancel.setVisibility(View.VISIBLE);
				} else{
					login_user_password_iv_cancel.setVisibility(View.INVISIBLE);
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
		
		login_login = (LinearLayout) findViewById(R.id.login_login_lin);
		login_login.setOnClickListener(this);
		
		login_rel_forget_password = (RelativeLayout) findViewById(R.id.login_rel_forget_password);
		login_rel_forget_password.setOnClickListener(this);
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.login_user_email:
			if(hasFocus){
				if(!"".equals(login_user_email.getText().toString())){
					login_user_email_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				login_user_email_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.login_user_password:
			if(hasFocus){
				if(!"".equals(login_user_password.getText().toString())){
					login_user_password_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				login_user_password_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_title_back:
			LoginActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.login_layout_to_register:
			Intent intent_register = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent_register);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		case R.id.login_account_qq:
			break;
		case R.id.login_account_sina:
			break;
		case R.id.login_user_email_iv_cancel:
			login_user_email.setText("");
			break;
		case R.id.login_user_password_iv_cancel:
			login_user_password.setText("");
			break;
		case R.id.login_login_lin:
			if(verifyLoginInformation()){
				common_progress_dialog_iv_rotate.startAnimation(anim_rotate);
				progressDialog.show();
				login();
			}
			break;
		case R.id.login_rel_forget_password:
			Intent intent_forget_password = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
			startActivity(intent_forget_password);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		}
	}
	
	/**
	 * user login
	 */
	public void login(){
		new Thread(){
			public void run() {
				String result = userService.login(login_user_email.getText().toString(), login_user_password.getText().toString());
				if("nocontent".equals(result)){
					Message msg = loginHandler.obtainMessage(LOGIN_FAIL);
					loginHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = loginHandler.obtainMessage(LOGIN_ERROR);
					loginHandler.sendMessage(msg);
				} else {
					Message msg = loginHandler.obtainMessage(LOGIN_SUCCESS);
					User user = userService.parseLoginJson(result);
					msg.obj = user;
					loginHandler.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * verify user input email or password
	 * @return true: verify success; false: verify fail
	 */
	public boolean verifyLoginInformation(){
		String regex = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(login_user_email.getText().toString());
		if("".equals(login_user_email.getText().toString().trim()) ){
			Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!matcher.matches() ){
			Toast.makeText(this, "请输入正确的邮箱格式", Toast.LENGTH_SHORT).show();
			return false;
		} else if("".equals(login_user_password.getText().toString())){
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * get header icon
	 */
	public void getHeaderIcon(final String url){
		new Thread(){
			public void run() {
				Bitmap bitmap = userService.getHeaderIcon(url);
				if(bitmap != null){
					Message msg = loginHandler.obtainMessage(LOGIN_ICON_SUCCESS);
					msg.obj = bitmap;
					loginHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler loginHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGIN_SUCCESS:
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
				User user = (User) msg.obj;
				storeUser(user);
				String url_icon = user.getIcon();
				if(!"default".equals(url_icon)){
					getHeaderIcon(user.getIcon());
				}
				progressDialog.dismiss();
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
				break;
			case LOGIN_FAIL:
				Toast.makeText(LoginActivity.this, "登录失败，请输入正确的邮箱或密码", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case LOGIN_ERROR:
				Toast.makeText(LoginActivity.this, "登录异常", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case LOGIN_ICON_SUCCESS:
				Bitmap bitmap = (Bitmap) msg.obj;
				userService.storeImage(bitmap);
				//sendBroadcast(intent);
				break;
			}
			
		};
	};
	
	public void storeUser(User user){
		spUtil.storeUserid(user.getUserid());
		spUtil.storeNickname(user.getNickname());
		spUtil.storeToken(user.getToken());
		spUtil.storeIcon(user.getIcon());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			LoginActivity.this.finish();
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
					LoginActivity.this.finish();
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
