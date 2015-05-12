package com.bt.qiubai;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiubai.entity.Weather;
import com.qiubai.fragment.PictureFragment;
import com.qiubai.service.CityService;
import com.qiubai.service.UserService;
import com.qiubai.service.WeatherService;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.DateUtil;
import com.qiubai.util.DensityUtil;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.util.WeatherKeyUtil;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private RelativeLayout main_title_reL_menu, rel_main_right, main_title_rel_person, 
		main_viewpager_title_rel_picture, main_viewpager_title_rel_joke, 
		main_viewpager_title_rel_novel, main_viewpager_title_rel_diy;
	private RelativeLayout main_drawer_right, main_drawer_left, main_drawer_left_rel_hot, 
		main_drawer_left_rel_character, main_drawer_left_rel_picture;
	private LinearLayout main_drawer_right_lin_comment, main_drawer_right_lin_collect, lin_weather, lin_setting;
	private ImageView main_viewpager_title_iv_picture, main_viewpager_title_iv_joke, main_viewpager_title_iv_novel,
		main_viewpager_title_iv_diy, main_drawer_right_iv_avatar, title_menu_avator;
	private TextView main_viewpager_title_tv_picture, main_viewpager_title_tv_joke, main_viewpager_title_tv_novel,
		main_viewpager_title_tv_diy, main_drawer_right_tv_nickname, text_weather;
	private DrawerLayout main_drawer;
	private ViewPager main_viewpager;
	
	private int screenWidth, screenHeight;
	private Bitmap bitmap_underline;
	private Dialog rightDialog;
	private List<Fragment> list_fragments = new ArrayList<Fragment>();
	private MainFragmentPagerAdapter mainFragmentPagerAdpater;
	
	private WeatherService weatherService;
	private CityService cityService;
	private SharedPreferencesUtil spUtil = new SharedPreferencesUtil(MainActivity.this);
	private UserService userService = new UserService();
	
	private boolean isMainDrawerLeftOpen = false, isMainDrawerRightOpen = false;
	private boolean isMainDrawerRightSet = false;
	private final static int WEATHER = 1; 
	private final static int EXIT = 2; 
	private static boolean isExit = false; // 定义变量，判断是否退出
	
	private String cityCode, public_key, private_key, key, getUrl;
	private Weather weather;
	private List<Weather> listWeathers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);
		
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		bitmap_underline = BitmapUtil.resizeBitmapFillBox(screenWidth/4, DensityUtil.dip2px(this, 2.5f), BitmapFactory.decodeResource(getResources(), R.drawable.main_viewpager_title_underline));
		
		title_menu_avator = (ImageView) findViewById(R.id.title_menu_avator);
		main_title_reL_menu = (RelativeLayout) findViewById(R.id.main_title_reL_menu);
		main_title_reL_menu.setOnClickListener(this);
		rel_main_right = (RelativeLayout) findViewById(R.id.rel_main_title_right);
		rel_main_right.setOnClickListener(this);
		main_title_rel_person = (RelativeLayout) findViewById(R.id.main_title_rel_person);
		main_title_rel_person.setOnClickListener(this);
		main_viewpager_title_rel_picture = (RelativeLayout) findViewById(R.id.main_viewpager_title_rel_picture);
		main_viewpager_title_rel_picture.setOnClickListener(this);
		main_viewpager_title_rel_joke = (RelativeLayout) findViewById(R.id.main_viewpager_title_rel_joke);
		main_viewpager_title_rel_joke.setOnClickListener(this);
		main_viewpager_title_rel_novel = (RelativeLayout) findViewById(R.id.main_viewpager_title_rel_novel);
		main_viewpager_title_rel_novel.setOnClickListener(this);
		main_viewpager_title_rel_diy = (RelativeLayout) findViewById(R.id.main_viewpager_title_rel_diy);
		main_viewpager_title_rel_diy.setOnClickListener(this);
		main_viewpager_title_iv_picture = (ImageView) findViewById(R.id.main_viewpager_title_iv_picture);
		main_viewpager_title_iv_joke = (ImageView) findViewById(R.id.main_viewpager_title_iv_joke);
		main_viewpager_title_iv_novel = (ImageView) findViewById(R.id.main_viewpager_title_iv_novel);
		main_viewpager_title_iv_diy = (ImageView) findViewById(R.id.main_viewpager_title_iv_diy);
		main_viewpager_title_tv_picture = (TextView) findViewById(R.id.main_viewpager_title_tv_picture);
		main_viewpager_title_tv_joke = (TextView) findViewById(R.id.main_viewpager_title_tv_joke);
		main_viewpager_title_tv_novel = (TextView) findViewById(R.id.main_viewpager_title_tv_novel);
		main_viewpager_title_tv_diy = (TextView) findViewById(R.id.main_viewpager_title_tv_diy);
		
		initTitleDialog(); //加载titlebar的dialog控件
		main_drawer = (DrawerLayout) findViewById(R.id.main_drawer);
		main_drawer.setDrawerListener(new MainDrawerListener());
		main_drawer_right = (RelativeLayout) findViewById(R.id.main_drawer_right);
		DrawerLayout.LayoutParams main_drawer_right_params = (android.support.v4.widget.DrawerLayout.LayoutParams) main_drawer_right.getLayoutParams();
		main_drawer_right_params.width = screenWidth/ 5 * 4;
		main_drawer_right.setLayoutParams(main_drawer_right_params);
		main_drawer_right_iv_avatar = (ImageView) findViewById(R.id.main_drawer_right_iv_avatar);
		main_drawer_right_iv_avatar.setOnClickListener(this);
		main_drawer_right_tv_nickname = (TextView) findViewById(R.id.main_drawer_right_tv_nickname);
		main_drawer_right_lin_comment = (LinearLayout) findViewById(R.id.main_drawer_right_lin_comment);
		main_drawer_right_lin_comment.setOnClickListener(this);
		main_drawer_right_lin_collect = (LinearLayout) findViewById(R.id.main_drawer_right_lin_collect);
		main_drawer_right_lin_collect.setOnClickListener(this);
		main_drawer_left = (RelativeLayout) findViewById(R.id.main_drawer_left);
		DrawerLayout.LayoutParams main_drawer_left_params =  (android.support.v4.widget.DrawerLayout.LayoutParams) main_drawer_left.getLayoutParams();
		main_drawer_left_params.width = screenWidth / 5 * 3;
		main_drawer_left.setLayoutParams(main_drawer_left_params);
		main_drawer_left_rel_hot = (RelativeLayout) findViewById(R.id.main_drawer_left_rel_hot);
		main_drawer_left_rel_hot.setOnClickListener(this);
		main_drawer_left_rel_character = (RelativeLayout) findViewById(R.id.main_drawer_left_rel_character);
		main_drawer_left_rel_character.setOnClickListener(this);
		main_drawer_left_rel_picture = (RelativeLayout) findViewById(R.id.main_drawer_left_rel_picture);
		main_drawer_left_rel_picture.setOnClickListener(this);
		
		main_viewpager = (ViewPager) findViewById(R.id.main_viewpager);
		initFragment();
		mainFragmentPagerAdpater = new MainFragmentPagerAdapter(getSupportFragmentManager());
		main_viewpager.setAdapter(mainFragmentPagerAdpater);
		main_viewpager.setCurrentItem(0);
		setViewpagerTitleTextColor(0);
		main_viewpager.setOffscreenPageLimit(3);//set buffer memory figure out pause
		main_viewpager.setOnPageChangeListener(new MainOnPageChangeListener());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(userService.checkUserLogin(MainActivity.this)){
			Bitmap bitmap = BitmapFactory.decodeFile(ReadPropertiesUtil.read("config", "header_icon_path"));
			if(bitmap == null){
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_drawer_right_person_avatar);
				title_menu_avator.setImageBitmap(bitmap);
			} else {
				title_menu_avator.setImageBitmap(BitmapUtil.circleBitmap(bitmap));
			}
		} else {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_drawer_right_person_avatar);
			title_menu_avator.setImageBitmap(bitmap);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_title_reL_menu:
			if(isMainDrawerRightOpen){
				main_drawer.closeDrawer(main_drawer_right);
			} else if(isMainDrawerLeftOpen){
				main_drawer.closeDrawer(main_drawer_left);
			} else {
				main_drawer.openDrawer(main_drawer_left);
			}
			break;
		case R.id.main_title_rel_person:
			if(isMainDrawerLeftOpen){
				main_drawer.closeDrawer(main_drawer_left);
			} else if(isMainDrawerRightOpen){
				main_drawer.closeDrawer(main_drawer_right);
			} else {
				main_drawer.openDrawer(main_drawer_right);
			}
			break;
		case R.id.main_drawer_right_iv_avatar:
			main_drawer.closeDrawer(main_drawer_right);
			if(userService.checkUserLogin(MainActivity.this)){
				Intent intent_person = new Intent(MainActivity.this, PersonActivity.class);
				startActivity(intent_person);
				overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			} else {
				Intent intent_login = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent_login);
				overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			}
			break;
		case R.id.rel_main_title_right:
			rightDialog.show();
			SharedPreferences share = getSharedPreferences(CityActivity.SHAREDPREFERENCES_FIRSTENTER, MODE_PRIVATE);
			String city = share.getString(CityActivity.CityActivity_CityTown, "常州");
			new WeatherInfo().execute(city);
			//initWeather(city);
			// 点击右边的按钮响应事件
			// 跳转到detail activity
			//Intent intent = new Intent(MainActivity.this, CharacterDetailActivity.class);
			//startActivity(intent);
			break;
		case R.id.main_viewpager_title_rel_picture:
			main_viewpager.setCurrentItem(0);
			break;
		case R.id.main_viewpager_title_rel_joke:
			main_viewpager.setCurrentItem(1);
			break;
		case R.id.main_viewpager_title_rel_novel:
			main_viewpager.setCurrentItem(2);
			break;
		case R.id.main_viewpager_title_rel_diy:
			main_viewpager.setCurrentItem(3);
			break;
		case R.id.main_drawer_left_rel_hot:
			main_viewpager.setCurrentItem(0);
			main_drawer.closeDrawer(main_drawer_left);
			break;
		case R.id.main_drawer_left_rel_character:
			main_viewpager.setCurrentItem(1);
			main_drawer.closeDrawer(main_drawer_left);
			break;
		case R.id.main_drawer_left_rel_picture:
			main_viewpager.setCurrentItem(2);
			main_drawer.closeDrawer(main_drawer_left);
			break;
		case R.id.main_drawer_right_lin_comment:
			if(userService.checkUserLogin(MainActivity.this)){
				
			} else {
				Toast.makeText(MainActivity.this, "您还未登录，请登录", Toast.LENGTH_SHORT).show();
				main_drawer.closeDrawer(main_drawer_right);
				Intent intent_to_login = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent_to_login);
				overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			}
			break;
		case R.id.main_drawer_right_lin_collect:
			break;
		case R.id.main_menu_action_weather_lin:
			//点击天气
			rightDialog.dismiss();
			Intent intent_weather = new Intent(MainActivity.this, WeatherActivity.class);
			startActivity(intent_weather);
			//Toast.makeText(MainActivity.this, "今天天气晴朗", Toast.LENGTH_SHORT).show();
			break;
		case R.id.main_menu_action_setting_lin:
			//点击设置
			rightDialog.dismiss();
			Toast.makeText(MainActivity.this, "点击设置，准备跳转", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	
	
	/**
	 * main DrawerListener
	 */
	private class MainDrawerListener implements DrawerListener{

		@Override
		public void onDrawerClosed(View view) {
			if(view == main_drawer_right){
				isMainDrawerRightOpen = false;
				isMainDrawerRightSet = false;
			} else if(view == main_drawer_left){
				isMainDrawerLeftOpen = false;
			}
		}

		@Override
		public void onDrawerOpened(View view) {
			if(view == main_drawer_right){
				isMainDrawerRightOpen = true;
			} else if(view == main_drawer_left){
				isMainDrawerLeftOpen = true;
			}
		}

		@Override
		public void onDrawerSlide(View view, float distanceX) {
			if(view == main_drawer_right){
				if(!isMainDrawerRightSet){
					if(userService.checkUserLogin(MainActivity.this)){
						main_drawer_right_tv_nickname.setText(spUtil.getNickname());
						Bitmap bitmap = BitmapFactory.decodeFile(ReadPropertiesUtil.read("config", "header_icon_path"));
						if(bitmap == null){
							bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_drawer_right_person_avatar);
							main_drawer_right_iv_avatar.setImageBitmap(bitmap);
						} else {
							main_drawer_right_iv_avatar.setImageBitmap(BitmapUtil.circleBitmap(bitmap));
						}
					} else {
						Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_drawer_right_person_avatar);
						main_drawer_right_iv_avatar.setImageBitmap(bitmap);
						main_drawer_right_tv_nickname.setText("立即登录");
					}
					isMainDrawerRightSet = true;
				}
			} else if(view == main_drawer_left){
				
			}
		}

		@Override
		public void onDrawerStateChanged(int arg0) {}
	}
	
	/**
	 * main OnPageChangeListener
	 */
	private class MainOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int item) {}

		@Override
		public void onPageScrolled(int item, float arg1, int distance) {
			//System.out.println("item" + item + "   distance:" + distance);
			if(item == 0){
				translateUnderline( (float)distance/4, bitmap_underline, main_viewpager_title_iv_picture);
				translateUnderline(- (float)screenWidth/4 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_joke);
				translateUnderline(- (float)screenWidth/4 * 2 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_novel);
				translateUnderline(- (float)screenWidth/4 * 3 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_diy);
			} else if(item == 1){
				translateUnderline( (float)screenWidth/4 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_picture);
				translateUnderline( (float)distance/4, bitmap_underline, main_viewpager_title_iv_joke);
				translateUnderline(- (float)screenWidth/4 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_novel);
				translateUnderline(- (float)screenWidth/4 * 2 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_diy);
			} else if(item == 2){
				translateUnderline( (float)screenWidth/4 * 2 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_picture);
				translateUnderline( (float)screenWidth/4 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_joke);
				translateUnderline( (float)distance/4, bitmap_underline, main_viewpager_title_iv_novel);
				translateUnderline(- (float)screenWidth/4 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_diy);
			} else if(item == 3){
				translateUnderline( (float)screenWidth/4 * 3 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_picture);
				translateUnderline( (float)screenWidth/4 * 2 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_joke);
				translateUnderline( (float)screenWidth/4 + (float)distance/4, bitmap_underline, main_viewpager_title_iv_novel);
				translateUnderline( (float)distance/4, bitmap_underline, main_viewpager_title_iv_diy);
			}
		}

		@Override
		public void onPageSelected(int item) {
			setViewpagerTitleTextColor(item);
		}
		
	}

	/**
	 * initialize title dialog
	 */
	private void initTitleDialog() {
		rightDialog = new Dialog(MainActivity.this, R.style.CommonActionDialog);
		rightDialog.setContentView(R.layout.main_menu_action_bar);
		rightDialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		lin_weather = (LinearLayout) rightDialog.findViewById(R.id.main_menu_action_weather_lin);
		lin_setting = (LinearLayout) rightDialog.findViewById(R.id.main_menu_action_setting_lin);
		text_weather = (TextView) rightDialog.findViewById(R.id.main_menu_action_weather);
		lin_weather.setOnClickListener(this);
		lin_setting.setOnClickListener(this);
	}
	
	/**
	 * initialize fragment
	 */
	private void initFragment() {
		PictureFragment picturesFragment = new PictureFragment();
		JokesFragment jokesFragment = new JokesFragment();
		NovelsFragment novelsFragment = new NovelsFragment();
		DiyFragment diyFragment = new DiyFragment();
		
		list_fragments.add(picturesFragment);
		list_fragments.add(jokesFragment);
		list_fragments.add(novelsFragment);
		list_fragments.add(diyFragment);
	}

	private class MainFragmentPagerAdapter extends FragmentPagerAdapter{

		public MainFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int item) {
			return list_fragments.get(item);
		}

		@Override
		public int getCount() {
			return list_fragments.size();
		}
	}
	
	/**
	 * set viewpager title text color
	 * @param item
	 */
	private void setViewpagerTitleTextColor(int item){
		if(item == 0){
			main_viewpager_title_tv_picture.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_active_color));
			main_viewpager_title_tv_joke.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_novel.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_diy.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
		} else if(item == 1){
			main_viewpager_title_tv_picture.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_joke.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_active_color));
			main_viewpager_title_tv_novel.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_diy.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
		} else if(item == 2){
			main_viewpager_title_tv_picture.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_joke.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_novel.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_active_color));
			main_viewpager_title_tv_diy.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
		} else if(item == 3){
			main_viewpager_title_tv_picture.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_joke.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_novel.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_inactive_color));
			main_viewpager_title_tv_diy.setTextColor(getResources().getColor(R.color.main_viewpager_title_tv_active_color));
		}
	}
	
	/**
	 * translate viewpager title underline
	 * @param distanceX
	 * @param bitmap
	 * @param imageview
	 */
	private void translateUnderline(float distanceX, Bitmap bitmap, ImageView imageview){
		Bitmap alterBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(alterBitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		Matrix matrix = new Matrix();
		matrix.setTranslate(distanceX, 0.0f);
		canvas.drawBitmap(bitmap, matrix, paint);
		imageview.setImageBitmap(alterBitmap);		
	}
	
	
	/**
	 * @author Tim
	 * 天气
	 *
	 */
	private class WeatherInfo extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			weatherService = new WeatherService();
			listWeathers = new ArrayList<Weather>();
			cityCode = weatherService.getCityByName(params[0],
					getApplicationContext());
			public_key = WeatherKeyUtil.JointPublicUrl(cityCode);
			private_key = ReadPropertiesUtil.read("weather", "PRIVATE");

			key = WeatherKeyUtil.standardURLEncoder(public_key, private_key);
			getUrl = WeatherKeyUtil.JointUrl(cityCode, key);
			String result = HttpUtil.doGet(getUrl);
//			Log.d(TAG, result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			try {
				JSONObject jsonObject = new JSONObject(result);

				JSONArray fs = jsonObject.getJSONObject("f").getJSONArray("f1");
				for (int i = 0; i < fs.length(); i++) {
					JSONObject f = (JSONObject) fs.opt(i);
					weather = new Weather();

					String dayTemperature = f.getString("fc");// 27
					String nightTemperature = f.getString("fd");// 15
					weather.setDayTemperature(dayTemperature);
					weather.setNightTemperature(nightTemperature);

					listWeathers.add(weather);
				}
				String temp = null;
				if (DateUtil.getCurrentHourMinute() >= 1800
						&& DateUtil.getCurrentHourMinute() <= 2400) {

					String dayTemp = listWeathers.get(1).getDayTemperature();
					String nightTemp = listWeathers.get(1)
							.getNightTemperature();
					temp = dayTemp + "°/" + nightTemp + "°";
				} else {
					String dayTemp = listWeathers.get(0).getDayTemperature();
					String nightTemp = listWeathers.get(0)
							.getNightTemperature();
					temp = dayTemp + "°/" + nightTemp + "°";
				}

				text_weather.setText(temp);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitApplication();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};

	private void exitApplication() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 利用Handler延迟2秒发送更改消息
			mainHandler.sendEmptyMessageDelayed(EXIT, 2000);
		} else {
			finish();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mainHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == EXIT) {
				isExit = false; // 退出程序
			} 
		}
	};

}
