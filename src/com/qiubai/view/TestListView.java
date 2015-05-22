package com.qiubai.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.bt.qiubai.R;

public class TestListView extends ListView implements OnScrollListener{
	
	private View headerView;
	private float pressDownY;
	private int firstVisibleItem, globalPaddingTop;
	private boolean isScrollToBottom, isLoadingMore = false;
	private int reachToRefresh = 160; // reach the height to refresh (pull down distance)

	private final static int REFRESH_PULL_DOWN = 0;
	private final static int REFRESH_RELEASE = 1;
	private final static int REFRESH_ING = 2;
	private int currentState = REFRESH_PULL_DOWN;
	
	public TestListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
		initHeaderView();
	}
	
	public void initHeaderView(){
		headerView = View.inflate(getContext(), R.layout.common_refresh_listview_header, null);
		this.addHeaderView(headerView);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pressDownY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float touchMoveY = ev.getY();
			float offsetY = touchMoveY - pressDownY;
			int paddingTop = (int)(touchMoveY - pressDownY)/3;
			globalPaddingTop = paddingTop;
			
			if(firstVisibleItem == 0 && currentState == REFRESH_PULL_DOWN){
				headerView.setPadding(0, paddingTop, 0, 0);
				if(paddingTop >= reachToRefresh){
					currentState = REFRESH_RELEASE;
				} else {
					currentState = REFRESH_PULL_DOWN;
				}
				this.onInterceptTouchEvent(ev);
				return true;
			} else if(firstVisibleItem == 0 && currentState == REFRESH_RELEASE){
				headerView.setPadding(0, paddingTop, 0, 0);
				if(paddingTop >= reachToRefresh){
					currentState = REFRESH_RELEASE;
				} else {
					currentState = REFRESH_PULL_DOWN;
				}
				this.onInterceptTouchEvent(ev);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(currentState == REFRESH_PULL_DOWN){
				translateListView(true);
			} else if ( currentState == REFRESH_RELEASE){
				currentState = REFRESH_ING;
				translateListView(false);
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * hidden header view
	 * @param flag  true: set time tag; false: don't set time tag
	 */
	public void hiddenHeaderView(boolean flag){
		currentState = REFRESH_PULL_DOWN;
		headerView.setPadding(0, 0, 0, 0);
		/*crl_min.clearAnimation();
		crl_hour.clearAnimation();
		initClock();  
		if(flag){
			crl_time.setTag(System.currentTimeMillis());
		}*/
	}
	
	public void translateListView(final boolean flag){
		final ObjectAnimator animator = ObjectAnimator.ofFloat(headerView, "translationX", 0, 0);
		animator.setDuration(200);
		animator.setInterpolator(new LinearInterpolator());
		animator.start();
		animator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {}
			
			@Override
			public void onAnimationRepeat(Animator animation) {}
			
			@Override
			public void onAnimationEnd(Animator animation) {}
			
			@Override
			public void onAnimationCancel(Animator animation) {}
			
		});
		animator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if(flag){
					headerView.setPadding(0, globalPaddingTop - (int)(((float)animator.getCurrentPlayTime())/200 * globalPaddingTop), 0, 0);
				} else {
					headerView.setPadding(0, reachToRefresh + (globalPaddingTop - reachToRefresh) - (int)(((float)animator.getCurrentPlayTime())/200 * (globalPaddingTop - reachToRefresh)), 0, 0);
				}
				
			}
		});;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		//System.out.println("firstVisibleItem: " + firstVisibleItem);
		this.firstVisibleItem = firstVisibleItem;
		if (getLastVisiblePosition() == (totalItemCount - 1)) {
			isScrollToBottom = true;
		} else {
			isScrollToBottom = false;
		}
	}
	
}
