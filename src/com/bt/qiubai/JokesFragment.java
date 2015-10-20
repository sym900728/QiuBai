package com.bt.qiubai;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.dao.JokeDao;
import com.qiubai.dao.impl.JokeDaoImpl;
import com.qiubai.entity.Joke;
import com.qiubai.service.JokeService;
import com.qiubai.util.CommonRefreshListViewAnimation;
import com.qiubai.util.NetworkUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.util.TimeUtil;
import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JokesFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout jokes_rel_listview, crl_header_hidden;
	private CommonRefreshListView jokesListView;
	
	private JokesBaseAdapter jokesBaseAdapter;
	private JokeService jokeService;
	private JokeDao jokeDao;
	private List<Joke> jokes = new ArrayList<Joke>();
	private SharedPreferencesUtil spUtil;
	
	private final static int JOKES_LISTVIEW_REFRESH_SUCCESS = 1;
	private final static int JOKES_LISTVIEW_REFRESH_NOCONTENT = 2;
	private final static int JOKES_LISTVIEW_REFRESH_ERROR = 3;
	private final static int JOKES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS = 4;
	private final static int JOKES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT = 5;
	private final static int JOKES_LISTVIEW_REFRESH_LOADING_MORE_ERROR = 6;
	private final static int JOKES_LISTVIEW_FIRST_LOADING_SUCCESS = 7;
	private final static int JOKES_LISTVIEW_FIRST_LOADING_ERROR = 8;
	private final static int JOKES_LISTVIEW_FIRST_LOADING_NOCONTENT = 9;
	private final static String JOKES_LISTVIEW_SIZE = "15";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view_jokes = inflater.inflate(R.layout.jokes_fragment, container, false);
		
		jokeService = new JokeService(this.getActivity());
		jokeDao = new JokeDaoImpl(this.getActivity());
		spUtil = new SharedPreferencesUtil(this.getActivity());
		
		jokes_rel_listview = (RelativeLayout) view_jokes.findViewById(R.id.jokes_rel_listview);
		jokesListView = (CommonRefreshListView) view_jokes.findViewById(R.id.jokes_listview);
		crl_header_hidden = (RelativeLayout) view_jokes.findViewById(R.id.crl_header_hidden);
		jokesBaseAdapter  = new JokesBaseAdapter(this.getActivity());
		jokesListView.setAdapter(jokesBaseAdapter);
		jokesListView.setHiddenView(crl_header_hidden);
		jokesListView.setOnRefreshListener(this);
		jokesListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		jokesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Joke joke = jokes.get(position - 1);
				Intent intent = new Intent(getActivity(), JokeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("joke", joke);
				intent.putExtras(bundle);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			}
		});
		loadDatabaseData();
		return view_jokes;
	}
	
	public void loadJokesDataTimer(){
		//判断有没有联网
		if(NetworkUtil.isConnectInternet(this.getActivity())){
			//判断是不是第一次使用 JokesFragment
			if(spUtil.isFirstRun("isJokesFragmentFirstRun")){
				onFirstLoadingJokes();
			} else {
				//判断当前时间跟上一次刷新时间差
				long time = spUtil.getRefreshTime("jokesFragmentLastRefreshTime");
				if(TimeUtil.compareTime(time)){
					CommonRefreshListViewAnimation.moveListView(jokesListView, "jokesFragmentLastRefreshTime");
				}
			}
		}
	}
	
	public void loadDatabaseData(){
		jokes.clear();
		jokes = jokeDao.getJokes(null);;
		if(!jokes.isEmpty()){
			jokesBaseAdapter.notifyDataSetChanged();
			jokesListView.setLastUpdateTime("jokesFragmentLastRefreshTime");
			jokes_rel_listview.setVisibility(View.VISIBLE);
		}
	}
	
	public void setDatabaseData(final List<Joke> list){
		new Thread(){
			public void run() {
				jokeDao.emptyJokeTable();
				jokeDao.addJokes(list);
			};
		}.start();
	}
	
	public void onFirstLoadingJokes(){
		//jokes_rel_listview.setVisibility(View.INVISIBLE);
		new Thread(){
			public void run() {
				String result = jokeService.getJokes("0", JOKES_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_FIRST_LOADING_NOCONTENT);
					jokesHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_FIRST_LOADING_ERROR);
					jokesHandler.sendMessage(msg);
				} else {
					List<Joke> list = jokeService.parseJokesJson(result);
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_FIRST_LOADING_SUCCESS);
					msg.obj = list;
					jokesHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	private class JokesBaseAdapter extends BaseAdapter{
		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public JokesBaseAdapter(Context context){
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return jokes.size();
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
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.jokes_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.jokes_listview_item_tv_content = (TextView) convertView.findViewById(R.id.jokes_listview_item_tv_content);
				viewHolder.jokes_listview_item_tv_zan = (TextView) convertView.findViewById(R.id.jokes_listview_item_tv_zan);
				viewHolder.jokes_listview_item_tv_comment = (TextView) convertView.findViewById(R.id.jokes_listview_item_tv_comment);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Joke joke = jokes.get(position);
			viewHolder.jokes_listview_item_tv_content.setText(joke.getDescription());
			viewHolder.jokes_listview_item_tv_zan.setText(joke.getZan() + " 赞");
			viewHolder.jokes_listview_item_tv_comment.setText(joke.getComments() + " 评论");
			return convertView;
		}
		
		private class ViewHolder{
			TextView jokes_listview_item_tv_content;
			TextView jokes_listview_item_tv_zan;
			TextView jokes_listview_item_tv_comment;
		}
	}
	
	private Handler jokesHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case JOKES_LISTVIEW_REFRESH_SUCCESS:
				jokesListView.hiddenFooterView(true);
				jokesListView.hiddenHeaderView();
				jokes.clear();
				jokes = (List<Joke>) msg.obj;
				jokesBaseAdapter.notifyDataSetChanged();
				jokesListView.updateLastUpdateTime("jokesFragmentLastRefreshTime");
				setDatabaseData(jokes);
				break;
			case JOKES_LISTVIEW_REFRESH_NOCONTENT:
				break;
			case JOKES_LISTVIEW_REFRESH_ERROR:
				jokesListView.hiddenHeaderView();
				break;
			case JOKES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS:
				jokesListView.hiddenFooterView(true);
				addToListJokes((List<Joke>) msg.obj);
				jokesBaseAdapter.notifyDataSetChanged();
				break;
			case JOKES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT:
				jokesListView.hiddenFooterView(false);
				break;
			case JOKES_LISTVIEW_REFRESH_LOADING_MORE_ERROR:
				jokesListView.hiddenFooterView(true);
				break;
			case JOKES_LISTVIEW_FIRST_LOADING_SUCCESS:
				jokes.clear();
				jokes = (List<Joke>) msg.obj;
				jokesBaseAdapter.notifyDataSetChanged();
				jokesListView.updateLastUpdateTime("jokesFragmentLastRefreshTime");
				jokes_rel_listview.setVisibility(View.VISIBLE);
				setDatabaseData(jokes);
				break;
			case JOKES_LISTVIEW_FIRST_LOADING_ERROR:
				break;
			case JOKES_LISTVIEW_FIRST_LOADING_NOCONTENT:
				break;
			}
		};
	};
	
	public void addToListJokes(List<Joke> list){
		for (Joke joke : list) {
			jokes.add(joke);
		}
	}

	/**
	 * pull down refresh
	 */
	@Override
	public void onDownPullRefresh() {
		new Thread(){
			public void run() {
				String result = jokeService.getJokes("0", JOKES_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_REFRESH_NOCONTENT);
					jokesHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_REFRESH_ERROR);
					jokesHandler.sendMessage(msg);
				} else {
					List<Joke> list = jokeService.parseJokesJson(result);
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_REFRESH_SUCCESS);
					msg.obj = list;
					jokesHandler.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * pull up load more
	 */
	@Override
	public void onLoadingMore() {
		new Thread(){
			public void run() {
				String offset = String.valueOf(jokes.size());
				String result = jokeService.getJokes(offset, JOKES_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT);
					jokesHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_REFRESH_LOADING_MORE_ERROR);
					jokesHandler.sendMessage(msg);
				} else {
					List<Joke> list = jokeService.parseJokesJson(result);
					Message msg = jokesHandler.obtainMessage(JOKES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS);
					msg.obj = list;
					jokesHandler.sendMessage(msg);
				}
			};
		}.start();
	}
}
