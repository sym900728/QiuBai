package com.bt.qiubai;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.qiubai.service.UserService;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.NetworkUtil;
import com.qiubai.util.SharedPreferencesUtil;

public class PersonActivity extends Activity implements OnClickListener, OnTouchListener, OnFocusChangeListener{

	private RelativeLayout person_title_back, person_rel_nickname, person_rel_header_icon, person_rel_password,
		person_dialog_nickname_rel_cancel, person_dialog_nickname_rel_confirm;
	private RelativeLayout person_dialog_password_rel_cancel, person_dialog_password_rel_confirm;
	private ScrollView person_scroll;
	private LinearLayout person_lin_logout;
	private ImageView person_dialog_nickname_iv_cancel, person_dialog_password_origin_iv_cancel,
		person_dialog_password_new_iv_cancel, person_dialog_password_repeat_iv_cancel, 
		person_dialog_icon_photo_iv_selector, person_dialog_icon_pic_iv_selector,
		person_iv_icon;
	private EditText person_dialog_nickname_et, person_dialog_password_origin_et, person_dialog_password_new_et, person_dialog_password_repeat_et;
	private TextView person_tv_account, person_tv_nickname;
	
	private Dialog personNicknameDialog, personPasswordDialog, personIconDialog;
	private GestureDetector gestureDetector;
	private UserService userService;
	private SharedPreferencesUtil spUtil = new SharedPreferencesUtil(PersonActivity.this);
	
