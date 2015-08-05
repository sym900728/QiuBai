package com.bt.qiubai;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.entity.Joke;
import com.qiubai.entity.Novel;
import com.qiubai.entity.Picture;
import com.qiubai.service.PictureService;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.DensityUtil;
import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class PicturesFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout pictures_rel_listview, crl_header_hidden;
	private CommonRefreshListView picturesListView;
	private int screenWidth, screenHeight;
	
	private PicturesBaseAdapter picturesBaseAdapter;
	private PictureService pictureService = new PictureService();
	private List<Picture> pictures = new ArrayList<Picture>();
	
	private final static int PICTURES_LISTVIEW_FIRST_LOADING_NOCONTENT = 0;
	private final static int PICTURES_LISTVIEW_FIRST_LOADING_ERROR = 1;
	private final static int PICTURES_LISTVIEW_FIRST_LOADING_SUCCESS = 2;
	private final static int PICTURES_LISTVIEW_REFRESH_NOCONTENT = 3;
	private final static int PICTURES_LISTVIEW_REFRESH_ERROR = 4;
	private final static int PICTURES_LISTVIEW_REFRESH_SUCCESS = 5;
	private final static int PICTURES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT = 6;
	private final static int PICTURES_LISTVIEW_REFRESH_LOADING_MORE_ERROR = 7;
	private final static int PICTURES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS = 8;
	private final static String PICTURES_LISTVIEW_SIZE = "10";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view_pictures = inflater.inflate(R.layout.pictures_fragment, container, false);
		
		screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		
		pictures_rel_listview = (RelativeLayout) view_pictures.findViewById(R.id.pictures_rel_listview);
		picturesListView = (CommonRefreshListView) view_pictures.findViewById(R.id.pictures_listview);
		crl_header_hidden = (RelativeLayout) view_pictures.findViewById(R.id.crl_header_hidden);
		picturesBaseAdapter  = new PicturesBaseAdapter(this.getActivity());
		picturesListView.setAdapter(picturesBaseAdapter);
		picturesListView.setHiddenView(crl_header_hidden);
		picturesListView.setOnRefreshListener(this);
		picturesListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		picturesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Picture picture = pictures.get(position - 1);
				Intent intent = new Intent(getActivity(), PictureActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("picture", picture);
				intent.putExtras(bundle);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			}
		});
		onFirstLoadingPictures();
		return view_pictures;
	}
	
	private Handler picturesHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PICTURES_LISTVIEW_FIRST_LOADING_NOCONTENT:
				break;
			case PICTURES_LISTVIEW_FIRST_LOADING_ERROR:
				break;
			case PICTURES_LISTVIEW_FIRST_LOADING_SUCCESS:
				pictures.clear();
				pictures =  (List<Picture>) msg.obj;
				picturesBaseAdapter.notifyDataSetChanged();
				pictures_rel_listview.setVisibility(View.VISIBLE);
				break;
			case PICTURES_LISTVIEW_REFRESH_NOCONTENT:
				break;
			case PICTURES_LISTVIEW_REFRESH_ERROR:
				break;
			case PICTURES_LISTVIEW_REFRESH_SUCCESS:
				picturesListView.hiddenFooterView(true);
				picturesListView.hiddenHeaderView(true);
				pictures.clear();
				pictures = (List<Picture>) msg.obj;
				picturesBaseAdapter.notifyDataSetChanged();
				break;
			case PICTURES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT:
				picturesListView.hiddenFooterView(false);
				break;
			case PICTURES_LISTVIEW_REFRESH_LOADING_MORE_ERROR:
				picturesListView.hiddenFooterView(true);
				break;
			case PICTURES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS:
				picturesListView.hiddenFooterView(true);
				addToListPictures((List<Picture>) msg.obj);
				picturesBaseAdapter.notifyDataSetChanged();
				break;
			}
		};
	};
	
	public void addToListPictures(List<Picture> list){
		for (Picture picture : list) {
			pictures.add(picture);
		}
	}
	
	public void onFirstLoadingPictures(){
		pictures_rel_listview.setVisibility(View.INVISIBLE);
		new Thread(){
			public void run() {
				String result = pictureService.getPictures("0", PICTURES_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_FIRST_LOADING_NOCONTENT);
					picturesHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_FIRST_LOADING_ERROR);
					picturesHandler.sendMessage(msg);
				} else {
					List<Picture> list = pictureService.parsePicturesJson(result);
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_FIRST_LOADING_SUCCESS);
					msg.obj = list;
					picturesHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	@Override
	public void onDownPullRefresh() {
		new Thread(){
			public void run() {
				String result = pictureService.getPictures("0", PICTURES_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_REFRESH_NOCONTENT);
					picturesHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_REFRESH_ERROR);
					picturesHandler.sendMessage(msg);
				} else {
					List<Picture> list = pictureService.parsePicturesJson(result);
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_REFRESH_SUCCESS);
					msg.obj = list;
					picturesHandler.sendMessage(msg);
				}
			};
		}.start();
	}

	@Override
	public void onLoadingMore() {
		new Thread(){
			public void run() {
				String offset = String.valueOf(pictures.size());
				String result = pictureService.getPictures(offset, PICTURES_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT);
					picturesHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_REFRESH_LOADING_MORE_ERROR);
					picturesHandler.sendMessage(msg);
				} else {
					List<Picture> list = pictureService.parsePicturesJson(result);
					Message msg = picturesHandler.obtainMessage(PICTURES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS);
					msg.obj = list;
					picturesHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	private class PicturesBaseAdapter extends BaseAdapter{
		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public PicturesBaseAdapter(Context context){
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return pictures.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Picture picture = pictures.get(position);
			return chooseView(picture, convertView, inflater);
		}
		
	}

	public View chooseView(Picture picture, View convertView, LayoutInflater inflater){
		
		if (picture.getId() % 4 == 1){
			convertView = inflater.inflate(R.layout.pictures_listview_item_first, null);
			//RelativeLayout pictures_listview_item_first_rel = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_first_rel);
			//android.widget.LinearLayout.LayoutParams lp_pictures_listview_item_first_rel = (android.widget.LinearLayout.LayoutParams) pictures_listview_item_first_rel.getLayoutParams();
			//lp_pictures_listview_item_first_rel.width = screenWidth - DensityUtil.dip2px(getActivity(), 20);
			//lp_pictures_listview_item_first_rel.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3;
			//pictures_listview_item_first_rel.setLayoutParams(lp_pictures_listview_item_first_rel);
			
			// the left image (number : 1)
			RelativeLayout pictures_listview_item_first_rel_1 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_first_rel_1);
			android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_first_rel_1 = (android.widget.RelativeLayout.LayoutParams) pictures_listview_item_first_rel_1.getLayoutParams();
			lp_pictures_listview_item_first_rel_1.width = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3 - 1;
			lp_pictures_listview_item_first_rel_1.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3 - 1;
			pictures_listview_item_first_rel_1.setLayoutParams(lp_pictures_listview_item_first_rel_1);
			
			ImageView pictures_listview_item_first_iv_1 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_first_iv_1);
			pictures_listview_item_first_iv_1.setImageBitmap(BitmapUtil.resizeBitmap(lp_pictures_listview_item_first_rel_1.width, lp_pictures_listview_item_first_rel_1.height, BitmapFactory.decodeResource(getResources(), R.drawable.pt_test1)));
			
			// the right top image (number : 2)
			RelativeLayout pictures_listview_item_first_rel_2 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_first_rel_2);
			android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_first_rel_2 = (android.widget.RelativeLayout.LayoutParams) pictures_listview_item_first_rel_2.getLayoutParams();
			lp_pictures_listview_item_first_rel_2.width = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			lp_pictures_listview_item_first_rel_2.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			pictures_listview_item_first_rel_2.setLayoutParams(lp_pictures_listview_item_first_rel_2);
			
			ImageView pictures_listview_item_first_iv_2 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_first_iv_2);
			pictures_listview_item_first_iv_2.setImageBitmap(BitmapUtil.resizeBitmap(lp_pictures_listview_item_first_rel_2.width, lp_pictures_listview_item_first_rel_2.height, BitmapFactory.decodeResource(getResources(), R.drawable.pt_test2)));
			
			// the right bottom image (number : 3)
			RelativeLayout pictures_listview_item_first_rel_3 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_first_rel_3);
			android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_first_rel_3 = (android.widget.RelativeLayout.LayoutParams) pictures_listview_item_first_rel_3.getLayoutParams();
			lp_pictures_listview_item_first_rel_3.width = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			lp_pictures_listview_item_first_rel_3.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			pictures_listview_item_first_rel_3.setLayoutParams(lp_pictures_listview_item_first_rel_3);
			
			ImageView pictures_listview_item_first_iv_3 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_first_iv_3);
			pictures_listview_item_first_iv_3.setImageBitmap(BitmapUtil.resizeBitmap(lp_pictures_listview_item_first_rel_3.width, lp_pictures_listview_item_first_rel_3.height, BitmapFactory.decodeResource(getResources(), R.drawable.pt_test3)));
			
			TextView pictures_listview_item_first_tv_title = (TextView) convertView.findViewById(R.id.pictures_listview_item_first_tv_title);
			pictures_listview_item_first_tv_title.setText("就是这个标题");
			
		} else if (picture.getId() % 2 == 0){
			convertView = inflater.inflate(R.layout.pictures_listview_item_two, null);
			RelativeLayout pictures_listview_item_two_rel = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_two_rel);
			android.widget.LinearLayout.LayoutParams lp_pictures_listview_item_two_rel = (android.widget.LinearLayout.LayoutParams) pictures_listview_item_two_rel.getLayoutParams();
			lp_pictures_listview_item_two_rel.width = screenWidth - DensityUtil.dip2px(getActivity(), 20);
			lp_pictures_listview_item_two_rel.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3;
			pictures_listview_item_two_rel.setLayoutParams(lp_pictures_listview_item_two_rel);
			
			ImageView pictures_listview_item_two_iv_1 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_two_iv_1);
			pictures_listview_item_two_iv_1.setImageBitmap(BitmapUtil.resizeBitmap(lp_pictures_listview_item_two_rel.width, lp_pictures_listview_item_two_rel.height, BitmapFactory.decodeResource(getResources(), R.drawable.pt_test)));
			TextView pictures_listview_item_two_tv_title = (TextView) convertView.findViewById(R.id.pictures_listview_item_two_tv_title);
			pictures_listview_item_two_tv_title.setText("就是这个标题");
			
		} else if (picture.getId() % 4 == 3){
			convertView = inflater.inflate(R.layout.pictures_listview_item_three, null);
			
			// the left top image (number : 1)
			RelativeLayout pictures_listview_item_three_rel_1 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_three_rel_1);
			android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_three_rel_1 = (android.widget.RelativeLayout.LayoutParams) pictures_listview_item_three_rel_1.getLayoutParams();
			lp_pictures_listview_item_three_rel_1.width = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			lp_pictures_listview_item_three_rel_1.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			pictures_listview_item_three_rel_1.setLayoutParams(lp_pictures_listview_item_three_rel_1);
						
			ImageView pictures_listview_item_three_iv_1 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_three_iv_1);
			pictures_listview_item_three_iv_1.setImageBitmap(BitmapUtil.resizeBitmap(lp_pictures_listview_item_three_rel_1.width, lp_pictures_listview_item_three_rel_1.height, BitmapFactory.decodeResource(getResources(), R.drawable.pt_test1)));
						
			// the left bottom image (number : 2)
			RelativeLayout pictures_listview_item_three_rel_2 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_three_rel_2);
			android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_three_rel_2 = (android.widget.RelativeLayout.LayoutParams) pictures_listview_item_three_rel_2.getLayoutParams();
			lp_pictures_listview_item_three_rel_2.width = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			lp_pictures_listview_item_three_rel_2.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
			pictures_listview_item_three_rel_2.setLayoutParams(lp_pictures_listview_item_three_rel_2);
						
			ImageView pictures_listview_item_three_iv_2 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_three_iv_2);
			pictures_listview_item_three_iv_2.setImageBitmap(BitmapUtil.resizeBitmap(lp_pictures_listview_item_three_rel_2.width, lp_pictures_listview_item_three_rel_2.height, BitmapFactory.decodeResource(getResources(), R.drawable.pt_test2)));
						
			// the right image (number : 3)
			RelativeLayout pictures_listview_item_three_rel_3 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_three_rel_3);
			android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_three_rel_3 = (android.widget.RelativeLayout.LayoutParams) pictures_listview_item_three_rel_3.getLayoutParams();
			lp_pictures_listview_item_three_rel_3.width = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3 - 1;
			lp_pictures_listview_item_three_rel_3.height = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3 - 1;
			pictures_listview_item_three_rel_3.setLayoutParams(lp_pictures_listview_item_three_rel_3);
						
			ImageView pictures_listview_item_three_iv_3 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_three_iv_3);
			pictures_listview_item_three_iv_3.setImageBitmap(BitmapUtil.resizeBitmap(lp_pictures_listview_item_three_rel_3.width, lp_pictures_listview_item_three_rel_3.height, BitmapFactory.decodeResource(getResources(), R.drawable.pt_test3)));
						
			TextView pictures_listview_item_three_tv_title = (TextView) convertView.findViewById(R.id.pictures_listview_item_three_tv_title);
			pictures_listview_item_three_tv_title.setText("就是这个标题");
		}
		return convertView;
	}
	
}
