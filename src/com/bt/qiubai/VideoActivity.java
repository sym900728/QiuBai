package com.bt.qiubai;

import java.io.IOException;

import com.qiubai.entity.Video;
import com.qiubai.service.VideoService;
import com.qiubai.util.DensityUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class VideoActivity extends Activity implements OnClickListener, OnTouchListener{

	private ScrollView video_scroll;
	private RelativeLayout video_title_rel_back, video_title_rel_right, video_title_rel_comment;
	private LinearLayout video_action_share, video_action_collect, video_action_comment; 
	private TextView video_tv_comment, video_tv_title, video_tv_from, video_tv_time;
	private SurfaceView video_surface_view;
	private MediaPlayer mediaPlayer;
	
	private int video_id, screenWidth, screenHeight, currentPosition;
	private GestureDetector gestureDetector;
	private Dialog videoActionDialog;
	private VideoService videoService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.video_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.video_title);

		videoService = new VideoService(VideoActivity.this);
		screenWidth = VideoActivity.this.getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = VideoActivity.this.getWindowManager().getDefaultDisplay().getHeight();
		
		Intent intent = getIntent();
		final Video video = (Video) intent.getSerializableExtra("video");
		video_id = video.getId();
		
		video_scroll = (ScrollView) findViewById(R.id.video_scroll);
		video_scroll.setOnTouchListener(this);
		video_title_rel_back = (RelativeLayout) findViewById(R.id.video_title_rel_back);
		video_title_rel_back.setOnClickListener(this);
		video_title_rel_right = (RelativeLayout) findViewById(R.id.video_title_rel_right);
		video_title_rel_right.setOnClickListener(this);
		video_title_rel_comment = (RelativeLayout) findViewById(R.id.video_title_rel_comment);
		video_title_rel_comment.setOnClickListener(this);
		
		video_tv_comment = (TextView) findViewById(R.id.video_tv_comment);
		video_tv_comment.setText(video.getComments() + " 评论");
		video_tv_title = (TextView) findViewById(R.id.video_tv_title);
		video_tv_title.setText(video.getTitle());
		video_tv_from = (TextView) findViewById(R.id.video_tv_from);
		video_tv_from.setText("来自：" + video.getBelong());
		video_tv_time = (TextView) findViewById(R.id.video_tv_time);
		video_tv_time.setText(dealTime(video.getTime()));
		
		gestureDetector = new GestureDetector(VideoActivity.this,onGestureListener);
		
		videoActionDialog = new Dialog(VideoActivity.this, R.style.CommonActionDialog);
		videoActionDialog.setContentView(R.layout.video_action_bar);
		videoActionDialog.getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		
		video_action_share = (LinearLayout) videoActionDialog.findViewById(R.id.video_action_share);
		video_action_collect = (LinearLayout) videoActionDialog.findViewById(R.id.video_action_collect);
		video_action_comment = (LinearLayout) videoActionDialog.findViewById(R.id.video_action_comment);
		video_action_share.setOnClickListener(this);
		video_action_collect.setOnClickListener(this);
		video_action_comment.setOnClickListener(this);
		
		mediaPlayer = new MediaPlayer();
		
		video_surface_view = (SurfaceView) findViewById(R.id.video_surface_view);
		video_surface_view.getHolder().setFixedSize(screenWidth - DensityUtil.dip2px(VideoActivity.this, 20), 
				(screenWidth - DensityUtil.dip2px(VideoActivity.this, 20)) * 2 / 3);
		video_surface_view.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		video_surface_view.getHolder().addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				System.out.println("holder 被销毁");
				//if(mediaPlayer != null && mediaPlayer.isPlaying()){
				//	currentPosition = mediaPlayer.getCurrentPosition();
				//	mediaPlayer.stop();
				//}
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				//System.out.println("holder 被创建");
				//if(currentPosition > 0){
				//	play(videoService.getVideo(video.getVideo()));
				//}
				mediaPlayer.setDisplay(video_surface_view.getHolder());
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				System.out.println("holder 变化了");
				
			}
		});
		play(videoService.getVideo(video.getVideo()));
		
	}

	public void play(String uri){
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(uri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.prepareAsync();
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				mediaPlayer.start();
			//	mediaPlayer.seekTo(currentPosition);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getVideoComments();
	}
	
	public void getVideoComments(){
		new Thread(){
			public void run() {
				videoHandler.post(new Runnable() {
					String result = videoService.getVideoComments(String.valueOf(video_id));
					@Override
					public void run() {
						if(!"error".equals(result) && !"nocontent".equals(result)){
							video_tv_comment.setText(result + " 评论");
						}
					}
				});
			};
		}.start();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_title_rel_back:
			VideoActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
			
		case R.id.video_title_rel_comment:
			Intent intent_to_comment = new Intent(VideoActivity.this, CommentActivity.class);
			intent_to_comment.putExtra("belong", "video");
			intent_to_comment.putExtra("newsid", video_id);
			startActivity(intent_to_comment);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
			
		case R.id.video_title_rel_right:
			videoActionDialog.show();
			break;
			
		case R.id.video_action_share:
			videoActionDialog.dismiss();
			break;
			
		case R.id.video_action_collect:
			videoActionDialog.dismiss();
			break;
			
		case R.id.video_action_comment:
			videoActionDialog.dismiss();
			Intent intent_to_comment2 = new Intent(VideoActivity.this, CommentActivity.class);
			intent_to_comment2.putExtra("belong", "video");
			intent_to_comment2.putExtra("newsid", video_id);
			startActivity(intent_to_comment2);
			overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
			break;
		}
	}
	
	private Handler videoHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			VideoActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float x = Math.abs(e2.getX() - e1.getX());
			float y = Math.abs(e2.getY() - e1.getY());
			if(y < x){
				if(e2.getX() - e1.getX() > 200){
					VideoActivity.this.finish();
					overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
					return true;
				}else if(e2.getX() - e1.getX() < -200){
					Intent intent = new Intent(VideoActivity.this, CommentActivity.class);
					intent.putExtra("belong", "video");
					intent.putExtra("newsid", video_id);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
					return true;
				}
			}else {
				return false;
			}
			return false;
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	/**
	 * deal time string to YYYY/MM/DD
	 * @param str
	 * @return
	 */
	public String dealTime(String str){
		String year = (str.split(" ")[0]).split("-")[0];
		String month = (str.split(" ")[0]).split("-")[1];
		String day = (str.split(" ")[0]).split("-")[2];
		return year + "/" + month + "/" + day; 
	}
}
