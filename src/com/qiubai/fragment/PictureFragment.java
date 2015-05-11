package com.qiubai.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.qiubai.R;
import com.qiubai.adapter.PictureBaseAdapter;
import com.qiubai.service.PictureService;
import com.qiubai.util.HttpUtil;
import com.qiubai.view.CharacterListView;
import com.qiubai.view.CharacterListView.OnRefreshListener;
import com.qiubai.view.CharacterListView.onLoadListener;

import android.R.integer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PictureFragment extends Fragment implements OnRefreshListener,onLoadListener{
	private static String TAG = "PictureFragment";
	
	private PictureService pictureService;
	
	private int character_start = 0;
	private int character_count = CharacterListView.pageSize;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
//			List<pic>
		};
	};
	
	
	

	/*private String[] fpd_textTitle = new String[] { "伦敦动漫大会超级英雄齐现身",
			"北影复试18岁考生脱上衣任拍", "南非老人庆百岁高空跳伞" };
	private String[] fpd_comment = new String[] { "34", "5", "121" };
	private int[] fpd_image = new int[] { R.drawable.pt_test,
			R.drawable.pt_test2, R.drawable.pt_test3 };
	private String[] fpd3_textTitle = new String[] { "伦敦动漫大会超级英雄齐现身",
			"北影复试18岁考生脱上衣任拍", "南非老人庆百岁高空跳伞" };
	private String[] fpd3_comment = new String[] { "34", "5", "121" };
	private int[] fpd3_image_1 = new int[] { R.drawable.pt_test1,
			R.drawable.pt_test2, R.drawable.pt_test3};
	private int[] fpd3_image_2 = new int[] { R.drawable.pt_test2,
			R.drawable.pt_test1, R.drawable.pt_test3};
	private int[] fpd3_image_3 = new int[] { R.drawable.pt_test3,
			R.drawable.pt_test1, R.drawable.pt_test};*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "==onCreate==");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		View pictureLayout = inflater.inflate(R.layout.fragment_picture_layout,
				container, false);
		// 取得listview实例
		ListView listPictureView = (ListView) pictureLayout
				.findViewById(R.id.listview_fragment_picture);

		// 创建一个List集合，List集合的元素是Map
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		//静态数据
		/*for (int i = 0; i < fpd_comment.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();

			listItem.put("fpd_image_text", fpd_image[i]);
			listItem.put("fpd_textTitle_text", fpd_textTitle[i]);
			listItem.put("fpd_comment", fpd_comment[i]);
			
			listItems.add(listItem);
			
			listItem = new HashMap<String, Object>();
			listItem.put("fpd3_image_1_text", fpd3_image_1[i]);
			listItem.put("fpd3_image_2_text", fpd3_image_2[i]);
			listItem.put("fpd3_image_3_text", fpd3_image_3[i]);
			listItem.put("fpd3_textTitle_text", fpd3_textTitle[i]);
			listItem.put("fpd3_comment", fpd3_comment[i]);
			
			listItems.add(listItem);
		}
*/

		PictureBaseAdapter pictureAdapter = new PictureBaseAdapter(
				getActivity(),listItems);
		listPictureView.setAdapter(pictureAdapter);
		
		new PictureLoad().execute("0","10");

		return pictureLayout;

	}
	
	private class PictureLoad extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			pictureService = new PictureService();
			Map<String, String> map = new HashMap<String, String>();
			map.put("offset", params[0]);
			map.put("rows", params[1]);
			String picture_result = pictureService.getPictures(map);
			Log.d(TAG, "picture_result:"+picture_result);
//			pictureService.getPictures(map);
			return null;
		}
		
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "==onStart==");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "==onResume==");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "==onPause==");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "==onStop==");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "==onDestroyView==");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "==onDestroy==");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "==onDetach==");
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void onRefresh() {
		
	}

}
