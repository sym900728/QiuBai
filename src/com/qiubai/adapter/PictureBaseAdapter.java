package com.qiubai.adapter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

import com.bt.qiubai.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureBaseAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Map<String, Object>> listItems;
	private ImageView fpd_image_text;
	private TextView fpd_textTitle_text;
	private TextView fpd_comment;

	private ImageView[] fpd3_image_text = new ImageView[3];
	private TextView fpd3_textTitle_text;
	private TextView fpd3_comment;
	
	private String TAG = "PictureBaseAdapter";

	public PictureBaseAdapter(Context context,
			List<Map<String, Object>> listItems) {
		this.mInflater = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		System.out.println("getItem(): "+listItems.get(position));
		return position;
	}

	@Override
	public long getItemId(int position) {
		System.out.println("position"+position);
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position % 2 == 0) {
			// 当时偶数的时候，返回的是只有一张图片的页面
			convertView = mInflater.inflate(R.layout.fragment_picture_detail,
					null);
			fpd_image_text = (ImageView) convertView
					.findViewById(R.id.fragment_picture_detail_img);
			fpd_textTitle_text = (TextView) convertView
					.findViewById(R.id.fragment_picture_detail_textTitle);
			fpd_comment = (TextView) convertView
					.findViewById(R.id.fragment_picture_detail_comment);
			
			fpd_image_text.setBackgroundResource((Integer) listItems.get(position).get("fpd_image_text"));
			
			
			fpd_textTitle_text.setText((String)listItems.get(position).get("fpd_textTitle_text"));
			fpd_comment.setText((String)listItems.get(position).get("fpd_comment"));

		} else {
			// 当时奇数的时候，返回的是三张图片的页面
			convertView = mInflater.inflate(
					R.layout.fragment_picture_detail_three, null);

			fpd3_image_text[0] = (ImageView) convertView
					.findViewById(R.id.fragment_picture_detail_three_img1);
			fpd3_image_text[1] = (ImageView) convertView
					.findViewById(R.id.fragment_picture_detail_three_img2);
			fpd3_image_text[2] = (ImageView) convertView
					.findViewById(R.id.fragment_picture_detail_three_img3);
			fpd3_textTitle_text = (TextView) convertView
					.findViewById(R.id.fragment_picture_detail_three_textTitle);
			fpd3_comment = (TextView) convertView
					.findViewById(R.id.fragment_picture_detail_three_comment);
			
			fpd3_image_text[0].setBackgroundResource((Integer)listItems.get(position).get("fpd3_image_1_text"));
			fpd3_image_text[1].setBackgroundResource((Integer)listItems.get(position).get("fpd3_image_2_text"));
			fpd3_image_text[2].setBackgroundResource((Integer)listItems.get(position).get("fpd3_image_3_text"));
			fpd3_textTitle_text.setText((String)listItems.get(position).get("fpd3_textTitle_text"));
			fpd3_comment.setText((String)listItems.get(position).get("fpd3_comment"));
		}

		return convertView;

	}

}
