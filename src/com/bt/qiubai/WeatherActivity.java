package com.bt.qiubai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qiubai.entity.Weather;
import com.qiubai.entity.WeatherPhenomena;
import com.qiubai.entity.WeatherWind;
import com.qiubai.entity.WeatherWindPower;
import com.qiubai.entity.Weekend;
import com.qiubai.service.WeatherService;
import com.qiubai.util.DateUtil;
import com.qiubai.util.HttpUtil;
import com.qiubai.util.ReadPropertiesUtil;
import com.qiubai.util.WeatherKeyUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	/**
	 * 返回按钮
	 */
	private RelativeLayout weather_back = null;
	/**
	 * 选择某个城市按钮
	 */
	private RelativeLayout weather_location_city = null;
	/**
	 * 分享天气按钮
	 */
	private RelativeLayout weather_share = null;

	/**
	 * 城市文本框
	 */
	private TextView weather_city;

	private TextView weather_degree;
	private TextView weather_detail;
	private TextView weather_index_detail;
	private TextView weather_publish;
	private ImageView weather_img_pheno;
	
	private ImageView weather_img_pheno_tomorrow;
	private TextView weather_weekend_tomorrow;
	private TextView weather_degree_tomorrow;
	private TextView weather_detail_tomorrow;
	
	private ImageView weather_img_pheno_after;
	private TextView weather_weekend_after;
	private TextView weather_degree_after;
	private TextView weather_detail_after;

	private WeatherService weatherService;

	public final static int WeatherToCity = 0;
	public final static int CityBackWeather = 1;

	private final static String TAG = "WeatherActivity";

	private String cityCode, public_key, private_key, key, getUrl;

	private String dayWeatherPhenomena;// 白天天气现象
	private String nightWeatherPhenomena;// 晚上天气现象
	private String dayTemperature;// 白天温度
	private String nightTemperature;// 晚上温度
	private String dayWind;// 白天分向
	private String nightWind;// 晚上分向
	private String dayWindPower;// 白天分力
	private String nightWindPower;// 晚上分力

	private Weather weather;
	private List<Weather> listWeathers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_main);

		initWeatherBar();
		initWeather();
		getCityFromSharePreference();

	}

	// weather:{"c":{"c1":"101191101","c2":"changzhou","c3":"常州","c4":"changzhou","c5":"常州","c6":"jiangsu","c7":"江苏","c8":"china","c9":"中国","c10":"2","c11":"0519","c12":"213000","c13":119.948000,"c14":31.766000,"c15":"8","c16":"AZ9513","c17":"+8"},
	// "f":{"f1":[
	// {"fa":"00","fb":"01","fc":"27","fd":"14","fe":"3","ff":"4","fg":"0","fh":"0","fi":"05:23|18:34"},
	// {"fa":"01","fb":"01","fc":"26","fd":"14","fe":"3","ff":"3","fg":"0","fh":"0","fi":"05:21|18:34"},
	// {"fa":"00","fb":"01","fc":"27","fd":"14","fe":"4","ff":"4","fg":"0","fh":"0","fi":"05:20|18:35"}],"f0":"201504231100"}}

	/**
	 * 加载天气组件栏的其他信息
	 */
	private void initWeather() {
		weather_degree = (TextView) findViewById(R.id.text_weather_degree);
		weather_detail = (TextView) findViewById(R.id.weather_detail);
		weather_index_detail = (TextView) findViewById(R.id.weather_index);
		weather_img_pheno = (ImageView) findViewById(R.id.img_weather);
		weather_publish = (TextView) findViewById(R.id.weather_publish);
		
		
		weather_img_pheno_tomorrow = (ImageView) findViewById(R.id.img_tomorrow_weather);
		weather_weekend_tomorrow = (TextView) findViewById(R.id.text_tomorrow_weather_weekend);
		weather_degree_tomorrow = (TextView) findViewById(R.id.text_tomorrow_weather_degree);
		weather_detail_tomorrow = (TextView) findViewById(R.id.text_tomorrow_weather_detail);
	
		weather_img_pheno_after = (ImageView) findViewById(R.id.img_after_weather);
		weather_weekend_after = (TextView) findViewById(R.id.text_after_weather_weekend);
		weather_degree_after = (TextView) findViewById(R.id.text_after_weather_degree);
		weather_detail_after = (TextView) findViewById(R.id.text_after_weather_detail);
		
		
	}

	/**
	 * 从本地的sharepreferences中读取城市列表名称
	 */
	private void getCityFromSharePreference() {
		SharedPreferences share = getSharedPreferences(
				CityActivity.SHAREDPREFERENCES_FIRSTENTER, MODE_PRIVATE);
		String city_town = share.getString(CityActivity.CityActivity_CityTown,
				"常州");
		// getWeather(city_town);
		new WeatherInfo().execute(city_town);
		weather_city.setText(city_town);
	}

	/**
	 * 加载天气组件栏的titlebar
	 */
	private void initWeatherBar() {
		weather_back = (RelativeLayout) findViewById(R.id.rel_weather_title_left);
		weather_location_city = (RelativeLayout) findViewById(R.id.rel_weather_title_location);
		weather_share = (RelativeLayout) findViewById(R.id.rel_weather_title_share);
		weather_city = (TextView) findViewById(R.id.title_city_text);

		weather_back.setOnClickListener(this);
		weather_location_city.setOnClickListener(this);
		weather_share.setOnClickListener(this);

	}

	/**
	 * 点击事件
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rel_weather_title_left:
			// 返回
			WeatherActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.rel_weather_title_location:
			Intent intent = new Intent(WeatherActivity.this, CityActivity.class);
			startActivityForResult(intent, WeatherToCity);
			overridePendingTransition(R.anim.in_from_right,
					R.anim.stay_in_place);
			// 选择城市
			break;
		case R.id.rel_weather_title_share:
			// 分享
			break;
		}
	}

	private class WeatherInfo extends AsyncTask<String, Void, String[]> {

		/*
		 * private String cityCode; private String
		 * public_key,private_key,key,getUrl;
		 */
		private String city_town;

		@Override
		protected String[] doInBackground(String... params) {
			String[] result = new String[3];

			weatherService = new WeatherService();
			listWeathers = new ArrayList<Weather>();
			city_town = params[0];
			cityCode = weatherService.getCityByName(city_town,
					getApplicationContext());
			public_key = WeatherKeyUtil.JointPublicUrl(cityCode);
			private_key = ReadPropertiesUtil.read("weather", "PRIVATE");

			key = WeatherKeyUtil.standardURLEncoder(public_key, private_key);
			getUrl = WeatherKeyUtil.JointUrl(cityCode, key);
			Log.d(TAG, "天气预报:" + getUrl);
			// String url =
			// "http://open.weather.com.cn/data/?areaid=101191101&type=forecast_f&date=201504231000&appid=543f65&key=%2BvtTYBDDzAF%2FnJ8Vk6cP7tMVS%2BQ%3D";
			result[0] = HttpUtil.doGet(getUrl);
			result[1] = Weekend.getWeekName(DateUtil.getCurrentWeekendTime())
					+ " " + DateUtil.getCurrentDayTime();

			public_key = WeatherKeyUtil.JointPublicUrlIndex(cityCode);
			key = WeatherKeyUtil.standardURLEncoder(public_key, private_key);
			getUrl = WeatherKeyUtil.JointUrlIndex(cityCode, key);
			result[2] = HttpUtil.doGet(getUrl);
			Log.d(TAG, "天气预报:" + result[0]);
			Log.d(TAG, "天气指数:" + result[2]);
			// Log.d(TAG, "当前的小时和分钟："+DateUtil.getCurrentHourMinute());
			// Log.d(TAG, "返回的小时："+DateUtil.getJointCurrentTime());
			return result;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			String temp = null;
			String detail = null;
			Bitmap bitmap;
			// System.out.println("result-->" + result[0]);
			// System.out.println("result-->" + result[1]);
			try {
				JSONObject jsonObject = new JSONObject(result[0]);

				JSONObject fs0 = jsonObject.getJSONObject("f");
				String publish_time = fs0.getString("f0");
				publish_time = DateUtil.getWeatherPublishTime(publish_time);
				weather_publish.setText(publish_time);
				JSONArray fs = jsonObject.getJSONObject("f").getJSONArray("f1");
				for (int i = 0; i < fs.length(); i++) {
					JSONObject f = (JSONObject) fs.opt(i);
					weather = new Weather();

					dayWeatherPhenomena = f.getString("fa");
					nightWeatherPhenomena = f.getString("fb");
					dayTemperature = f.getString("fc");// 27
					nightTemperature = f.getString("fd");// 15
					dayWind = f.getString("fe");
					nightWind = f.getString("ff");
					dayWindPower = f.getString("fg");
					nightWindPower = f.getString("fh");

					weather.setDayWeatherPhenomena(WeatherPhenomena
							.getPhenomenaName(dayWeatherPhenomena));
					weather.setNightWeatherPhenomena(WeatherPhenomena
							.getPhenomenaName(nightWeatherPhenomena));
					weather.setDayTemperature(dayTemperature);
					weather.setNightTemperature(nightTemperature);
					weather.setDayWind(WeatherWind.getWindName(dayWind));
					weather.setNightWind(WeatherWind.getWindName(nightWind));
					weather.setDayWindPower(WeatherWindPower
							.getWindPowerName(dayWindPower));
					weather.setNightWindPower(WeatherWindPower
							.getWindPowerName(nightWindPower));
					weather.setDayphenIcon(WeatherPhenomena
							.getPhenomenaPicture(dayWeatherPhenomena));
					weather.setNightphenIcon(WeatherPhenomena
							.getNightPhenomenaPicture(nightWeatherPhenomena));
					listWeathers.add(weather);
				}
				// 当时间到了晚上6点钟之后，需要显示的是晚上的天气信息，温度则是第二天的温度
				if (DateUtil.getCurrentHourMinute() >= 1800
						&& DateUtil.getCurrentHourMinute() <= 2400) {
					// Log.d(TAG, ">=1100"+DateUtil.getCurrentHourMinute());
					String dayTemp = listWeathers.get(1).getDayTemperature();
					String nightTemp = listWeathers.get(1)
							.getNightTemperature();
					temp = dayTemp + "°/" + nightTemp + "°";
					String nightWeatherPhen = listWeathers.get(0)
							.getNightWeatherPhenomena();
					String nightWind = listWeathers.get(0).getNightWind();
					String nightWindPower = listWeathers.get(0)
							.getNightWindPower();
					detail = result[1] + " " + nightWeatherPhen + " "
							+ nightWind + " " + nightWindPower;
					bitmap = BitmapFactory.decodeResource(getResources(),
							listWeathers.get(0).getNightphenIcon());

				} else {
					// Log.d(TAG, "<1100"+DateUtil.getCurrentHourMinute());
					String dayTemp = listWeathers.get(0).getDayTemperature();
					String nightTemp = listWeathers.get(0)
							.getNightTemperature();
					temp = dayTemp + "°/" + nightTemp + "°";
					String dayWeatherPhen = listWeathers.get(0)
							.getDayWeatherPhenomena();
					String dayWind = listWeathers.get(0).getDayWind();
					String dayWindPower = listWeathers.get(0).getDayWindPower();
					detail = result[1] + " " + dayWeatherPhen + " " + dayWind
							+ " " + dayWindPower;
					bitmap = BitmapFactory.decodeResource(getResources(),
							listWeathers.get(0).getDayphenIcon());
				}
				String tempTomorrow = listWeathers.get(1).getDayTemperature()
						+ "°/" + listWeathers.get(1).getNightTemperature()
						+ "°";
				String weekendTomorrow = Weekend.getWeekName(DateUtil
						.getTomorrowWeekendTime());

				String detailTomorrow = listWeathers.get(1).getDayWeatherPhenomena()
						+ " " + listWeathers.get(1).getDayWindPower();

				Bitmap bitmapTomorrow = BitmapFactory.decodeResource(
						getResources(), listWeathers.get(1).getDayphenIcon());
				
				
				String tempAfter = listWeathers.get(2).getDayTemperature()
						+ "°/" + listWeathers.get(2).getNightTemperature()
						+ "°";
				String weekendAfter = Weekend.getWeekName(DateUtil
						.getAfterWeekendTime());
				
				String detailAfter = listWeathers.get(2).getDayWeatherPhenomena()
						+ " " + listWeathers.get(2).getDayWindPower();
				
				Bitmap bitmapAfter = BitmapFactory.decodeResource(
						getResources(), listWeathers.get(2).getDayphenIcon());
				//设置明天的天气
				weather_img_pheno_tomorrow.setImageBitmap(bitmapTomorrow);
				weather_weekend_tomorrow.setText(weekendTomorrow);
				weather_degree_tomorrow.setText(tempTomorrow);
				weather_detail_tomorrow.setText(detailTomorrow);
				
				//设置后天的天气
				weather_img_pheno_after.setImageBitmap(bitmapAfter);
				weather_weekend_after.setText(weekendAfter);
				weather_degree_after.setText(tempAfter);
				weather_detail_after.setText(detailAfter);
				
				weather_degree.setText(temp);
				weather_detail.setText(detail);
				weather_img_pheno.setImageBitmap(bitmap);
				weather_img_pheno.startAnimation(AnimationUtils.loadAnimation(
						WeatherActivity.this, R.anim.icon_fade_in));

			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				JSONObject jsonObject = new JSONObject(result[2]);
				JSONArray jsonArray = jsonObject.getJSONArray("i");
				StringBuffer weatherIndex = new StringBuffer();
				String i2,i5;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject index = (JSONObject) jsonArray.opt(i);

					i2 = index.getString("i2");
					i5 = index.getString("i5");
					if(!i5.equals("null")){
						weatherIndex.append(i2 + ":" + i5 + "\n");
					}
				}

				weather_index_detail.setText(weatherIndex.toString());
				weather_index_detail.startAnimation(AnimationUtils
						.loadAnimation(WeatherActivity.this, R.anim.fade_in));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == WeatherToCity && resultCode == CityBackWeather) {
			Bundle data = intent.getExtras();
			String city_town = data.getString("city_town");
			weather_city.setText(city_town);
			// getWeather(city_town);

			new WeatherInfo().execute(city_town);
		}
	}
}
