package com.bt.qiubai;

import java.util.zip.Inflater;

import com.qiubai.view.CommonRefreshListView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

public class NovelsFragment extends Fragment{
	
	private RelativeLayout crl_header_hidden;
	
	private CommonRefreshListView novelsListView;
	
	private NovelsBaseAdapter novelsBaseAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View novels = inflater.inflate(R.layout.novels_fragment, container, false);
		
		crl_header_hidden = (RelativeLayout) novels.findViewById(R.id.crl_header_hidden);
		novelsBaseAdapter = new NovelsBaseAdapter(this.getActivity());
		novelsListView = (CommonRefreshListView) novels.findViewById(R.id.novels_listview);
		novelsListView.setAdapter(novelsBaseAdapter);
		novelsListView.setHiddenView(crl_header_hidden);
		return novels;
	}
	
	private class NovelsBaseAdapter extends BaseAdapter{
		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public NovelsBaseAdapter(Context context) {
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
			convertView = inflater.inflate(R.layout.novels_listview_item, null);
			return convertView;
		}
		
	}
}
