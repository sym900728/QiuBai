package com.bt.qiubai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiubai.dao.PictureDao;
import com.qiubai.dao.impl.PictureDaoImpl;
import com.qiubai.entity.Picture;
import com.qiubai.service.PictureService;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.CommonRefreshListViewAnimation;
import com.qiubai.util.DensityUtil;
import com.qiubai.util.NetworkUtil;
import com.qiubai.util.PropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.util.TimeUtil;
import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PicturesFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout pictures_rel_listview, crl_header_hidden;
	private CommonRefreshListView picturesListView;
	private int screenWidth, requireSmallWidth, requireLargeWidth, 
		requireSuperLargeWidth, requireSuperLargeHeight;
	private Bitmap bitmap_default_rectangle, bitmap_default_square;//rectangel: 长方形; square: 正方形;
	private Bitmap bitmap_changed_small, bitmap_changed_normal, bitmap_changed_large;
	
	private PicturesBaseAdapter picturesBaseAdapter;
	private PictureService pictureService;
	private PictureDao pictureDao;
	private PropertiesUtil propUtil;
	private List<Picture> pictures = new ArrayList<Picture>();
	private Map<String, Bitmap> map = new HashMap<String, Bitmap>();
	private SharedPreferencesUtil spUtil;
	
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
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view_pictures = inflater.inflate(R.layout.pictures_fragment, container, false);
		
		pictureService = new PictureService(this.getActivity());
		pictureDao = new PictureDaoImpl(this.getActivity());
		propUtil = new PropertiesUtil(this.getActivity());
		spUtil = new SharedPreferencesUtil(this.getActivity());
		
		screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		requireSuperLargeWidth = screenWidth - DensityUtil.dip2px(getActivity(), 20);
		requireSuperLargeHeight = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3;
		requireSmallWidth = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 3 - 1;
		requireLargeWidth = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3 - 1;
		bitmap_default_rectangle = BitmapFactory.decodeResource(getResources(), R.drawable.common_listview_item_image_default2);
		bitmap_default_square = BitmapFactory.decodeResource(getResources(), R.drawable.common_listview_item_image_default);
		bitmap_changed_small = BitmapUtil.resizeBitmap(requireSmallWidth, requireSmallWidth, bitmap_default_square);
		bitmap_changed_normal = BitmapUtil.resizeBitmap(requireLargeWidth, requireLargeWidth, bitmap_default_square);
		bitmap_changed_large = BitmapUtil.resizeBitmap(requireSuperLargeWidth, requireSuperLargeHeight, bitmap_default_rectangle);
		
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
		loadDatabaseData();
		loadPicturesDataTimer();
		return view_pictures;
	}
	
	public void loadPicturesDataTimer(){
		//判断是否联网
		if(NetworkUtil.isConnectInternet(this.getActivity())){
			//判断 PicturesFragment 是不是第一次运行
			if(spUtil.isFirstRun("isPicturesFragmentFirstRun")){
				//第一次运行
				onFirstLoadingPictures();
			} else {
				//判断当前时间跟上一次刷新时间差
				long time = spUtil.getRefreshTime("picturesFragmentLastRefreshTime");
				if(TimeUtil.compareTime(time)){
					//listview 自动下拉刷新
					CommonRefreshListViewAnimation.moveListView(picturesListView, "picturesFragmentLastRefreshTime");
				}
			}
		}
	}
	
	public void loadDatabaseData(){
		pictures.clear();
		pictures = pictureDao.getPictures(null);
		if(!pictures.isEmpty()){
			picturesBaseAdapter.notifyDataSetChanged();
			picturesListView.setLastUpdateTime("picturesFragmentLastRefreshTime");
			pictures_rel_listview.setVisibility(View.VISIBLE);
		}
	}
	
	public void setDatabaseData(final List<Picture> list){
		new Thread(){
			public void run() {
				pictureDao.emptyPictureTable();
				pictureDao.addPictures(list);
			};
		}.start();
	}
	
	/**
	 * 第一次加载
	 */
	public void onFirstLoadingPictures(){
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
	
	private Handler picturesHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PICTURES_LISTVIEW_FIRST_LOADING_NOCONTENT:
				break;
			case PICTURES_LISTVIEW_FIRST_LOADING_ERROR:
				break;
			case PICTURES_LISTVIEW_FIRST_LOADING_SUCCESS:
				pictures =  (List<Picture>) msg.obj;
				picturesBaseAdapter.notifyDataSetChanged();
				picturesListView.updateLastUpdateTime("picturesFragmentLastRefreshTime");
				pictures_rel_listview.setVisibility(View.VISIBLE);
				setDatabaseData(pictures);
				//storeBitmapToMap(pictures);
				break;
			case PICTURES_LISTVIEW_REFRESH_NOCONTENT:
				break;
			case PICTURES_LISTVIEW_REFRESH_ERROR:
				picturesListView.hiddenHeaderView();
				break;
			case PICTURES_LISTVIEW_REFRESH_SUCCESS:
				picturesListView.hiddenFooterView(true);
				picturesListView.hiddenHeaderView();
				pictures.clear();
				pictures = (List<Picture>) msg.obj;
				picturesBaseAdapter.notifyDataSetChanged();
				picturesListView.updateLastUpdateTime("picturesFragmentLastRefreshTime");
				setDatabaseData(pictures);
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
				//storeBitmapToMap((List<Picture>)msg.obj);
				break;
			}
		};
	};
	
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

		/**
		 * getView 方法中不要有调整图像大小的代码（可以放在子线程中）
		 */
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.pictures_listview_item, null);
				viewHolder = new ViewHolder();
				
				viewHolder.pictures_listview_item_one_lin = (LinearLayout) convertView.findViewById(R.id.pictures_listview_item_one_lin);
				viewHolder.pictures_listview_item_two_lin = (LinearLayout) convertView.findViewById(R.id.pictures_listview_item_two_lin);
				viewHolder.pictures_listview_item_three_lin = (LinearLayout) convertView.findViewById(R.id.pictures_listview_item_three_lin);
				
				viewHolder.pictures_listview_item_one_rel_1 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_one_rel_1);
				viewHolder.pictures_listview_item_one_iv_1 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_one_iv_1);
				viewHolder.pictures_listview_item_one_rel_2 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_one_rel_2);
				viewHolder.pictures_listview_item_one_iv_2 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_one_iv_2);
				viewHolder.pictures_listview_item_one_rel_3 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_one_rel_3);
				viewHolder.pictures_listview_item_one_iv_3 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_one_iv_3);
				viewHolder.pictures_listview_item_one_tv_title = (TextView) convertView.findViewById(R.id.pictures_listview_item_one_tv_title);
				
				viewHolder.pictures_listview_item_two_rel = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_two_rel);
				viewHolder.pictures_listview_item_two_iv_1 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_two_iv_1);
				viewHolder.pictures_listview_item_two_tv_title = (TextView) convertView.findViewById(R.id.pictures_listview_item_two_tv_title);
				
				viewHolder.pictures_listview_item_three_rel_1 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_three_rel_1);
				viewHolder.pictures_listview_item_three_iv_1 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_three_iv_1);
				viewHolder.pictures_listview_item_three_rel_2 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_three_rel_2);
				viewHolder.pictures_listview_item_three_iv_2 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_three_iv_2);
				viewHolder.pictures_listview_item_three_rel_3 = (RelativeLayout) convertView.findViewById(R.id.pictures_listview_item_three_rel_3);
				viewHolder.pictures_listview_item_three_iv_3 = (ImageView) convertView.findViewById(R.id.pictures_listview_item_three_iv_3);
				viewHolder.pictures_listview_item_three_tv_title = (TextView) convertView.findViewById(R.id.pictures_listview_item_three_tv_title);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Picture picture = pictures.get(position);
			if (picture.getId() % 4 == 1){
				viewHolder.pictures_listview_item_one_lin.setVisibility(View.VISIBLE);
				viewHolder.pictures_listview_item_two_lin.setVisibility(View.GONE);
				viewHolder.pictures_listview_item_three_lin.setVisibility(View.GONE);
				// the left image (number : 1)
				android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_one_rel_1 = (android.widget.RelativeLayout.LayoutParams) 
						viewHolder.pictures_listview_item_one_rel_1.getLayoutParams();
				lp_pictures_listview_item_one_rel_1.width = requireLargeWidth;
				lp_pictures_listview_item_one_rel_1.height = requireLargeWidth;
				viewHolder.pictures_listview_item_one_rel_1.setLayoutParams(lp_pictures_listview_item_one_rel_1);
				viewHolder.pictures_listview_item_one_iv_1.setImageBitmap(bitmap_changed_normal);
				viewHolder.pictures_listview_item_one_iv_1.setTag(picture.getImage1());
				//viewHolder.pictures_listview_item_one_iv_1.setTag(picture.getImage1());;
				loadImage("1", picture.getId(), picture.getImage1(), viewHolder.pictures_listview_item_one_iv_1, requireLargeWidth, requireLargeWidth);
				
				// the right top image (number : 2)
				android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_one_rel_2 = (android.widget.RelativeLayout.LayoutParams) 
						viewHolder.pictures_listview_item_one_rel_2.getLayoutParams();
				lp_pictures_listview_item_one_rel_2.width = requireSmallWidth;
				lp_pictures_listview_item_one_rel_2.height = requireSmallWidth;
				viewHolder.pictures_listview_item_one_rel_2.setLayoutParams(lp_pictures_listview_item_one_rel_2);
				viewHolder.pictures_listview_item_one_iv_2.setImageBitmap(bitmap_changed_small);
				viewHolder.pictures_listview_item_one_iv_2.setTag(picture.getImage2());
				loadImage("2", picture.getId(), picture.getImage2(), viewHolder.pictures_listview_item_one_iv_2, requireSmallWidth, requireSmallWidth);
				
				// the right bottom image (number : 3)
				android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_one_rel_3 = (android.widget.RelativeLayout.LayoutParams) 
						viewHolder.pictures_listview_item_one_rel_3.getLayoutParams();
				lp_pictures_listview_item_one_rel_3.width = requireSmallWidth;
				lp_pictures_listview_item_one_rel_3.height = requireSmallWidth;
				viewHolder.pictures_listview_item_one_rel_3.setLayoutParams(lp_pictures_listview_item_one_rel_3);
				viewHolder.pictures_listview_item_one_iv_3.setImageBitmap(bitmap_changed_small);
				viewHolder.pictures_listview_item_one_iv_3.setTag(picture.getImage3());
				loadImage("3", picture.getId(), picture.getImage3(), viewHolder.pictures_listview_item_one_iv_3, requireSmallWidth, requireSmallWidth);
				
				viewHolder.pictures_listview_item_one_tv_title.setText(picture.getTitle());
				
			} else if (picture.getId() % 2 == 0){
				viewHolder.pictures_listview_item_one_lin.setVisibility(View.GONE);
				viewHolder.pictures_listview_item_two_lin.setVisibility(View.VISIBLE);
				viewHolder.pictures_listview_item_three_lin.setVisibility(View.GONE);
				android.widget.LinearLayout.LayoutParams lp_pictures_listview_item_two_rel = (android.widget.LinearLayout.LayoutParams) 
						viewHolder.pictures_listview_item_two_rel.getLayoutParams();
				lp_pictures_listview_item_two_rel.width = requireSuperLargeWidth;
				lp_pictures_listview_item_two_rel.height = requireSuperLargeHeight;
				viewHolder.pictures_listview_item_two_rel.setLayoutParams(lp_pictures_listview_item_two_rel);
				viewHolder.pictures_listview_item_two_iv_1.setImageBitmap(bitmap_changed_large);
				viewHolder.pictures_listview_item_two_iv_1.setTag(picture.getImage1());
				loadImage("1", picture.getId(), picture.getImage1(), viewHolder.pictures_listview_item_two_iv_1, requireSuperLargeWidth, requireSuperLargeHeight);
				
				viewHolder.pictures_listview_item_two_tv_title.setText(picture.getTitle());
			} else if (picture.getId() % 4 == 3){
				viewHolder.pictures_listview_item_one_lin.setVisibility(View.GONE);
				viewHolder.pictures_listview_item_two_lin.setVisibility(View.GONE);
				viewHolder.pictures_listview_item_three_lin.setVisibility(View.VISIBLE);
				// the left top image (number : 1)
				android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_three_rel_1 = (android.widget.RelativeLayout.LayoutParams) 
						viewHolder.pictures_listview_item_three_rel_1.getLayoutParams();
				lp_pictures_listview_item_three_rel_1.width = requireSmallWidth;
				lp_pictures_listview_item_three_rel_1.height = requireSmallWidth;
				viewHolder.pictures_listview_item_three_rel_1.setLayoutParams(lp_pictures_listview_item_three_rel_1);
				viewHolder.pictures_listview_item_three_iv_1.setImageBitmap(bitmap_changed_small);
				viewHolder.pictures_listview_item_three_iv_1.setTag(picture.getImage1());;
				loadImage("1", picture.getId(), picture.getImage1(), viewHolder.pictures_listview_item_three_iv_1, requireSmallWidth, requireSmallWidth);
				
				// the left bottom image (number : 2)
				android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_three_rel_2 = (android.widget.RelativeLayout.LayoutParams) 
						viewHolder.pictures_listview_item_three_rel_2.getLayoutParams();
				lp_pictures_listview_item_three_rel_2.width = requireSmallWidth;
				lp_pictures_listview_item_three_rel_2.height = requireSmallWidth;
				viewHolder.pictures_listview_item_three_rel_2.setLayoutParams(lp_pictures_listview_item_three_rel_2);
				viewHolder.pictures_listview_item_three_iv_2.setImageBitmap(bitmap_changed_small);
				viewHolder.pictures_listview_item_three_iv_2.setTag(picture.getImage2());
				loadImage("2", picture.getId(), picture.getImage2(), viewHolder.pictures_listview_item_three_iv_2, requireSmallWidth, requireSmallWidth);
				
				// the right bottom image (number : 3)
				android.widget.RelativeLayout.LayoutParams lp_pictures_listview_item_three_rel_3 = (android.widget.RelativeLayout.LayoutParams) 
						viewHolder.pictures_listview_item_three_rel_3.getLayoutParams();
				lp_pictures_listview_item_three_rel_3.width = requireLargeWidth;
				lp_pictures_listview_item_three_rel_3.height = requireLargeWidth;
				viewHolder.pictures_listview_item_three_rel_3.setLayoutParams(lp_pictures_listview_item_three_rel_3);
				viewHolder.pictures_listview_item_three_iv_3.setImageBitmap(bitmap_changed_normal);
				viewHolder.pictures_listview_item_three_iv_3.setTag(picture.getImage3());
				loadImage("3", picture.getId(), picture.getImage3(), viewHolder.pictures_listview_item_three_iv_3, requireLargeWidth, requireLargeWidth);
				
				viewHolder.pictures_listview_item_three_tv_title.setText(picture.getTitle());
			}
			
			return convertView;
		}
		
	}

	private class ViewHolder{
		LinearLayout pictures_listview_item_one_lin;
		LinearLayout pictures_listview_item_two_lin;
		LinearLayout pictures_listview_item_three_lin;
		
		RelativeLayout pictures_listview_item_one_rel_1;
		ImageView pictures_listview_item_one_iv_1;
		RelativeLayout pictures_listview_item_one_rel_2;
		ImageView pictures_listview_item_one_iv_2;
		RelativeLayout pictures_listview_item_one_rel_3;
		ImageView pictures_listview_item_one_iv_3;
		TextView pictures_listview_item_one_tv_title;
		
		RelativeLayout pictures_listview_item_two_rel;
		ImageView pictures_listview_item_two_iv_1;
		TextView pictures_listview_item_two_tv_title;
		
		RelativeLayout pictures_listview_item_three_rel_1;
		ImageView pictures_listview_item_three_iv_1;
		RelativeLayout pictures_listview_item_three_rel_2;
		ImageView pictures_listview_item_three_iv_2;
		RelativeLayout pictures_listview_item_three_rel_3;
		ImageView pictures_listview_item_three_iv_3;
		TextView pictures_listview_item_three_tv_title;
	}

	/**
	 * load image
	 * @param flag
	 * @param id
	 * @param uri
	 * @param iv
	 * @param width
	 * @param height
	 */
	private void loadImage(final String flag, final int id, final String uri, final ImageView iv, final int width, final int height){
		Bitmap bitmap_map = map.get(id + "_" + flag);
		if(bitmap_map != null){
			iv.setImageBitmap(bitmap_map);
		} else {
			new Thread(){
				public void run() {
					Bitmap bitmap_file = BitmapFactory.decodeFile(propUtil.readProperties("config.properties", "pictures_picture_path") + "/" + id + "/" + flag + ".png");
					if(bitmap_file != null){
						final Bitmap bitmap_map_changed = BitmapUtil.resizeBitmap(width, height, bitmap_file);
						map.put(id + "_" + flag, bitmap_map_changed);
						picturesHandler.post(new Runnable() {
							@Override
							public void run() {
								iv.setImageBitmap(bitmap_map_changed);
							}
						});
						
					} else {
						new Thread(){
							public void run() {
								Bitmap bitmap_remote = pictureService.getImage(uri);
								if(bitmap_remote != null){
									pictureService.storeImage(bitmap_remote, String.valueOf(id), flag + ".png");
									final Bitmap bitmap_remote_changed = BitmapUtil.resizeBitmap(width, height, bitmap_remote);
									map.put(id + "_" + flag , bitmap_remote_changed);
									picturesHandler.post(new Runnable() {
										@Override
										public void run() {
											String str = (String) iv.getTag();
											if(uri.equals(str)){
												iv.setImageBitmap(bitmap_remote_changed);
											}
										}
									});
								}
							};
						}.start();
					}
				};
			}.start();
			
		}
	}
	
	public void storeBitmapToMap(List<Picture> list){
		for(final Picture picture : list){
			if(!"".equals(picture.getImage1()) && picture.getImage1() != null ){
				new Thread(){
					public void run() {
						Bitmap bitmap = pictureService.getImage(picture.getImage1());
						if(bitmap != null){
							map.put(picture.getId() + "_1", chooseBitmap(1, picture, bitmap));
							pictureService.storeImage(bitmap, String.valueOf(picture.getId()), "1.png");
						}
					};
				}.start();
			}
			if(!"".equals(picture.getImage2()) && picture.getImage2() != null ){
				new Thread(){
					public void run() {
						Bitmap bitmap = pictureService.getImage(picture.getImage2());
						if(bitmap != null){
							map.put(picture.getId() + "_2", chooseBitmap(2, picture, bitmap));
							pictureService.storeImage(bitmap, String.valueOf(picture.getId()), "2.png");
						}
					};
				}.start();
			}
			if(!"".equals(picture.getImage3()) && picture.getImage3() != null ){
				new Thread(){
					public void run() {
						Bitmap bitmap = pictureService.getImage(picture.getImage3());
						if(bitmap != null){
							map.put(picture.getId() + "_3", chooseBitmap(3, picture, bitmap));
							pictureService.storeImage(bitmap, String.valueOf(picture.getId()), "3.png");
						}
					};
				}.start();
			}
		}
	}
	
	public Bitmap chooseBitmap(int flag, Picture picture, Bitmap bitmap){
		if(picture.getId() % 4 == 1){
			if(flag == 1){
				return BitmapUtil.resizeBitmap(requireLargeWidth, requireLargeWidth, bitmap);
			} else if (flag == 2){
				return BitmapUtil.resizeBitmap(requireSmallWidth, requireSmallWidth, bitmap);
			} else {
				return BitmapUtil.resizeBitmap(requireSmallWidth, requireSmallWidth, bitmap);
			}
		} else if (picture.getId() % 2 == 0){
			if(flag == 1){
				return BitmapUtil.resizeBitmap(requireSuperLargeWidth, requireSuperLargeHeight, bitmap);
			}
		} else if (picture.getId() % 4 == 3){
			if(flag == 1){
				return BitmapUtil.resizeBitmap(requireSmallWidth, requireSmallWidth, bitmap);
			} else if(flag == 2){
				return BitmapUtil.resizeBitmap(requireSmallWidth, requireSmallWidth, bitmap);
			} else {
				return BitmapUtil.resizeBitmap(requireLargeWidth, requireLargeWidth, bitmap);
			}
		}
		return null;
	}
	
	
	public void addToListPictures(List<Picture> list){
		for (Picture picture : list) {
			pictures.add(picture);
		}
	}
	
}
