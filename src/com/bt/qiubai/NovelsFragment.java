package com.bt.qiubai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiubai.dao.NovelDao;
import com.qiubai.dao.impl.NovelDaoImpl;
import com.qiubai.entity.Novel;
import com.qiubai.service.NovelService;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NovelsFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout novels_rel_listview, crl_header_hidden;
	private CommonRefreshListView novelsListView;
	private NovelsBaseAdapter novelsBaseAdapter;
	
	private int requireWidth, requireHeight;//图片框的大小
	private Bitmap bitmap_default;
	
	private List<Novel> novels = new ArrayList<Novel>();
	private Map<Integer, Bitmap> map = new HashMap<Integer, Bitmap>();
	private NovelService novelService;
	private PropertiesUtil propUtil;
	private SharedPreferencesUtil spUtil;
	private NovelDao novelDao;
	
	private final static int NOVELS_LISTVIEW_REFRESH_SUCCESS = 1;
	private final static int NOVELS_LISTVIEW_REFRESH_NOCONTENT = 2;
	private final static int NOVELS_LISTVIEW_REFRESH_ERROR = 3;
	private final static int NOVELS_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS = 4;
	private final static int NOVELS_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT = 5;
	private final static int NOVELS_LISTVIEW_REFRESH_LOADING_MORE_ERROR = 6;
	private final static int NOVELS_LISTVIEW_FIRST_LOADING_SUCCESS = 7;
	private final static int NOVELS_LISTVIEW_FIRST_LOADING_ERROR = 8;
	private final static int NOVELS_LISTVIEW_FIRST_LOADING_NOCONTENT = 9;
	private final static String NOVELS_LISTVIEW_SIZE = "10";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view_novels = inflater.inflate(R.layout.novels_fragment, container, false);
		
		novelService = new NovelService(this.getActivity());
		propUtil = new PropertiesUtil(getActivity());
		spUtil = new SharedPreferencesUtil(this.getActivity());
		novelDao = new NovelDaoImpl(this.getActivity());
		
		requireWidth = DensityUtil.dip2px(NovelsFragment.this.getActivity(), 80);
		requireHeight = DensityUtil.dip2px(NovelsFragment.this.getActivity(), 80);
		bitmap_default = BitmapFactory.decodeResource(getResources(), R.drawable.common_listview_item_image_default);
		
		novels_rel_listview = (RelativeLayout) view_novels.findViewById(R.id.novels_rel_listview);
		crl_header_hidden = (RelativeLayout) view_novels.findViewById(R.id.crl_header_hidden);
		
		novelsListView = (CommonRefreshListView) view_novels.findViewById(R.id.novels_listview);
		novelsBaseAdapter = new NovelsBaseAdapter(this.getActivity());
		novelsListView.setAdapter(novelsBaseAdapter);
		novelsListView.setHiddenView(crl_header_hidden);
		novelsListView.setOnRefreshListener(this);
		novelsListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		novelsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Novel novel = novels.get(position - 1);
				Intent intent = new Intent(getActivity(), NovelActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("novel", novel);
				intent.putExtras(bundle);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			}
		});
		loadDatabaseData();
		return view_novels;
	}
	
	public void loadNovelsDataTimer(){
		//判断是否联网
		if(NetworkUtil.isConnectInternet(this.getActivity())){
			//判断是不是第一次运行 NovelsFragment
			if(spUtil.isFirstRun("isNovelsFragmentFirstRun")){
				onFirstLoadingNovels();
			} else {
				//比较时间
				if(TimeUtil.compareTime(spUtil.getRefreshTime("novelsFragmentLastRefreshTime"))){
					CommonRefreshListViewAnimation.moveListView(novelsListView, "novelsFragmentLastRefreshTime");
				}
			}
		}
	}
	
	public void loadDatabaseData(){
		novels.clear();
		novels = novelDao.getNovels(null);
		if(!novels.isEmpty()){
			novelsBaseAdapter.notifyDataSetChanged();
			novelsListView.setLastUpdateTime("novelsFragmentLastRefreshTime");
			novels_rel_listview.setVisibility(View.VISIBLE);
		}
	}
	
	public void setDatabaseData(final List<Novel> list){
		new Thread(){
			public void run() {
				novelDao.emptyNovelTable();
				novelDao.addNovels(list);
			};
		}.start();
	}
	
	
	public void onFirstLoadingNovels(){
		novels_rel_listview.setVisibility(View.INVISIBLE);
		new Thread(){
			public void run() {
				String result = novelService.getNovels("0", NOVELS_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_FIRST_LOADING_NOCONTENT);
					novelsHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_FIRST_LOADING_ERROR);
					novelsHandler.sendMessage(msg);
				} else {
					List<Novel> list = novelService.parseNovelsJson(result);
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_FIRST_LOADING_SUCCESS);
					msg.obj = list;
					novelsHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	@SuppressLint("ViewHolder")
	private class NovelsBaseAdapter extends BaseAdapter{
		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public NovelsBaseAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return novels.size();
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
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
			
			if(convertView == null){
				convertView = inflater.inflate(R.layout.novels_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.novels_listview_item_tv_title = (TextView) convertView.findViewById(R.id.novels_listview_item_tv_title);
				viewHolder.novels_listview_item_tv_description = (TextView) convertView.findViewById(R.id.novels_listview_item_tv_description);
				viewHolder.novels_listview_item_tv_comment = (TextView) convertView.findViewById(R.id.novels_listview_item_tv_comment);
				viewHolder.novels_listview_item_iv_picture = (ImageView) convertView.findViewById(R.id.novels_listview_item_iv_picture);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Novel novel = novels.get(position);
			viewHolder.novels_listview_item_tv_title.setText(novel.getTitle());
			viewHolder.novels_listview_item_tv_description.setText(novel.getDescription());
			viewHolder.novels_listview_item_tv_comment.setText(novel.getComments() + " 评论");
			viewHolder.novels_listview_item_iv_picture.setImageBitmap(bitmap_default);
			viewHolder.novels_listview_item_iv_picture.setTag(novel.getImage());
			if(!"default".equals(novel.getImage()) && !"".equals(novel.getImage()) ){
				loadImage(novel.getId(), novel.getImage(), viewHolder.novels_listview_item_iv_picture);
			}
			return convertView;
		}
		
		private class ViewHolder{
			TextView novels_listview_item_tv_title;
			TextView novels_listview_item_tv_description;
			TextView novels_listview_item_tv_comment;
			ImageView novels_listview_item_iv_picture;
		}
	}
	
	/**
	 * get novel image
	 * @param url
	 * @param iv
	 */
	public void loadImage(final int id, final String url, final ImageView iv){
		Bitmap bitmap = map.get(id);
		if(bitmap != null){
			iv.setImageBitmap(bitmap);
		} else {
			new Thread(){
				public void run() {
					final Bitmap bitmap2 = BitmapFactory.decodeFile(propUtil.readProperties("config.properties", "novels_picture_path") + "/" + id + ".png");
					if(bitmap2 != null){
						map.put(id, bitmap2);
						novelsHandler.post(new Runnable() {
							@Override
							public void run() {
								iv.setImageBitmap(bitmap2);
							}
						});
					} else {
						new Thread(){
							public void run() {
								final Bitmap bitmap3 = novelService.getImage(url);
								if(bitmap3 != null){
									novelService.storeImage(bitmap3, id + ".png");
									map.put(id, bitmap3);
									novelsHandler.post(new Runnable() {
										@Override
										public void run() {
											String str = (String) iv.getTag();
											if(url.equals(str)){
												iv.setImageBitmap(bitmap3);
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
	
	@SuppressLint("HandlerLeak")
	private Handler novelsHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOVELS_LISTVIEW_REFRESH_SUCCESS://下拉刷新数据成功
				novelsListView.hiddenHeaderView();
				novelsListView.hiddenFooterView(true);
				novels.clear();
				novels = (List<Novel>) msg.obj;
				novelsBaseAdapter.notifyDataSetChanged();
				novelsListView.updateLastUpdateTime("novelsFragmentLastRefreshTime");
				setDatabaseData(novels);
				break;
			case NOVELS_LISTVIEW_REFRESH_NOCONTENT:
				break;
			case NOVELS_LISTVIEW_REFRESH_ERROR:
				novelsListView.hiddenHeaderView();
				break;
			case NOVELS_LISTVIEW_FIRST_LOADING_SUCCESS://第一次加载数据成功
				novels.clear();
				novels =  (List<Novel>) msg.obj;
				novelsBaseAdapter.notifyDataSetChanged();
				novelsListView.updateLastUpdateTime("novelsFragmentLastRefreshTime");
				novels_rel_listview.setVisibility(View.VISIBLE);
				setDatabaseData(novels);
				//storeBitmapToMap(novels);
				break;
			case NOVELS_LISTVIEW_FIRST_LOADING_NOCONTENT:
				break;
			case NOVELS_LISTVIEW_FIRST_LOADING_ERROR:
				break;
			case NOVELS_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS://上拉加载更多数据成功
				novelsListView.hiddenFooterView(true);
				addToListNovels((List<Novel>) msg.obj);
				novelsBaseAdapter.notifyDataSetChanged();
				//storeBitmapToMap((List<Novel>) msg.obj);
				break;
			case NOVELS_LISTVIEW_REFRESH_LOADING_MORE_ERROR:
				novelsListView.hiddenFooterView(true);
				break;
			case NOVELS_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT:
				novelsListView.hiddenFooterView(false);
				break;
			}
		};
	};
	
	public void storeBitmapToMap(List<Novel> list){
		for(final Novel novel : list){
			if(!"default".equals(novel.getImage()) && !"".equals(novel.getImage())){
				new Thread(){
					public void run() {
						Bitmap bitmap = novelService.getImage(novel.getImage());
						if(bitmap != null){
							map.put(novel.getId(), bitmap);
							novelService.storeImage(bitmap, novel.getId() + ".png");
						}
					};
				}.start();
			}
		}
	}
	
	public void addToListNovels(List<Novel> list){
		for(Novel novel : list){
			novels.add(novel);
		}
	}
	
	/**
	 * pull down to refresh
	 */
	@Override
	public void onDownPullRefresh() {
		new Thread(){
			public void run() {
				String result = novelService.getNovels("0", NOVELS_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_REFRESH_NOCONTENT);
					novelsHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_REFRESH_ERROR);
					novelsHandler.sendMessage(msg);
				} else {
					List<Novel> list = novelService.parseNovelsJson(result);
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_REFRESH_SUCCESS);
					msg.obj = list;
					novelsHandler.sendMessage(msg);
				}
				
			};
		}.start();
	}

	/**
	 * pull up to load more
	 */
	@Override
	public void onLoadingMore() {
		new Thread(){
			public void run() {
				String offset = String.valueOf(novels.size());
				String result = novelService.getNovels(offset, NOVELS_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT);
					novelsHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_REFRESH_LOADING_MORE_ERROR);
					novelsHandler.sendMessage(msg);
				} else {
					List<Novel> list = novelService.parseNovelsJson(result);
					Message msg = novelsHandler.obtainMessage(NOVELS_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS);
					msg.obj = list;
					novelsHandler.sendMessage(msg);
				}
				
			};
		}.start();
	}
}