	private final static int PERSON_CHANGE_NICKNAME_SUCCESS = 1;
	private final static int PERSON_CHANGE_NICKNAME_FAIL = 2;
	private final static int PERSON_CHANGE_NICKNAME_ERROR = 3;
	private final static int PERSON_CHANGE_PASSWORD_SUCCESS = 4;
	private final static int PERSON_CHANGE_PASSWORD_FAIL = 5;
	private final static int PERSON_CHANGE_PASSWORD_ERROR = 6;
	private final static int PERSON_CHANGE_ICON_SUCCESS = 7;
	private final static int PERSON_CHANGE_ICON_FAIL = 8;
	private final static int PERSON_CHANGE_ICON_ERROR = 9;
	private final static int PERSON_REQUEST_CODE_CAMERA = 1;
	private final static int PERSON_REQUEST_CODE_CROP = 2;
	private final static int PERSON_REQUEST_CODE_PHOTO = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.person_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.person_title);
		
		userService = new UserService(PersonActivity.this);
		
		person_scroll = (ScrollView) findViewById(R.id.person_scroll);
		gestureDetector = new GestureDetector(PersonActivity.this,onGestureListener);
		person_scroll.setOnTouchListener(this);
		person_tv_account = (TextView) findViewById(R.id.person_tv_account);
		person_tv_account.setText(spUtil.getUserid());
		person_tv_nickname = (TextView) findViewById(R.id.person_tv_nickname);
		person_tv_nickname.setText(spUtil.getNickname());
		person_iv_icon = (ImageView) findViewById(R.id.person_iv_icon);
		Bitmap bitmap_header_icon = BitmapFactory.decodeFile("/data/data/com.bt.qiubai/userinfo/header_icon.png");
		if(bitmap_header_icon != null){
			person_iv_icon.setImageBitmap(BitmapUtil.circleBitmap(bitmap_header_icon));
		}
		
		personNicknameDialog = new Dialog(PersonActivity.this, R.style.CommonDialog);
		personNicknameDialog.setContentView(R.layout.person_dialog_nickname);
		person_dialog_nickname_rel_cancel = (RelativeLayout) personNicknameDialog.findViewById(R.id.person_dialog_nickname_rel_cancel);
		person_dialog_nickname_rel_cancel.setOnClickListener(this);
		person_dialog_nickname_rel_confirm = (RelativeLayout) personNicknameDialog.findViewById(R.id.person_dialog_nickname_rel_confirm);
		person_dialog_nickname_rel_confirm.setOnClickListener(this);
		person_dialog_nickname_iv_cancel = (ImageView) personNicknameDialog.findViewById(R.id.person_dialog_nickname_iv_cancel);
		person_dialog_nickname_iv_cancel.setOnClickListener(this);
		person_dialog_nickname_et = (EditText) personNicknameDialog.findViewById(R.id.person_dialog_nickname_et);
		person_dialog_nickname_et.setOnFocusChangeListener(this);
		person_dialog_nickname_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(s.toString())){
					person_dialog_nickname_iv_cancel.setVisibility(View.INVISIBLE);
				} else {
					person_dialog_nickname_iv_cancel.setVisibility(View.VISIBLE);
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
		
		personPasswordDialog = new Dialog(PersonActivity.this, R.style.CommonDialog);
		personPasswordDialog.setContentView(R.layout.person_dialog_password);
		person_dialog_password_rel_cancel = (RelativeLayout) personPasswordDialog.findViewById(R.id.person_dialog_password_rel_cancel);
		person_dialog_password_rel_cancel.setOnClickListener(this);
		person_dialog_password_rel_confirm = (RelativeLayout) personPasswordDialog.findViewById(R.id.person_dialog_password_rel_confirm);
		person_dialog_password_rel_confirm.setOnClickListener(this);
		person_dialog_password_origin_iv_cancel = (ImageView) personPasswordDialog.findViewById(R.id.person_dialog_password_origin_iv_cancel);
		person_dialog_password_origin_iv_cancel.setOnClickListener(this);
		person_dialog_password_origin_et = (EditText) personPasswordDialog.findViewById(R.id.person_dialog_password_origin_et);
		person_dialog_password_origin_et.setOnFocusChangeListener(this);
		person_dialog_password_origin_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(s.toString())){
					person_dialog_password_origin_iv_cancel.setVisibility(View.INVISIBLE);
				} else {
					person_dialog_password_origin_iv_cancel.setVisibility(View.VISIBLE);
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
		person_dialog_password_new_iv_cancel = (ImageView) personPasswordDialog.findViewById(R.id.person_dialog_password_new_iv_cancel);
		person_dialog_password_new_iv_cancel.setOnClickListener(this);
		person_dialog_password_new_et = (EditText) personPasswordDialog.findViewById(R.id.person_dialog_password_new_et);
		person_dialog_password_new_et.setOnFocusChangeListener(this);
		person_dialog_password_new_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(s.toString())){
					person_dialog_password_new_iv_cancel.setVisibility(View.INVISIBLE);
				} else {
					person_dialog_password_new_iv_cancel.setVisibility(View.VISIBLE);
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
		person_dialog_password_repeat_iv_cancel = (ImageView) personPasswordDialog.findViewById(R.id.person_dialog_password_repeat_iv_cancel);
		person_dialog_password_repeat_iv_cancel.setOnClickListener(this);
		person_dialog_password_repeat_et = (EditText) personPasswordDialog.findViewById(R.id.person_dialog_password_repeat_et);
		person_dialog_password_repeat_et.setOnFocusChangeListener(this);
		person_dialog_password_repeat_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(s.toString())){
					person_dialog_password_repeat_iv_cancel.setVisibility(View.INVISIBLE);
				} else {
					person_dialog_password_repeat_iv_cancel.setVisibility(View.VISIBLE);
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
		
		personIconDialog = new Dialog(PersonActivity.this, R.style.CommonDialog);
		personIconDialog.setContentView(R.layout.person_dialog_icon);
		person_dialog_icon_photo_iv_selector = (ImageView) personIconDialog.findViewById(R.id.person_dialog_icon_photo_iv_selector);
		person_dialog_icon_photo_iv_selector.setOnClickListener(this);
		person_dialog_icon_pic_iv_selector = (ImageView) personIconDialog.findViewById(R.id.person_dialog_icon_pic_iv_selector);
		person_dialog_icon_pic_iv_selector.setOnClickListener(this);
		
		person_title_back = (RelativeLayout) findViewById(R.id.person_title_back);
		person_title_back.setOnClickListener(this);
		person_lin_logout = (LinearLayout) findViewById(R.id.person_lin_logout);
		person_lin_logout.setOnClickListener(this);
		person_rel_nickname = (RelativeLayout) findViewById(R.id.person_rel_nickname);
		person_rel_nickname.setOnClickListener(this);
		person_rel_header_icon = (RelativeLayout) findViewById(R.id.person_rel_header_icon);
		person_rel_header_icon.setOnClickListener(this);
		person_rel_password = (RelativeLayout) findViewById(R.id.person_rel_password);
		person_rel_password.setOnClickListener(this);
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.person_dialog_nickname_et:
			if(hasFocus){
				if(!"".equals(person_dialog_nickname_et.getText().toString())){
					person_dialog_nickname_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				person_dialog_nickname_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.person_dialog_password_origin_et:
			if(hasFocus){
				if(!"".equals(person_dialog_password_origin_et.getText().toString())){
					person_dialog_password_origin_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				person_dialog_password_origin_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.person_dialog_password_new_et:
			if(hasFocus){
				if(!"".equals(person_dialog_password_new_et.getText().toString())){
					person_dialog_password_new_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				person_dialog_password_new_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.person_dialog_password_repeat_et:
			if(hasFocus){
				if(!"".equals(person_dialog_password_repeat_et.getText().toString())){
					person_dialog_password_repeat_iv_cancel.setVisibility(View.VISIBLE);
				}
			} else {
				person_dialog_password_repeat_iv_cancel.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.person_title_back:
			PersonActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.person_lin_logout:
			userService.logout(this);
			PersonActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.person_rel_nickname:
			person_dialog_nickname_et.setText(person_tv_nickname.getText().toString());
			personNicknameDialog.show();
			break;
		case R.id.person_rel_header_icon:
			personIconDialog.show();
			break;
		case R.id.person_rel_password:
			personPasswordDialog.show();
			break;
		case R.id.person_dialog_nickname_rel_cancel:
			personNicknameDialog.dismiss();
			break;
		case R.id.person_dialog_nickname_rel_confirm:
			if(verifyNickname()){
				changeNickname();
			}
			personNicknameDialog.dismiss();
			break;
		case R.id.person_dialog_nickname_iv_cancel:
			person_dialog_nickname_et.setText("");
			break;
		case R.id.person_dialog_password_rel_cancel:
			person_dialog_password_origin_et.setText("");
			person_dialog_password_new_et.setText("");
			person_dialog_password_repeat_et.setText("");
			personPasswordDialog.dismiss();
			break;
		case R.id.person_dialog_password_rel_confirm:
			if(verifyPassword()){
				changePassword();
			}
			break;
		case R.id.person_dialog_password_origin_iv_cancel:
			person_dialog_password_origin_et.setText("");
			break;
		case R.id.person_dialog_password_new_iv_cancel:
			person_dialog_password_new_et.setText("");
			break;
		case R.id.person_dialog_password_repeat_iv_cancel:
			person_dialog_password_repeat_et.setText("");
			break;
		case R.id.person_dialog_icon_photo_iv_selector:
			Intent intent_to_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent_to_camera, PERSON_REQUEST_CODE_CAMERA);
			break;
		case R.id.person_dialog_icon_pic_iv_selector:
			Intent intent_to_photo = new Intent(Intent.ACTION_GET_CONTENT);
			//intent_to_photo.addCategory(Intent.CATEGORY_OPENABLE);
			intent_to_photo.setType("image/*");
			startActivityForResult(intent_to_photo, PERSON_REQUEST_CODE_PHOTO);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PERSON_REQUEST_CODE_CAMERA:
			if(resultCode == RESULT_OK){
				if(data != null){
					//System.out.println(data);
					startPhotoZoom(data.getData());
				}
			} else if (resultCode == RESULT_CANCELED){
				//System.out.println("cancel");
			}
			break;
		case PERSON_REQUEST_CODE_CROP:
			if(resultCode == RESULT_OK){
				Bitmap bitmap = data.getParcelableExtra("data");
				userService.storeImage(bitmap);
				Bitmap bitmap2 = BitmapFactory.decodeFile("/data/data/com.bt.qiubai/userinfo/header_icon.png");
				person_iv_icon.setImageBitmap(BitmapUtil.circleBitmap(bitmap2));
				changeHeaderIcon();
			} else if (resultCode == RESULT_CANCELED){
				
			}
		case PERSON_REQUEST_CODE_PHOTO:
			if(resultCode == RESULT_OK){
				startPhotoZoom(data.getData());
			} else if(resultCode == RESULT_CANCELED){}
			break;
		}
	}
	
	/**
	 * crop picture
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", "PNG");
		startActivityForResult(intent, PERSON_REQUEST_CODE_CROP);
	}
	
	/**
	 * verify password
	 * @return
	 */
	public boolean verifyPassword(){
		String password_origin = person_dialog_password_origin_et.getText().toString(); 
		String password_new = person_dialog_password_new_et.getText().toString();
		String password_repeat = person_dialog_password_repeat_et.getText().toString();
		if("".equals(password_origin)){
			Toast.makeText(PersonActivity.this, "原密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if(password_origin.length() < 6 || password_origin.length() > 20){
			Toast.makeText(PersonActivity.this, "请输入6~20位字符的原密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if("".equals(password_new)){
			Toast.makeText(PersonActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if(password_new.length() < 6 || password_new.length() > 20){
			Toast.makeText(PersonActivity.this, "请输入6~20位字符的新密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if("".equals(password_repeat)){
			Toast.makeText(PersonActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if(password_repeat.length() < 6 || password_repeat.length() > 20){
			Toast.makeText(PersonActivity.this, "请输入6~20位字符的确认密码", Toast.LENGTH_SHORT).show();
			return false;
		} else if( !password_new.equals(password_repeat)){
			Toast.makeText(PersonActivity.this, "确认密码与新密码不一致", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * change header icon
	 */
	public void changeHeaderIcon(){
		new Thread(){
			@SuppressLint("SdCardPath")
			@Override
			public void run() {
				String token = spUtil.getToken();
				String userid = spUtil.getUserid();
				File file = new File("/data/data/com.bt.qiubai/userinfo/header_icon.png");
				String result = userService.uploadIcon(file, token, userid);
				if("success".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_ICON_SUCCESS);
					personHandler.sendMessage(msg);
				} else if("fail".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_ICON_FAIL);
					personHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_ICON_ERROR);
					personHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	/**
	 * change password
	 */
	public void changePassword(){
		new Thread(){
			@Override
			public void run() {
				String token = spUtil.getToken();
				String userid = spUtil.getUserid();
				String password_origin = person_dialog_password_origin_et.getText().toString();
				String password_new = person_dialog_password_new_et.getText().toString();
				String result = userService.changePassword(userid, token, password_origin, password_new);
				if("success".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_PASSWORD_SUCCESS);
					personHandler.sendMessage(msg);
				} else if("fail".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_PASSWORD_FAIL);
					personHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_PASSWORD_ERROR);
					personHandler.sendMessage(msg);
				}
			}
		}.start();
	}
	
	/**
	 * modify nickname
	 */
	public void changeNickname(){
		new Thread(){
			public void run() {
				String token = spUtil.getToken();
				String userid = spUtil.getUserid();
				String nickname = person_dialog_nickname_et.getText().toString().trim();
				String result = userService.changeNickname(userid, token, nickname);
				if("success".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_NICKNAME_SUCCESS);
					personHandler.sendMessage(msg);
				} else if("fail".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_NICKNAME_FAIL);
					personHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = personHandler.obtainMessage(PERSON_CHANGE_NICKNAME_ERROR);
					personHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	/**
	 * verify nickname string
	 * @return
	 */
	public boolean verifyNickname(){
		String nickname = person_dialog_nickname_et.getText().toString();
		if("".equals(nickname)){
			Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if (nickname.length() < 3 || nickname.length() > 10){
			Toast.makeText(this, "请输入3~10位字符的昵称", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@SuppressLint("HandlerLeak")
	private Handler personHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PERSON_CHANGE_NICKNAME_SUCCESS:
				Toast.makeText(PersonActivity.this, "昵称修改成功", Toast.LENGTH_SHORT).show();
				String nickname = person_dialog_nickname_et.getText().toString().trim();
				spUtil.storeNickname(nickname);
				person_tv_nickname.setText(nickname);
				break;
			case PERSON_CHANGE_NICKNAME_FAIL:
				Toast.makeText(PersonActivity.this, "昵称修改失败", Toast.LENGTH_SHORT).show();
				break;
			case PERSON_CHANGE_NICKNAME_ERROR:
				Toast.makeText(PersonActivity.this, "昵称修改异常", Toast.LENGTH_SHORT).show();
				break;
			case PERSON_CHANGE_PASSWORD_SUCCESS:
				Toast.makeText(PersonActivity.this, "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
				personPasswordDialog.dismiss();
				userService.logout(PersonActivity.this);
				PersonActivity.this.finish();
				overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
				break;
			case PERSON_CHANGE_PASSWORD_FAIL:
				Toast.makeText(PersonActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
				break;
			case PERSON_CHANGE_PASSWORD_ERROR:
				Toast.makeText(PersonActivity.this, "密码修改异常", Toast.LENGTH_SHORT).show();
				break;
			case PERSON_CHANGE_ICON_SUCCESS:
				Toast.makeText(PersonActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
				break;
			case PERSON_CHANGE_ICON_FAIL:
				Toast.makeText(PersonActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
				break;
			case PERSON_CHANGE_ICON_ERROR:
				Toast.makeText(PersonActivity.this, "头像上传异常", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			PersonActivity.this.finish();
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
					PersonActivity.this.finish();
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
