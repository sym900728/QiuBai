package com.bt.qiubai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiubai.dao.VideoDao;
import com.qiubai.dao.impl.VideoDaoImpl;
import com.qiubai.entity.Novel;
import com.qiubai.entity.Video;
import com.qiubai.service.VideoService;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.CommonRefreshListViewAnimation;
import com.qiubai.util.DensityUtil;
import com.qiubai.util.NetworkUtil;
import com.qiubai.util.PropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.util.TimeUtil;
import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

public class VideosFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout videos_rel_listview, crl_header_hidden;
	
	private CommonRefreshListView videosListView;
	private int screenWidth, screenHeight, requireWidth, requireHeight;
	private Bitmap bitmap_default;
	
	private VideosBaseAdapter videosBaseAdapter;
	private VideoService videoService;
	private PropertiesUtil propUtil;
	private SharedPreferencesUtil spUtil;
	private VideoDao videoDao;
	private List<Video> videos = new ArrayList<Video>();
	private Map<Integer, Bitmap> map = new HashMap<Integer, Bitmap>();
	
	private final static int VIDEOS_LISTVIEW_FIRST_LOADING_NOCONTENT = 0;
	private final static int VIDEOS_LISTVIEW_FIRST_LOADING_ERROR = 1;
	private final static int VIDEOS_LISTVIEW_FIRST_LOADING_SUCCESS = 2;
	private final static int VIDEOS_LISTVIEW_REFRESH_NOCONTENT = 3;
	private final static int VIDEOS_LISTVIEW_REFRESH_ERROR = 4;
	private final static int VIDEOS_LISTVIEW_REFRESH_SUCCESS = 5;
	private final static int VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT = 6;
	private final static int VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_ERROR = 7;
	private final static int VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS = 8;
	private final static String VIDEOS_LISTVIEW_SIZE = "10";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view_videos = inflater.inflate(R.layout.videos_fragment, container, false);
		
		videoService = new VideoService(getActivity());
		propUtil = new PropertiesUtil(getActivity());
		spUtil = new SharedPreferencesUtil(this.getActivity());
		videoDao = new VideoDaoImpl(this.getActivity());
		
		screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		requireWidth = screenWidth - DensityUtil.dip2px(getActivity(), 20);
		requireHeight = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 2 / 3;
		bitmap_default = BitmapUtil.resizeBitmap(requireWidth, requireHeight, 
				BitmapFactory.decodeResource(getResources(), R.drawable.common_listview_item_image_default2));
		
		videos_rel_listview = (RelativeLayout) view_videos.findViewById(R.id.videos_rel_listview);
		videosListView = (CommonRefreshListView) view_videos.findViewById(R.id.videos_listview);
		crl_header_hidden = (RelativeLayout) view_videos.findViewById(R.id.crl_header_hidden);
		videosBaseAdapter = new VideosBaseAdapter(getActivity());
		videosListView.setAdapter(videosBaseAdapter);
		videosListView.setHiddenView(crl_header_hidden);
		videosListView.setOnRefreshListener(this);
		videosListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		videosListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Video video = videos.get(position - 1);
				Intent intent = new Intent(getActivity(), VideoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("video", video);
				intent.putExtras(bundle);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			}
		});
		loadDatabaseData();
		return view_videos;
	}
	
	public void loadVideosDataTimer(){
		if(NetworkUtil.isConnectInternet(this.getActivity())){
			if(spUtil.isFirstRun("isVideosFragmentFirstRun")){
				onFirstLoadingVideos();
			} else {
				if(TimeUtil.compareTime(spUtil.getRefreshTime("videosFragmentLastRefreshTime"))){
					CommonRefreshListViewAnimation.moveListView(videosListView, "videosFragmentLastRefreshTime");
				}
			}
		}
	}
	
	public void loadDatabaseData(){
		videos.clear();
		videos = videoDao.getVideos(null);
		if(!videos.isEmpty()){
			videosBaseAdapter.notifyDataSetChanged();
			videosListView.setLastUpdateTime("videosFragmentLastRefreshTime");
			videos_rel_listview.setVisibility(View.VISIBLE);
		}
	}
	
	public void setDatabaseData(final List<Video> list){
		new Thread(){
			public void run() {
				videoDao.emptyVideoTable();
				videoDao.addVideos(list);
			};
		}.start();
	}
	
	public void onFirstLoadingVideos(){
		//videos_rel_listview.setVisibility(View.INVISIBLE);
		new Thread(){
			public void run() {
				String result = videoService.getVideos("0", VIDEOS_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_FIRST_LOADING_NOCONTENT);
					videosHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_FIRST_LOADING_ERROR);
					videosHandler.sendMessage(msg);
				} else {
					List<Video> list = videoService.parseVideosJson(result);
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_FIRST_LOADING_SUCCESS);
					msg.obj = list;
					videosHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	@Override
	public void onDownPullRefresh() {
		new Thread(){
			public void run() {
				String result = videoService.getVideos("0", VIDEOS_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_REFRESH_NOCONTENT);
					videosHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_REFRESH_ERROR);
					videosHandler.sendMessage(msg);
				} else {
					List<Video> list = videoService.parseVideosJson(result);
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_REFRESH_SUCCESS);
					msg.obj = list;
					videosHandler.sendMessage(msg);
				}
			};
		}.start();
	}

	@Override
	public void onLoadingMore() {
		new Thread(){
			public void run() {
				String offset = String.valueOf(videos.size());
				String result = videoService.getVideos(offset, VIDEOS_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT);
					videosHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_ERROR);
					videosHandler.sendMessage(msg);
				} else {
					List<Video> list = videoService.parseVideosJson(result);
					Message msg = videosHandler.obtainMessage(VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS);
					msg.obj = list;
					videosHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	private Handler videosHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case VIDEOS_LISTVIEW_FIRST_LOADING_NOCONTENT:
				break;
			case VIDEOS_LISTVIEW_FIRST_LOADING_ERROR:
				break;
			case VIDEOS_LISTVIEW_FIRST_LOADING_SUCCESS://第一次加载数据成功
				videos.clear();
				videos =  (List<Video>) msg.obj;
				videosBaseAdapter.notifyDataSetChanged();
				videosListView.updateLastUpdateTime("videosFragmentLastRefreshTime");
				videos_rel_listview.setVisibility(View.VISIBLE);
				setDatabaseData(videos);
				break;
			case VIDEOS_LISTVIEW_REFRESH_NOCONTENT:
				break;
			case VIDEOS_LISTVIEW_REFRESH_ERROR:
				videosListView.hiddenHeaderView();
				break;
			case VIDEOS_LISTVIEW_REFRESH_SUCCESS://下拉刷新加载数据成功
				videosListView.hiddenFooterView(true);
				videosListView.hiddenHeaderView();
				videos.clear();
				videos = (List<Video>) msg.obj;
				videosBaseAdapter.notifyDataSetChanged();
				videosListView.updateLastUpdateTime("videosFragmentLastRefreshTime");
				setDatabaseData(videos);
				break;
			case VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT:
				videosListView.hiddenFooterView(false);
				break;
			case VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_ERROR:
				videosListView.hiddenFooterView(true);
				break;
			case VIDEOS_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS:
				videosListView.hiddenFooterView(true);
				addToListVideos((List<Video>) msg.obj);
				videosBaseAdapter.notifyDataSetChanged();
				//storeBitmapToMap((List<Video>) msg.obj);
				break;
			}
		};
	};
	
	public void addToListVideos(List<Video> list){
		for (Video video : list) {
			videos.add(video);
		}
	}
	
	private class VideosBaseAdapter extends BaseAdapter{

		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public VideosBaseAdapter(Context context){
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return videos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.videos_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.videos_listview_item_rel = (RelativeLayout) convertView.findViewById(R.id.videos_listview_item_rel);
				viewHolder.videos_listview_item_iv = (ImageView) convertView.findViewById(R.id.videos_listview_item_iv);
				viewHolder.videos_listview_item_tv_title = (TextView) convertView.findViewById(R.id.videos_listview_item_tv_title);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Video video = videos.get(position);
			
			android.widget.LinearLayout.LayoutParams lp_videos_listview_item_rel = (android.widget.LinearLayout.LayoutParams) 
					viewHolder.videos_listview_item_rel.getLayoutParams();
			lp_videos_listview_item_rel.width = requireWidth;
			lp_videos_listview_item_rel.height = requireHeight;
			viewHolder.videos_listview_item_rel.setLayoutParams(lp_videos_listview_item_rel);
			viewHolder.videos_listview_item_iv.setImageBitmap(bitmap_default);
			viewHolder.videos_listview_item_tv_title.setText(video.getTitle());
			viewHolder.videos_listview_item_iv.setTag(video.getImage());
			loadImage(video, viewHolder.videos_listview_item_iv, requireWidth, requireHeight);
			return convertView;
		}
		
		private class ViewHolder{
			RelativeLayout videos_listview_item_rel;
			ImageView videos_listview_item_iv;
			TextView videos_listview_item_tv_title;
		}
	}
	
	public void loadImage(final Video video, final ImageView iv, final int width, final int height){
		Bitmap bitmap_map = map.get(video.getId());
		if(bitmap_map != null){
			iv.setImageBitmap(bitmap_map);
		} else {
			new Thread(){
				public void run() {
					Bitmap bitmap_file = BitmapFactory.decodeFile(propUtil.readProperties("config.properties", "videos_picture_path") + "/" + video.getId() + ".png");
					if(bitmap_file != null){
						final Bitmap bitmap_file_changed = BitmapUtil.resizeBitmap(width, height, bitmap_file);
						map.put(video.getId(), bitmap_file_changed);
						videosHandler.post(new Runnable() {
							
							@Override
							public void run() {
								iv.setImageBitmap(bitmap_file_changed);
							}
						});
					} else {
						new Thread(){
							public void run() {
								Bitmap bitmap_remote = videoService.getImage(video.getImage());
								if(bitmap_remote != null){
									final Bitmap bitmap_remote_changed = BitmapUtil.resizeBitmap(width, height, bitmap_remote);
									map.put(video.getId(), bitmap_remote_changed);
									videoService.storeImage(bitmap_remote, video.getId() + ".png");
									videosHandler.post(new Runnable() {
										
										@Override
										public void run() {
											String str = (String) iv.getTag();
											if(video.getImage().equals(str)){
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
	
	public void storeBitmapToMap(List<Video> list){
		for(final Video video : list){
			if(!"default".equals(video.getImage()) && !"".equals(video.getImage())){
				new Thread(){
					public void run() {
						Bitmap bitmap = videoService.getImage(video.getImage());
						if(bitmap != null){
							Bitmap bitmap_changed = BitmapUtil.resizeBitmap(requireWidth, requireHeight, bitmap);
							map.put(video.getId(), bitmap_changed);
							videoService.storeImage(bitmap_changed, video.getId() + ".png");
						}
					};
				}.start();
			}
		}
	}
}
