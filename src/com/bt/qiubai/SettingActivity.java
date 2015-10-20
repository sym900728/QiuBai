package com.bt.qiubai;

import com.qiubai.util.SharedPreferencesUtil;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener, OnTouchListener{
	
	private RelativeLayout setting_title_back;
	private ScrollView setting_scroll;
	private RelativeLayout setting_rel_ip, setting_rel_port, setting_dialog_ip_rel_cancel,
		setting_dialog_ip_rel_confirm, setting_dialog_port_rel_cancel, setting_dialog_port_rel_confirm;
	private TextView setting_tv_ip, setting_tv_port;
	private EditText setting_dialog_ip_et, setting_dialog_port_et;
	private ImageView setting_dialog_ip_iv_cancel, setting_dialog_port_iv_cancel;
	
	private GestureDetector gestureDetector;
	private Dialog settingIpDialog, settingPortDialog;
	private SharedPreferencesUtil spUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.setting_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.setting_title);
		
		spUtil = new SharedPreferencesUtil(SettingActivity.this);
		
		setting_title_back = (RelativeLayout) findViewById(R.id.setting_title_back);
		setting_title_back.setOnClickListener(this);
		
		setting_scroll = (ScrollView) findViewById(R.id.setting_scroll);
		gestureDetector = new GestureDetector(SettingActivity.this,onGestureListener);
		setting_scroll.setOnTouchListener(this);
		
		setting_rel_ip = (RelativeLayout) findViewById(R.id.setting_rel_ip);
		setting_rel_ip.setOnClickListener(this);
		setting_rel_port = (RelativeLayout) findViewById(R.id.setting_rel_port);
		setting_rel_port.setOnClickListener(this);
		
		setting_tv_ip = (TextView) findViewById(R.id.setting_tv_ip);
		setting_tv_ip.setText(spUtil.getIp());
		setting_tv_port = (TextView) findViewById(R.id.setting_tv_port);
		setting_tv_port.setText(spUtil.getPort());
		
		settingIpDialog = new Dialog(SettingActivity.this, R.style.CommonDialog);
		settingIpDialog.setContentView(R.layout.setting_dialog_ip);
		setting_dialog_ip_rel_cancel = (RelativeLayout) settingIpDialog.findViewById(R.id.setting_dialog_ip_rel_cancel);
		setting_dialog_ip_rel_cancel.setOnClickListener(this);
		setting_dialog_ip_rel_confirm = (RelativeLayout) settingIpDialog.findViewById(R.id.setting_dialog_ip_rel_confirm);
		setting_dialog_ip_rel_confirm.setOnClickListener(this);
		setting_dialog_ip_iv_cancel = (ImageView) settingIpDialog.findViewById(R.id.setting_dialog_ip_iv_cancel);
		setting_dialog_ip_iv_cancel.setOnClickListener(this);
		setting_dialog_ip_et = (EditText) settingIpDialog.findViewById(R.id.setting_dialog_ip_et);
		setting_dialog_ip_et.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(!"".equals(setting_dialog_ip_et.getText().toString())){
						setting_dialog_ip_iv_cancel.setVisibility(View.VISIBLE);
					}
				} else {
					setting_dialog_ip_iv_cancel.setVisibility(View.INVISIBLE);
				}
			}
		});
		setting_dialog_ip_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(s.toString())){
					setting_dialog_ip_iv_cancel.setVisibility(View.INVISIBLE);
				} else {
					setting_dialog_ip_iv_cancel.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		settingPortDialog = new Dialog(SettingActivity.this, R.style.CommonDialog);
		settingPortDialog.setContentView(R.layout.setting_dialog_port);
		setting_dialog_port_rel_cancel = (RelativeLayout) settingPortDialog.findViewById(R.id.setting_dialog_port_rel_cancel);
		setting_dialog_port_rel_cancel.setOnClickListener(this);
		setting_dialog_port_rel_confirm = (RelativeLayout) settingPortDialog.findViewById(R.id.setting_dialog_port_rel_confirm);
		setting_dialog_port_rel_confirm.setOnClickListener(this);
		setting_dialog_port_iv_cancel = (ImageView) settingPortDialog.findViewById(R.id.setting_dialog_port_iv_cancel);
		setting_dialog_port_iv_cancel.setOnClickListener(this);
		setting_dialog_port_et = (EditText) settingPortDialog.findViewById(R.id.setting_dialog_port_et);
		setting_dialog_port_et.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(!"".equals(setting_dialog_port_et.getText().toString())){
						setting_dialog_port_iv_cancel.setVisibility(View.VISIBLE);
					}
				} else {
					setting_dialog_port_iv_cancel.setVisibility(View.INVISIBLE);
				}
			}
		});
		setting_dialog_port_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(s.toString())){
					setting_dialog_port_iv_cancel.setVisibility(View.INVISIBLE);
				} else {
					setting_dialog_port_iv_cancel.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_title_back:
			SettingActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.setting_rel_ip:
			setting_dialog_ip_et.setText(spUtil.getIp());
			settingIpDialog.show();
			break;
		case R.id.setting_rel_port:
			setting_dialog_port_et.setText(spUtil.getPort());
			settingPortDialog.show();
			break;
		case R.id.setting_dialog_ip_iv_cancel:
			setting_dialog_ip_et.setText("");
			break;
		case R.id.setting_dialog_ip_rel_cancel:
			setting_dialog_ip_et.setText("");
			settingIpDialog.dismiss();
			break;
		case R.id.setting_dialog_ip_rel_confirm:
			if(verifyIp()){
				changeIp();
			}
			settingIpDialog.dismiss();
			break;
		case R.id.setting_dialog_port_iv_cancel:
			setting_dialog_port_et.setText("");
			break;
		case R.id.setting_dialog_port_rel_cancel:
			setting_dialog_port_et.setText("");
			settingPortDialog.dismiss();
			break;
		case R.id.setting_dialog_port_rel_confirm:
			if(verifyPort()){
				changePort();
			}
			settingPortDialog.dismiss();
			break;
		}
	}
	
	public void changePort(){
		String port = setting_dialog_port_et.getText().toString().trim();
		spUtil.storePort(port);
		setting_tv_port.setText(port);
	}
	
	public boolean verifyPort(){
		String port = setting_dialog_port_et.getText().toString();
		if("".equals(port)){
			Toast.makeText(this, "port不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public void changeIp(){
		String ip = setting_dialog_ip_et.getText().toString().trim();
		spUtil.storeIp(ip);
		setting_tv_ip.setText(ip);
	}
	
	public boolean verifyIp(){
		String ip = setting_dialog_ip_et.getText().toString();
		if("".equals(ip)){
			Toast.makeText(this, "ip不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			SettingActivity.this.finish();
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
					SettingActivity.this.finish();
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
