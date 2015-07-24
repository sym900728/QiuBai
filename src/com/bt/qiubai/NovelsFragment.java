package com.bt.qiubai;

import java.util.ArrayList;
import java.util.List;

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

import com.qiubai.entity.Novel;
import com.qiubai.service.NovelService;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.DensityUtil;
import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;

public class NovelsFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout novels_rel_listview, crl_header_hidden;
	private TextView novels_listview_item_tv_title, novels_listview_item_tv_description, novels_listview_item_tv_comment;
	private ImageView novels_listview_item_iv_picture;
	
	private CommonRefreshListView novelsListView;
	
	private NovelsBaseAdapter novelsBaseAdapter;
	
	private List<Novel> novels = new ArrayList<Novel>();
	private NovelService novelService = new NovelService();
	
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
		novels_rel_listview = (RelativeLayout) view_novels.findViewById(R.id.novels_rel_listview);
		crl_header_hidden = (RelativeLayout) view_novels.findViewById(R.id.crl_header_hidden);
		novelsBaseAdapter = new NovelsBaseAdapter(this.getActivity());
		novelsListView = (CommonRefreshListView) view_novels.findViewById(R.id.novels_listview);
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
		onFirstLoadingNovels();
		return view_novels;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.novels_listview_item, null);
			//System.out.println("position: " + position);
			Novel novel = novels.get(position);
			novels_listview_item_tv_title = (TextView) convertView.findViewById(R.id.novels_listview_item_tv_title);
			novels_listview_item_tv_title.setText(novel.getTitle());
			novels_listview_item_tv_description = (TextView) convertView.findViewById(R.id.novels_listview_item_tv_description);
			novels_listview_item_tv_description.setText(novel.getDescription());
			novels_listview_item_tv_comment = (TextView) convertView.findViewById(R.id.novels_listview_item_tv_comment);
			novels_listview_item_tv_comment.setText(novel.getComments() + " 评论");
			novels_listview_item_iv_picture = (ImageView) convertView.findViewById(R.id.novels_listview_item_iv_picture);
			novels_listview_item_iv_picture.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.common_listview_item_image_default));
			if(!"default".equals(novel.getImage())){
				getImage(novel.getImage(), novels_listview_item_iv_picture);
			}
			return convertView;
		}
		
	}
	
	/**
	 * get novel image
	 * @param url
	 * @param iv
	 */
	public void getImage(final String url, final ImageView iv){
		new Thread(){
			public void run() {
				final Bitmap bitmap = novelService.getImage(url);
				if(bitmap != null){
					novelsHandler.post(new Runnable() {
						@Override
						public void run() {
							iv.setImageBitmap(BitmapUtil.resizeBitmap(DensityUtil.dip2px(NovelsFragment.this.getActivity(), 80), DensityUtil.dip2px(NovelsFragment.this.getActivity(), 80),bitmap));
						}
					});
				}
			};
		}.start();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler novelsHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOVELS_LISTVIEW_REFRESH_SUCCESS:
				novelsListView.hiddenHeaderView(true);
				novelsListView.hiddenFooterView(true);
				novels.clear();
				novels = (List<Novel>) msg.obj;
				novelsBaseAdapter.notifyDataSetChanged();
				break;
			case NOVELS_LISTVIEW_REFRESH_NOCONTENT:
				break;
			case NOVELS_LISTVIEW_REFRESH_ERROR:
				novelsListView.hiddenHeaderView(false);
				break;
			case NOVELS_LISTVIEW_FIRST_LOADING_SUCCESS:
				novels.clear();
				novels =  (List<Novel>) msg.obj;
				novelsBaseAdapter.notifyDataSetChanged();
				novels_rel_listview.setVisibility(View.VISIBLE);
				break;
			case NOVELS_LISTVIEW_FIRST_LOADING_NOCONTENT:
				break;
			case NOVELS_LISTVIEW_FIRST_LOADING_ERROR:
				break;
			case NOVELS_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS:
				novelsListView.hiddenFooterView(true);
				addToListNovels((List<Novel>) msg.obj);
				novelsBaseAdapter.notifyDataSetChanged();
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
