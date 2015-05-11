package com.bt.qiubai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qiubai.adapter.CitySortAdapter;
import com.qiubai.dao.PinyinComparator;
import com.qiubai.entity.City;
import com.qiubai.entity.CitySortModel;
import com.qiubai.service.CityService;
import com.qiubai.util.CharacterParser;
import com.qiubai.view.CityClearEditText;
import com.qiubai.view.MyCityLetterListView;
import com.qiubai.view.MyCityLetterListView.OnTouchingLetterChangedListener;

public class CityActivity extends Activity {
	private ListView sortListView;
	private MyCityLetterListView myCityLetterListView;
	private TextView dialog;
	private RelativeLayout relative_back;
	private CitySortAdapter citySortAdapter;
	private CityClearEditText cityClearEditText;

	/**
	 * 汉子转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<CitySortModel> sourceDataList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private CityService cityService;

	private static final String TAG = "CityActivity";
	private List<City> listCitys;
	public static final String SHAREDPREFERENCES_FIRSTENTER = "qiubai";
	/**
	 * 存放城市的名称
	 */
	public static final String CityActivity_CityTown = "CityActivity_CityTown";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_list);
		listCitys = initCityList();
		initViews(listCitys);
	}

	private List<City> initCityList() {

		try {
			ExecutorService pool = Executors.newFixedThreadPool(1);
			Callable<List<City>> city = new ReturnCallbale();
			Future<List<City>> f1 = pool.submit(city);
			listCitys = (List<City>) f1.get();
			// Log.d(TAG, "initCityList()" + listCitys.size());
			pool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return listCitys;
	}

	public class ReturnCallbale implements Callable<List<City>> {

		@Override
		public List<City> call() throws Exception {
			// TODO Auto-generated method stub
			cityService = new CityService();
			listCitys = cityService.getAllCity(getApplicationContext());
			return listCitys;
		}

	}

	/**
	 * 往本地的sharepreferences中存放城市的名字
	 * 
	 * @param city
	 */
	private void initShareDataBase(String city) {
		SharedPreferences share = getSharedPreferences(
				SHAREDPREFERENCES_FIRSTENTER, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(CityActivity_CityTown, city);
		editor.commit();
	}

	private void initViews(List<City> list) {
		// 实例化汉子转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		myCityLetterListView = (MyCityLetterListView) findViewById(R.id.city_letterListView);
		dialog = (TextView) findViewById(R.id.city_dialog);
		myCityLetterListView.setmTextDialog(dialog);

		relative_back = (RelativeLayout) findViewById(R.id.rel_city_title_back);

		relative_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CityActivity.this.finish();
				overridePendingTransition(R.anim.stay_in_place,
						R.anim.out_to_right);
			}
		});
		// 设置右键触摸监听
		myCityLetterListView
				.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

					@Override
					public void onTouchingLetterChanged(String s) {
						// 该字母首次出现的位置
						int postion = citySortAdapter.getPositionForSection(s
								.charAt(0));
						if (postion != -1) {
							sortListView.setSelection(postion);
						}

					}
				});
		sortListView = (ListView) findViewById(R.id.city_list_country);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象

				String cityName = ((CitySortModel) citySortAdapter
						.getItem(position)).getName();

				String[] city = cityName.split("-");
				// String city_province = city[0];
				String city_town = city[1];

				Intent intent = getIntent();
				intent.putExtra("city_town", city_town);

				initShareDataBase(city_town);
				CityActivity.this.setResult(WeatherActivity.CityBackWeather,
						intent);
				CityActivity.this.finish();
				overridePendingTransition(R.anim.stay_in_place,
						R.anim.out_to_right);
			}
		});

		sourceDataList = filledData(list);

		// 根据a-z进行排序源数据
		Collections.sort(sourceDataList, pinyinComparator);
		citySortAdapter = new CitySortAdapter(sourceDataList, this);
		sortListView.setAdapter(citySortAdapter);

		cityClearEditText = (CityClearEditText) findViewById(R.id.city_filter_edit);

		// 根据输入框输入值的改变进行过滤搜索
		cityClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
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

	/**
	 * 为listView 中添加数据库中的数据
	 * 
	 * @param list
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private List<CitySortModel> filledData(List<City> list) {
		List<CitySortModel> mSortList = new ArrayList<CitySortModel>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				CitySortModel citySortModel = new CitySortModel();
				citySortModel.setName(list.get(i).getProvince() + "-"
						+ list.get(i).getTown());
				String pinyin = list.get(i).getProven()+list.get(i).getDistricten();
				citySortModel.setPinyinName(pinyin);

				// 汉子转换成拼音
//				String pinyin = characterParser.getSelling(list.get(i)
//						.getProvince() + "-" + list.get(i).getTown());
				// Log.d(TAG,
				// "pinyin--->"+pinyin+"  :"+pinyin.matches("^[a-z]*$"));
				String sortString = pinyin.substring(0, 1).toUpperCase();

				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					citySortModel.setSortLetters(sortString.toUpperCase());
//					citySortModel.setPinyinName(pinyin);
				} else {
					citySortModel.setSortLetters("#");
				}

				mSortList.add(citySortModel);
			}
		}
		return mSortList;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<CitySortModel> filterDateList = new ArrayList<CitySortModel>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = sourceDataList;
		} else {
			filterDateList.clear();
			for (CitySortModel citySortModel : sourceDataList) {
				String name = citySortModel.getName();
				String pinyinName = citySortModel.getPinyinName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())
						|| pinyinName.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(citySortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		citySortAdapter.updateListView(filterDateList);
	}
	
}
