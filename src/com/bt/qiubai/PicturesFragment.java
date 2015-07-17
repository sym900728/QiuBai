package com.bt.qiubai;

import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

public class PicturesFragment extends Fragment implements OnRefreshListener{
	
	private RelativeLayout pictures_rel_listview, crl_header_hidden;
	private CommonRefreshListView picturesListView;
	
	private PicturesBaseAdapter picturesBaseAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view_pictures = inflater.inflate(R.layout.pictures_fragment, container, false);
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
			}
		});
		onFirstLoadingPictures();
		return view_pictures;
	}
	
	public void onFirstLoadingPictures(){
		pictures_rel_listview.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				
			};
		}.start();
	}
	
	public void setPicturesFragmentListViewItemSize(){
		
	}
	
	private class PicturesBaseAdapter extends BaseAdapter{
		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public PicturesBaseAdapter(Context context){
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

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			convertView = inflater.inflate(R.layout.pictures_listview_item, null);
			return convertView;
		}
		
	}

	@Override
	public void onDownPullRefresh() {
	}

	@Override
	public void onLoadingMore() {
	}
}
