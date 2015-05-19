package com.bt.qiubai;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;

public class JokesFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout jokes_rel_listview, crl_header_hidden;
	private CommonRefreshListView jokesListView;
	
	private JokesBaseAdapter jokesBaseAdapter;
	
	private final static int JOKES_LISTVIEW_REFRESH_SUCCESS = 1;
	private final static int JOKES_LISTVIEW_REFRESH_NOCONTENT = 2;
	private final static int JOKES_LISTVIEW_REFRESH_ERROR = 3;
	private final static int JOKES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS = 4;
	private final static int JOKES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT = 5;
	private final static int JOKES_LISTVIEW_REFRESH_LOADING_MORE_ERROR = 6;
	private final static int JOKES_LISTVIEW_FIRST_LOADING_SUCCESS = 7;
	private final static int JOKES_LISTVIEW_FIRST_LOADING_ERROR = 8;
	private final static int JOKES_LISTVIEW_FIRST_LOADING_NOCONTENT = 9;
	private final static String JOKES_LISTVIEW_SIZE = "20";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view_jokes = inflater.inflate(R.layout.jokes_fragment, container, false);
		jokes_rel_listview = (RelativeLayout) view_jokes.findViewById(R.id.jokes_rel_listview);
		jokesListView = (CommonRefreshListView) view_jokes.findViewById(R.id.jokes_listview);
		crl_header_hidden = (RelativeLayout) view_jokes.findViewById(R.id.crl_header_hidden);
		jokesBaseAdapter  = new JokesBaseAdapter(this.getActivity());
		jokesListView.setAdapter(jokesBaseAdapter);
		jokesListView.setHiddenView(crl_header_hidden);
		jokesListView.setOnRefreshListener(this);
		return view_jokes;
	}
	
	private class JokesBaseAdapter extends BaseAdapter{
		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public JokesBaseAdapter(Context context){
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return 20;
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
			convertView = inflater.inflate(R.layout.jokes_listview_item, null);
			return convertView;
		}
		
	}
	
	private Handler jokesHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case JOKES_LISTVIEW_REFRESH_SUCCESS:
				break;
			case JOKES_LISTVIEW_REFRESH_NOCONTENT:
				break;
			case JOKES_LISTVIEW_REFRESH_ERROR:
				break;
			case JOKES_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS:
				break;
			case JOKES_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT:
				break;
			case JOKES_LISTVIEW_REFRESH_LOADING_MORE_ERROR:
				break;
			case JOKES_LISTVIEW_FIRST_LOADING_SUCCESS:
				break;
			case JOKES_LISTVIEW_FIRST_LOADING_ERROR:
				break;
			case JOKES_LISTVIEW_FIRST_LOADING_NOCONTENT:
				break;
			}
		};
	};

	/**
	 * pull down refresh
	 */
	@Override
	public void onDownPullRefresh() {
		new Thread(){
			public void run() {
				
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
				
			};
		}.start();
	}
}
