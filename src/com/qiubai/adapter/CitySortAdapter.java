package com.qiubai.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bt.qiubai.R;
import com.qiubai.entity.CitySortModel;

public class CitySortAdapter extends BaseAdapter implements SectionIndexer {

	
	private List<CitySortModel> list = null;
	private Context context;

	public CitySortAdapter(List<CitySortModel> list, Context context) {
		this.list = list;
		this.context = context;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<CitySortModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final CitySortModel mContent = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.city_list_item, null);
			viewHolder.tvLetter = (TextView) convertView
					.findViewById(R.id.city_catalog);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.city_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 根据position获取分类的首字母的char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		
		// SpannableString msp = null;
		// msp = new SpannableString(list.get(position).getName());

		// msp.setSpan(new ForegroundColorSpan(Color.BLUE), 1, 3,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// viewHolder.tvTitle.setText(msp);
		viewHolder.tvTitle.setText(list.get(position).getName());
		return convertView;
	}

	final static class ViewHolder {
		private TextView tvLetter;
		private TextView tvTitle;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

}
