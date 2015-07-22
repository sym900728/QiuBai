package com.qiubai.view;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bt.qiubai.R;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.DensityUtil;

public class CommonRefreshListView extends ListView implements OnScrollListener{
	private View hiddenView, headerView, footerView;
	private ImageView crl_min, crl_hour, crl_clock_bg;
	private TextView crl_time;
	
	private int firstVisibleItemPosition, footerViewHeight, pressDownY;
	private int reachToRefresh = 160; // reach the height to refresh (pull down distance)
	private boolean isScrollToBottom, isLoadingMore = false;
	private OnRefreshListener onRefreshListener;
	private Bitmap bitmap_min, bitmap_clock_bg;
	private String direction = null;
	
	private final static int REFRESH_PULL_DOWN = 0;
	private final static int REFRESH_RELEASE = 1;
	private final static int REFRESH_ING = 2;
	private int currentState = REFRESH_PULL_DOWN;
	private boolean pressDownFirstItemVisible = true;
	
	public CommonRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		initFooterView();
		bitmap_min = BitmapFactory.decodeResource(getResources(), R.drawable.common_refresh_listview_line_min);
		bitmap_clock_bg = BitmapFactory.decodeResource(getResources(), R.drawable.common_refresh_listview_disk);
		this.setOnScrollListener(this);
	}
	
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.onRefreshListener = listener;
	}
	
	/**
	 * set header hidden view
	 * get header hidden view's children's view
	 * @param view
	 */
	public void setHiddenView(View view){
		this.hiddenView = view;
		crl_hour = (ImageView) hiddenView.findViewById(R.id.crl_hour);
		crl_min = (ImageView) hiddenView.findViewById(R.id.crl_min);
		crl_clock_bg = (ImageView) hiddenView.findViewById(R.id.crl_clock_bg);
		initClock();
		initUpdateTime();
	}
	
	private void initUpdateTime(){
		crl_time = (TextView) hiddenView.findViewById(R.id.crL_time);
		crl_time.setText("1分钟前更新");
		crl_time.setTag(System.currentTimeMillis());
	}
	
	/**
	 * initialize clock (hour hand, minute hand, background)
	 */
	private void initClock(){
		Bitmap bitmap_min = BitmapFactory.decodeResource(getResources(), R.drawable.common_refresh_listview_line_min);
		Bitmap alterBitmap_min = BitmapUtil.resizeSquareBitmap(DensityUtil.dip2px(getContext(), 35), bitmap_min);
		Bitmap newBitmap_min = BitmapUtil.rotateBitmap(0, alterBitmap_min);
		
		Bitmap bitmap_hour = BitmapFactory.decodeResource(getResources(), R.drawable.common_refresh_listview_line_hour);
		Bitmap alterBitmap_hour = BitmapUtil.resizeSquareBitmap(DensityUtil.dip2px(getContext(), 35), bitmap_hour);
		
		Bitmap bitmap_clock_bg = BitmapFactory.decodeResource(getResources(), R.drawable.common_refresh_listview_disk);
		Bitmap alterBitmap_clock_bg = BitmapUtil.resizeSquareBitmap(DensityUtil.dip2px(getContext(), 20), bitmap_clock_bg);
		crl_hour.setImageBitmap(alterBitmap_hour);
		crl_min.setImageBitmap(newBitmap_min);
		crl_clock_bg.setImageBitmap(alterBitmap_clock_bg);
	}
	
	/**
	 * initialize header view (set reachToRefresh)
	 */
	private void initHeaderView(){
		headerView = View.inflate(getContext(), R.layout.common_refresh_listview_header, null);
		this.addHeaderView(headerView);
	}
	
	/**
	 * initialize footer view
	 */
	private void initFooterView(){
		footerView = View.inflate(getContext(), R.layout.common_refresh_listview_footer, null);
		footerView.measure(0, 0);
		footerViewHeight = footerView.getMeasuredHeight();
		footerView.setPadding(0, -footerViewHeight, 0, 0);
		this.addFooterView(footerView);
	}
	
	/**
	 * rotate minute hand
	 * @param degree
	 */
	private void rotateMinuteHand(int paddingTop){
		int startOffset = DensityUtil.dip2px(getContext(), 30);
		if (paddingTop > startOffset){
			float degree = ((float)( (float)(paddingTop - startOffset) / (float)(reachToRefresh - startOffset) ))*360;
			//System.out.println("degree:" + degree);
			if(degree > 360.0f){
				degree = 360.0f;
			}
			Bitmap alterBitmap_min = BitmapUtil.resizeSquareBitmap(DensityUtil.dip2px(getContext(), 35), bitmap_min);
			Bitmap newBitmap = BitmapUtil.rotateBitmap(degree, alterBitmap_min);
			crl_min.setImageBitmap(newBitmap);
		}
	}
	
	/**
	 * rotate hour hand
	 * @param degree
	 */
	private void rotateHourHand(float degree){
		Bitmap bitmap_hour = BitmapFactory.decodeResource(getResources(), R.drawable.common_refresh_listview_line_hour);
		Bitmap alterBitmap_hour = BitmapUtil.resizeSquareBitmap(DensityUtil.dip2px(getContext(), 35), bitmap_hour);
		Bitmap newBitmap = BitmapUtil.rotateBitmap(degree, alterBitmap_hour);
		crl_hour.setImageBitmap(newBitmap);
	}
	
	/**
	 * zoom clock background image
	 * @param paddingTop
	 */
	private void zoomClockBackground(int paddingTop){
		int startOffset = DensityUtil.dip2px(getContext(), 30);
		if (paddingTop > startOffset){
			float scale = ( (float)(paddingTop - startOffset) / (float)(reachToRefresh - startOffset) )* 0.7f + 1.0f;
			//System.out.println("scale:" + scale);
			if(scale > 1.7f){
				scale = 1.7f;
			}
			Bitmap alterBitmap_clock_bg = BitmapUtil.resizeSquareBitmap(DensityUtil.dip2px(getContext(), 20), bitmap_clock_bg);
			Bitmap newBitmap_clock_bg = BitmapUtil.zoomBitmap(scale, alterBitmap_clock_bg);
			crl_clock_bg.setImageBitmap(newBitmap_clock_bg);
		}
	}
	
	/**
	 * header view clock's hour hand and minute hand rotate animation
	 */
	private void headerViewAnimation(){
		Animation animation_min = AnimationUtils.loadAnimation(getContext(), R.anim.crl_min_rotate);
		Animation animation_hour = AnimationUtils.loadAnimation(getContext(), R.anim.crl_hour_rotate);
		crl_min.startAnimation(animation_min);
		crl_hour.startAnimation(animation_hour);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pressDownY = (int) ev.getY();
			if(firstVisibleItemPosition != 0){
				pressDownFirstItemVisible = false;
			} else {
				pressDownFirstItemVisible = true;
			}
			//System.out.println(firstVisibleItemPosition);
			break;
		case MotionEvent.ACTION_MOVE:
			int touchMoveY = (int) ev.getY();
			int paddingTop = (int)(touchMoveY - pressDownY)/3;
			if(touchMoveY <= pressDownY){
				direction = "move up";
			} else {
				direction = "move down";
			}
			if(firstVisibleItemPosition == 0 && currentState == REFRESH_PULL_DOWN){
				if(pressDownFirstItemVisible){
					if(paddingTop >= 0){// pull down
						if(paddingTop > reachToRefresh){
							//System.out.println("RELEASE");
							currentState = REFRESH_RELEASE;
						}
					}
					setUpdateTimeView();
					zoomClockBackground(paddingTop);
					rotateMinuteHand(paddingTop);
					headerView.setPadding(0, paddingTop, 0, 0);
					this.onInterceptTouchEvent(ev);
					return true;
				} else {
					pressDownY = touchMoveY;
					pressDownFirstItemVisible = true;
				}
				
			} else if (firstVisibleItemPosition == 0 && currentState == REFRESH_RELEASE){
				if(paddingTop >= reachToRefresh){
					currentState = REFRESH_RELEASE;
				} else {
					currentState = REFRESH_PULL_DOWN;
				}
				zoomClockBackground(paddingTop);
				rotateMinuteHand(paddingTop);
				headerView.setPadding(0, paddingTop, 0, 0);
				return true;
			} 
			break;
		case MotionEvent.ACTION_UP:
			if (currentState == REFRESH_RELEASE) {
				headerView.setPadding(0, reachToRefresh, 0, 0); // show whole header
				currentState = REFRESH_ING; // into refreshing status
				this.headerViewAnimation();
				if(onRefreshListener != null){
					onRefreshListener.onDownPullRefresh();
				}
			} else if(currentState == REFRESH_PULL_DOWN){
				headerView.setPadding(0, -reachToRefresh, 0, 0);
				initClock();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING){
			if(isScrollToBottom && !isLoadingMore) {
				isLoadingMore = true;
				//System.out.println("加载更多数据");
				footerView.setPadding(0, 0, 0, 0);
				this.setSelection(this.getCount());
				if(onRefreshListener != null){
					onRefreshListener.onLoadingMore();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		//System.out.println("firstVisibleItemPosition:" + firstVisibleItemPosition);
		firstVisibleItemPosition = firstVisibleItem;
		if (getLastVisiblePosition() == (totalItemCount - 1)) {
			if("move up".equals(direction)){
				isScrollToBottom = true;
			} else {
				isScrollToBottom = false;
			}
		} else {
			isScrollToBottom = false;
		}
	}
	
	/**
	 * hidden header view
	 * @param flag  true: set time tag; false: don't set time tag
	 */
	public void hiddenHeaderView(boolean flag){
		currentState = REFRESH_PULL_DOWN;
		headerView.setPadding(0, 0, 0, 0);
		crl_min.clearAnimation();
		crl_hour.clearAnimation();
		initClock();  
		if(flag){
			crl_time.setTag(System.currentTimeMillis());
		}
	}
	
	/**
	 * hide footer view
	 * @param flag true: more datum loading; false: no more content
	 */
	public void hiddenFooterView(boolean flag){
		TextView comment_listview_footer_loading = (TextView) footerView.findViewById(R.id.comment_listview_footer_loading);
		if(flag){
			comment_listview_footer_loading.setText("更多数据加载中");
			footerView.setPadding(0, -footerViewHeight, 0, 0);
			isLoadingMore = false;
		} else {
			comment_listview_footer_loading.setText("没有更多内容了");
		}
	}
	
	public void setUpdateTimeView(){
		long crl_time_tag = (Long) crl_time.getTag();
		long crl_time_current = System.currentTimeMillis();
		int second = (int)((crl_time_current - crl_time_tag) / 1000);
		if (second < 60){
			crl_time.setText("1分钟前更新");
		} else {
			int minute = (int)(second / 60);
			if(minute < 60){
				crl_time.setText(minute + "分钟前更新");
			} else {
				int hour = (int)(minute / 60);
				minute = minute % 60;
				if(hour < 24){
					crl_time.setText(hour + "小时" + minute +"分钟前更新");
				} else {
					Calendar calendar  = Calendar.getInstance();
					calendar.setTimeInMillis(crl_time_current);
					calendar.get(Calendar.MONTH);
					calendar.get(Calendar.DAY_OF_MONTH);
					calendar.get(Calendar.HOUR_OF_DAY);
					calendar.get(Calendar.MINUTE);
					crl_time.setText(calendar.get(Calendar.MONTH) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日"
							+ calendar.get(Calendar.HOUR_OF_DAY) + "时" + calendar.get(Calendar.MINUTE) + "分跟新");
				}
			}
		}
	}
	
	/**
	 * callback method
	 */
	public interface OnRefreshListener{
		public void onDownPullRefresh();
		public void onLoadingMore();
	}

}
