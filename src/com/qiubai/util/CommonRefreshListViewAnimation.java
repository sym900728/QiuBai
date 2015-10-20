package com.qiubai.util;

import com.qiubai.view.CommonRefreshListView;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.animation.LinearInterpolator;

public class CommonRefreshListViewAnimation {

	public static void moveListView(final CommonRefreshListView listview, String key) {
		
		// 判断 listview 是不是已经正在刷新
		if (listview.getCurrentState() != CommonRefreshListView.REFRESH_ING) {
			listview.setSelection(0);
			listview.setLastUpdateTime(key);
			listview.clockFinishState();
			ObjectAnimator listViewAnimator = ObjectAnimator.ofFloat(listview, "translationY", 0.0f, 0.0f);
			listViewAnimator.setDuration(400);
			listViewAnimator.setInterpolator(new LinearInterpolator());
			listViewAnimator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					listview.autoRefresh();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			listViewAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					int time = (int) animation.getCurrentPlayTime();
					if (time >= 400) {
						listview.setHeaderViewPadding(160);
					} else {
						listview.setHeaderViewPadding((160 * time) / 400);
					}
				}
			});
			listViewAnimator.start();
		}

	}
}
