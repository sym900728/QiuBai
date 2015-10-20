package com.bt.qiubai;

import java.util.ArrayList;
import java.util.List;

import com.qiubai.entity.Picture;
import com.qiubai.entity.PictureDetail;
import com.qiubai.service.PictureDetailService;
import com.qiubai.service.PictureService;
import com.qiubai.util.NetworkUtil;
import com.qiubai.util.PropertiesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PictureActivity extends Activity implements OnClickListener{
	
	private RelativeLayout picture_title_back, picture_title_action_bar;
	private RelativeLayout picture_title_rel_comment;
	private LinearLayout picture_action_share, picture_action_collect, picture_action_comment;
	private TextView picture_tv_title, picture_tv_number, picture_tv_content, picture_tv_comments;
	
	private int picture_id = 0, picture_counts = 0;
	
	private ViewPager picture_viewpager;
	private List<View> list;  //表示装载滑动的布局
	private List<PictureDetail> pictureDetails = new ArrayList<PictureDetail>();
	private PicturePagerAdpater picturePagerAdpater;
	
	private Dialog pictureActionDialog;
	private PictureDetailService pictureDetailService;
	private PictureService pictureService;
	private PropertiesUtil propUtil;
	private final static int PICTURE_DETAIL_NOCONTENT = 0;
	private final static int PICTURE_DETAIL_ERROR = 1;
	private final static int PICTURE_DETAIL_SUCCESS = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_activity);
		
		pictureDetailService = new PictureDetailService(PictureActivity.this);
		pictureService = new PictureService(PictureActivity.this);
		propUtil = new PropertiesUtil(PictureActivity.this);
		
		if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
		}
		
		Intent intent = getIntent();
		Picture picture = (Picture) intent.getSerializableExtra("picture");
		picture_id = picture.getId();
		picture_counts = picture.getCounts();
		
		picture_title_back = (RelativeLayout) findViewById(R.id.picture_title_back);
		picture_title_rel_comment = (RelativeLayout) findViewById(R.id.picture_title_rel_comment);
		picture_title_action_bar = (RelativeLayout) findViewById(R.id.picture_title_action_bar);
		picture_title_back.setOnClickListener(this);
		picture_title_rel_comment.setOnClickListener(this);
		picture_title_action_bar.setOnClickListener(this);
		picture_tv_title = (TextView) findViewById(R.id.picture_tv_title);
		picture_tv_title.setText(picture.getTitle());
		picture_tv_number = (TextView) findViewById(R.id.picture_tv_number);
		picture_tv_number.setText("1/" + picture.getCounts());
		picture_tv_content = (TextView) findViewById(R.id.picture_tv_content);
		picture_tv_comments = (TextView) findViewById(R.id.picture_tv_comments);
		picture_tv_comments.setText(picture.getComments() + " 评论");
		
		pictureActionDialog = new Dialog(PictureActivity.this, R.style.CommonActionDialog);
		pictureActionDialog.setContentView(R.layout.picture_action_bar);
		pictureActionDialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		
		picture_action_share = (LinearLayout) pictureActionDialog.findViewById(R.id.picture_action_share);
		picture_action_collect = (LinearLayout) pictureActionDialog.findViewById(R.id.picture_action_collect);
		picture_action_comment = (LinearLayout) pictureActionDialog.findViewById(R.id.picture_action_comment);
		picture_action_share.setOnClickListener(this);
		picture_action_collect.setOnClickListener(this);
		picture_action_comment.setOnClickListener(this);
		
		picture_viewpager = (ViewPager) findViewById(R.id.picture_viewpager);
		createImageView(picture_counts);
		
		picturePagerAdpater = new PicturePagerAdpater();
		picture_viewpager.setAdapter(picturePagerAdpater);
		picture_viewpager.setCurrentItem(0);
		picture_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				int index = arg0 + 1;
				picture_tv_number.setText(index + "/" + picture_counts);
				if(pictureDetails.size() > 0){
					picture_tv_content.setText(pictureDetails.get(arg0).getContent());
				} else {
					picture_tv_content.setText("");
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
			
		});
		getPictureDetails(picture_id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPictureComments();
	}
	
	/**
	 * get picture comments
	 */
	public void getPictureComments(){
		new Thread(){
			public void run() {
				pictureHandler.post(new Runnable() {
					final String result = pictureService.getPictureComments(String.valueOf(picture_id));
					@Override
					public void run() {
						if(!"error".equals(result) && !"nocontent".equals(result)){
							picture_tv_comments.setText(result + " 评论");
						}
					}
				});
			};
		}.start();
	}
	
	/**
	 * get picture details
	 * @param pictureid
	 */
	public void getPictureDetails(final int pictureid){
		new Thread(){
			public void run() {
				String result = pictureDetailService.getPictureDetails(String.valueOf(pictureid));
				if("nocontent".equals(result)){
					Message msg = pictureHandler.obtainMessage(PICTURE_DETAIL_NOCONTENT);
					pictureHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = pictureHandler.obtainMessage(PICTURE_DETAIL_ERROR);
					pictureHandler.sendMessage(msg);
				} else {
					List<PictureDetail> list = pictureDetailService.parsePictureDetailsJson(result);
					Message msg = pictureHandler.obtainMessage(PICTURE_DETAIL_SUCCESS);
					msg.obj = list;
					pictureHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	public void createImageView(int size){
		list = new ArrayList<View>();
		for(int i = 0; i < size; i++){
			View view = getLayoutInflater().inflate(R.layout.picture_viewpager_item, null);
			list.add(view);
		}
	}
	
	public void setViewPagerContent(List<PictureDetail> pictureDetails){
		PictureDetail pd = pictureDetails.get(0);
		picture_tv_content.setText(pd.getContent());
		for(int i = 0; i < pictureDetails.size(); i ++){
			PictureDetail pd2 = pictureDetails.get(i);
			View view = list.get(i);
			ImageView picture_viewpager_item_iv = (ImageView) view.findViewById(R.id.picture_viewpager_item_iv);
			getViewPagerItemImage(picture_viewpager_item_iv, pd2);
		}
	}
	
	public void getViewPagerItemImage(final ImageView iv, final PictureDetail pd){
		new Thread(){
			public void run() {
				final Bitmap bitmap_file = BitmapFactory.decodeFile(propUtil.readProperties("config.properties", "picturedetails_picture_path") + "/" + pd.getPictureid()  + "/" + pd.getId() + ".png");
				if(bitmap_file != null){
					pictureHandler.post(new Runnable() {
						
						@Override
						public void run() {
							iv.setImageBitmap(bitmap_file);
						}
					});
				} else {
					new Thread(){
						public void run() {
							final Bitmap bitmap_remote = pictureDetailService.getImage(pd.getImage());
							if(bitmap_remote != null){
								pictureDetailService.storeImage(bitmap_remote, String.valueOf(pd.getPictureid()), pd.getId() + ".png");
								pictureHandler.post(new Runnable() {
									
									@Override
									public void run() {
										iv.setImageBitmap(bitmap_remote);
									}
								});
							}
						};
					}.start();
				}
			};
		}.start();
	}
	
	private class PicturePagerAdpater extends PagerAdapter{
		
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager)container).addView(list.get(position));
			return list.get(position);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(list.get(position));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.picture_title_back:
			PictureActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
			
		case R.id.picture_title_rel_comment:
			Intent intent_to_comment1 = new Intent(PictureActivity.this, CommentActivity.class);
			intent_to_comment1.putExtra("belong", "picture");
			intent_to_comment1.putExtra("newsid", picture_id);
			startActivity(intent_to_comment1);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
			
		case R.id.picture_title_action_bar:
			pictureActionDialog.show();
			break;
			
		case R.id.picture_action_share:
			pictureActionDialog.dismiss();
			break;
			
		case R.id.picture_action_collect:
			pictureActionDialog.dismiss();
			break;
			
		case R.id.picture_action_comment:
			pictureActionDialog.dismiss();
			Intent intent_to_comment2 = new Intent(PictureActivity.this, CommentActivity.class);
			intent_to_comment2.putExtra("belong", "picture");
			intent_to_comment2.putExtra("newsid", picture_id);
			startActivity(intent_to_comment2);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			PictureActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler pictureHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PICTURE_DETAIL_NOCONTENT:
				break;
			case PICTURE_DETAIL_ERROR:
				break;
			case PICTURE_DETAIL_SUCCESS:
				pictureDetails = (List<PictureDetail>) msg.obj;
				setViewPagerContent(pictureDetails);
				break;
			}
		};
	};
}
