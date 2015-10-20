package com.bt.qiubai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiubai.entity.CommentWithUser;
import com.qiubai.service.CommentService;
import com.qiubai.service.UserService;
import com.qiubai.util.BitmapUtil;
import com.qiubai.util.NetworkUtil;
import com.qiubai.util.PropertiesUtil;
import com.qiubai.util.SharedPreferencesUtil;
import com.qiubai.view.CommonRefreshListView;
import com.qiubai.view.CommonRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity implements OnClickListener, OnTouchListener, OnRefreshListener {
	
	private RelativeLayout comment_title_back, comment_rel_listview, comment_rel_no_comment;
	private EditText comment_edittext_comment;
	private TextView comment_send;
	private CommonRefreshListView commentListview;
	private RelativeLayout crl_header_hidden;
	private ImageView common_progress_dialog_iv_rotate, common_publish_progress_dialog_iv_rotate;
	private TextView comment_tv_no_comment;
	
	private int newsid;
	private String belong;
	private String commentContentFlag = "nocontent";
	private Animation anim_rotate, anim_publish_rotate;
	private CommentBaseAdapter commentBaseAdapter;
	private GestureDetector gestureDetector;
	private Dialog progressDialog;
	private Dialog publishProgressDialog;
	private Bitmap bitmap_header_icon_default;
	private Map<String, Bitmap> map = new HashMap<String, Bitmap>();
	
	private List<CommentWithUser> comments = new ArrayList<CommentWithUser>();
	private SharedPreferencesUtil spUtil = new SharedPreferencesUtil(CommentActivity.this);
	private CommentService commentService;
	private UserService userService;
	private PropertiesUtil propUtil;
	
	private final static int COMMENT_SUCCESS = 1;
	private final static int COMMENT_FAIL = 2;
	private final static int COMMENT_ERROR = 3;
	private final static int COMMENT_LISTVIEW_REFRESH_SUCCESS = 4;
	private final static int COMMENT_LISTVIEW_REFRESH_NOCONTENT = 5;
	private final static int COMMENT_LISTVIEW_REFRESH_ERROR = 6;
	private final static int COMMENT_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS = 7;
	private final static int COMMENT_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT = 8;
	private final static int COMMENT_LISTVIEW_REFRESH_LOADING_MORE_ERROR = 9;
	private final static int COMMENT_LISTVIEW_FIRST_LOADING_SUCCESS = 10;
	private final static int COMMENT_LISTVIEW_FIRST_LOADING_ERROR = 11;
	private final static int COMMENT_LISTVIEW_FIRST_LOADING_NOCONTENT = 12;
	private final static String COMMENT_LISTVIEW_SIZE = "10";
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.comment_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.comment_title);
		
		commentService = new CommentService(CommentActivity.this);
		userService = new UserService(CommentActivity.this);
		propUtil = new PropertiesUtil(CommentActivity.this);
		
		bitmap_header_icon_default = BitmapUtil.circleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.main_drawer_right_person_avatar));
		
		if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
		}
		
		Intent intent = getIntent();
		newsid = intent.getIntExtra("newsid", 0);
		belong = intent.getStringExtra("belong");
		
		crl_header_hidden = (RelativeLayout) findViewById(R.id.crl_header_hidden);
		comment_rel_listview = (RelativeLayout) findViewById(R.id.comment_rel_listview);
		comment_rel_no_comment = (RelativeLayout) findViewById(R.id.comment_rel_no_comment);
		comment_tv_no_comment = (TextView) findViewById(R.id.comment_tv_no_comment);
		comment_title_back = (RelativeLayout) findViewById(R.id.comment_title_back);
		comment_title_back.setOnClickListener(this);
		
		publishProgressDialog = new Dialog(CommentActivity.this, R.style.CommonProgressDialog);
		publishProgressDialog.setContentView(R.layout.common_progress_dialog);
		publishProgressDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams publishProgressDialog_lp = publishProgressDialog.getWindow().getAttributes();
		publishProgressDialog_lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		publishProgressDialog_lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		publishProgressDialog.getWindow().setAttributes(publishProgressDialog_lp);
		publishProgressDialog.setCancelable(false);
		publishProgressDialog.setCanceledOnTouchOutside(false);
		common_publish_progress_dialog_iv_rotate = (ImageView) publishProgressDialog.findViewById(R.id.common_progress_dialog_iv_rotate);
		anim_publish_rotate = AnimationUtils.loadAnimation(this, R.anim.common_rotate);
		
		progressDialog = new Dialog(CommentActivity.this, R.style.CommonProgressDialog);
		progressDialog.setContentView(R.layout.common_progress_dialog);
		progressDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams progressDialog_lp = progressDialog.getWindow().getAttributes();
		progressDialog_lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		progressDialog_lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        progressDialog.getWindow().setAttributes(progressDialog_lp);
        progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		common_progress_dialog_iv_rotate = (ImageView) progressDialog.findViewById(R.id.common_progress_dialog_iv_rotate);
		anim_rotate = AnimationUtils.loadAnimation(this, R.anim.common_rotate);
		progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					progressDialog.dismiss();
					CommentActivity.this.finish();
					overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
					return true;
				}
				return false;
			}
		});
		
		commentListview = (CommonRefreshListView) findViewById(R.id.comment_listview);
		commentBaseAdapter = new CommentBaseAdapter(this);
		commentListview.setAdapter(commentBaseAdapter);
		commentListview.setHiddenView(crl_header_hidden);
		commentListview.setOnRefreshListener(this);
		commentListview.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		gestureDetector = new GestureDetector(CommentActivity.this,onGestureListener );
		commentListview.setOnTouchListener(this);
		
		comment_edittext_comment = (EditText) findViewById(R.id.comment_edittext_comment);
		comment_edittext_comment.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!"".equals(s.toString().trim())){
					comment_send.setTextColor(getResources().getColor(R.color.comment_send_enable_text_color));
				} else {
					comment_send.setTextColor(getResources().getColor(R.color.comment_send_disable_text_color));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		comment_send = (TextView) findViewById(R.id.comment_send);
		comment_send.setOnClickListener(this);
		onFirstLoadingComment();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_title_back:
			CommentActivity.this.finish();
			overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
			break;
		case R.id.comment_send:
			if(verifyInformation()){
				if (userService.checkUserLogin(CommentActivity.this)) {
					sendComment();
				} else {
					Toast.makeText(CommentActivity.this, "您还没有登录，请登录", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
				}
			}
			break;
		}
	}
	
	/**
	 * verify information and network
	 * @return true: success; false: fail
	 */
	public boolean verifyInformation(){
		if("".equals(comment_edittext_comment.getText().toString().trim())){
			return false;
		} else if (comment_edittext_comment.getText().toString().trim().length() > 500) {
			Toast.makeText(this, "最多只能输入500个字符", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!NetworkUtil.isConnectInternet(this)){
			Toast.makeText(this, "您没有连接网络，请连接网络", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * publish comment
	 */
	public void sendComment(){
		common_publish_progress_dialog_iv_rotate.startAnimation(anim_publish_rotate);
		publishProgressDialog.show();
		new Thread(){
			public void run() {
				String userid = spUtil.getUserid();
				String token = spUtil.getToken();
				String result = commentService.addComment(belong, String.valueOf(newsid), userid, token, comment_edittext_comment.getText().toString().trim());
				if("fail".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_FAIL);
					commentHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_ERROR);
					commentHandler.sendMessage(msg);
				} else {
					Message msg = commentHandler.obtainMessage(COMMENT_SUCCESS);
					msg.obj = result;
					commentHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	/**
	 * open comment activity to load comment
	 */
	public void onFirstLoadingComment(){
		comment_rel_listview.setVisibility(View.INVISIBLE);
		comment_rel_no_comment.setVisibility(View.INVISIBLE);
		progressDialog.show();
		common_progress_dialog_iv_rotate.startAnimation(anim_rotate);
		new Thread(){
			public void run() {
				String result = commentService.getComments(belong, String.valueOf(newsid), "0", COMMENT_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_FIRST_LOADING_NOCONTENT);
					commentHandler.sendMessage(msg);
				} else if ("error".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_FIRST_LOADING_ERROR);
					commentHandler.sendMessage(msg);
				} else {
					List<CommentWithUser> list = commentService.parseCommentsJson(result);
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_FIRST_LOADING_SUCCESS);
					msg.obj = list;
					commentHandler.sendMessage(msg);
				}
				
			};
		}.start();
	}
	
	/**
	 * pull down to refresh
	 */
	@Override
	public void onDownPullRefresh() {
		new Thread(){
			public void run() {
				String result = commentService.getComments(belong, String.valueOf(newsid), "0", COMMENT_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_REFRESH_NOCONTENT);
					commentHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_REFRESH_ERROR);
					commentHandler.sendMessage(msg);
				} else {
					List<CommentWithUser> list = commentService.parseCommentsJson(result);
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_REFRESH_SUCCESS);
					msg.obj = list;
					commentHandler.sendMessage(msg);
				}
				
			};
		}.start();
	}

	/**
	 * loading more comments
	 */
	@Override
	public void onLoadingMore() {
		new Thread(){
			public void run() {
				String offset = String.valueOf(comments.size());
				String result = commentService.getComments(belong, String.valueOf(newsid), offset, COMMENT_LISTVIEW_SIZE);
				if("nocontent".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT);
					commentHandler.sendMessage(msg);
				} else if("error".equals(result)){
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_REFRESH_LOADING_MORE_ERROR);
					commentHandler.sendMessage(msg);
				} else {
					Message msg = commentHandler.obtainMessage(COMMENT_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS);
					List<CommentWithUser> list = commentService.parseCommentsJson(result);
					msg.obj = list;
					commentHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	@SuppressLint("ViewHolder")
	private class CommentBaseAdapter extends BaseAdapter {
		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		
		public CommentBaseAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return comments.size();
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
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.comment_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.comment_listview_item_iv_icon = (ImageView) convertView.findViewById(R.id.comment_listview_item_iv_icon);
				viewHolder.comment_listview_item_tv_content = (TextView) convertView.findViewById(R.id.comment_listview_item_tv_content);
				viewHolder.comment_listview_item_tv_username = (TextView) convertView.findViewById(R.id.comment_listview_item_tv_username);
				viewHolder.comment_listview_item_tv_time = (TextView) convertView.findViewById(R.id.comment_listview_item_tv_time);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			CommentWithUser cwu = comments.get(position);
			viewHolder.comment_listview_item_tv_content.setText(cwu.getComment().getContent());
			viewHolder.comment_listview_item_tv_username.setText(cwu.getUser().getNickname());
			viewHolder.comment_listview_item_tv_time.setText(dealTime(cwu.getComment().getTime()));
			viewHolder.comment_listview_item_iv_icon.setImageBitmap(bitmap_header_icon_default);
			if( !"default".equals(cwu.getUser().getIcon()) && !"".equals(cwu.getUser().getIcon()) ){
				loadUserIcon(cwu.getUser().getUserid(), cwu.getUser().getIcon(), viewHolder.comment_listview_item_iv_icon);
			}
			
			return convertView;
		}
		
		private class ViewHolder{
			ImageView comment_listview_item_iv_icon;
			TextView comment_listview_item_tv_content;
			TextView comment_listview_item_tv_username;
			TextView comment_listview_item_tv_time;
		}
		
	}
	
	public void loadUserIcon(final String userid, final String url, final ImageView iv){
		Bitmap bitmap_map = map.get(userid);
		if(bitmap_map != null){
			iv.setImageBitmap(bitmap_map);
		} else {
			new Thread(){
				public void run() {
					Bitmap bitmap_file = BitmapFactory.decodeFile(propUtil.readProperties("config.properties", "userinfo_path") + userid + ".png");
					if(bitmap_file != null){
						final Bitmap bitmap_file_changed = BitmapUtil.circleBitmap(bitmap_file);
						map.put(userid, bitmap_file_changed);
						commentHandler.post(new Runnable() {
							@Override
							public void run() {
								iv.setImageBitmap(bitmap_file_changed);
							}
						});
					} else {
						new Thread(){
							public void run() {
								Bitmap bitmap_remote = userService.getHeaderIcon(url);
								if(bitmap_remote != null){
									
									final Bitmap bitmap_remote_changed = BitmapUtil.circleBitmap(bitmap_remote);
									commentService.storeImage(bitmap_remote, userid + ".png");
									map.put(userid, bitmap_remote_changed);
									commentHandler.post(new Runnable() {
										@Override
										public void run() {
											iv.setImageBitmap(bitmap_remote_changed);
										}
									});
								}
								
							};
						}.start();
					}
				};
			}.start();
		}
	}

	public void storeBitmapToMap(List<CommentWithUser> list){
		for(final CommentWithUser cwu : list){
			if(!"default".equals(cwu.getUser().getIcon()) && !"".equals(cwu.getUser().getIcon()) ){
				new Thread(){
					public void run() {
						Bitmap bitmap = userService.getHeaderIcon(cwu.getUser().getIcon());
						if(bitmap != null){
							map.put(cwu.getUser().getUserid(), BitmapUtil.circleBitmap(bitmap));
							commentService.storeImage(bitmap, cwu.getUser().getUserid() + ".png");
						}
					};
				}.start();
			}
		}
	}
	
	/**
	 * get comment by id
	 * @param id
	 */
	public void getCommentById(final String id){
		new Thread(){
			public void run() {
				String userid = spUtil.getUserid();
				String token = spUtil.getToken();
				final String result = commentService.getCommentById(token, id, userid);
				if(!"error".equals(result) && !"nocontent".equals(result)){
					commentHandler.post(new Runnable() {
						@Override
						public void run() {
							CommentWithUser cwu = commentService.parseCommentJson(result);
							comments.add(0, cwu);
							commentListview.setTimeTextViewTag();
							commentBaseAdapter.notifyDataSetChanged();
							if("nocontent".equals(commentContentFlag)){
								commentContentFlag = "existcontent";
								comment_rel_listview.setVisibility(View.VISIBLE);
								comment_rel_no_comment.setVisibility(View.INVISIBLE);
							}
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * deal time string to MM/DD hh:mm
	 * @param str
	 * @return
	 */
	public String dealTime(String str){
		String month = (str.split(" ")[0]).split("-")[1];
		String day = (str.split(" ")[0]).split("-")[2];
		String hour = (str.split(" ")[1]).split(":")[0];
		String minute = (str.split(" ")[1]).split(":")[1];
		return month + "/" + day + " " + hour + ":" + minute; 
	}
	
	@SuppressLint("HandlerLeak")
	private Handler commentHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case COMMENT_SUCCESS:
				common_publish_progress_dialog_iv_rotate.clearAnimation();
				publishProgressDialog.dismiss();
				comment_edittext_comment.setText("");
				String comment_id = (String) msg.obj;
				getCommentById(comment_id);
				Toast.makeText(CommentActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
				break;
			case COMMENT_FAIL:
				common_publish_progress_dialog_iv_rotate.clearAnimation();
				publishProgressDialog.dismiss();
				Toast.makeText(CommentActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
				break;
			case COMMENT_ERROR:
				common_publish_progress_dialog_iv_rotate.clearAnimation();
				publishProgressDialog.dismiss();
				Toast.makeText(CommentActivity.this, "发布异常", Toast.LENGTH_SHORT).show();
				break;
			case COMMENT_LISTVIEW_REFRESH_SUCCESS:
				commentListview.hiddenHeaderView();
				commentListview.setTimeTextViewTag();
				commentListview.hiddenFooterView(true);
				comments.clear();
				comments = (List<CommentWithUser>) msg.obj;
				commentBaseAdapter.notifyDataSetChanged();
				//storeBitmapToMap(comments);
				break;
			case COMMENT_LISTVIEW_REFRESH_NOCONTENT:
				comment_tv_no_comment.setText("暂时没有评论，请写评论");
				commentListview.hiddenHeaderView();
				commentListview.setTimeTextViewTag();
				commentListview.hiddenFooterView(true);
				comment_rel_listview.setVisibility(View.INVISIBLE);
				comment_rel_no_comment.setVisibility(View.VISIBLE);
				break;
			case COMMENT_LISTVIEW_REFRESH_ERROR:
				Toast.makeText(CommentActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
				commentListview.hiddenHeaderView();
				break;
			case COMMENT_LISTVIEW_REFRESH_LOADING_MORE_SUCCESS:
				commentListview.hiddenFooterView(true);
				addToListComments((List<CommentWithUser>) msg.obj);
				commentBaseAdapter.notifyDataSetChanged();
				//storeBitmapToMap((List<CommentWithUser>) msg.obj);
				break;
			case COMMENT_LISTVIEW_REFRESH_LOADING_MORE_NOCONTENT:
				commentListview.hiddenFooterView(false);
				break;
			case COMMENT_LISTVIEW_REFRESH_LOADING_MORE_ERROR:
				Toast.makeText(CommentActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
				commentListview.hiddenFooterView(true);
				break;
			case COMMENT_LISTVIEW_FIRST_LOADING_SUCCESS:
				common_progress_dialog_iv_rotate.clearAnimation();
				progressDialog.dismiss();
				//List<CommentWithUser> list2 = (List<CommentWithUser>) msg.obj;
				comments.clear();
				addToListComments((List<CommentWithUser>) msg.obj);
				commentBaseAdapter.notifyDataSetChanged();
				commentListview.setTimeTextViewTag();
				comment_rel_listview.setVisibility(View.VISIBLE);
				commentContentFlag = "existcontent";
				//storeBitmapToMap((List<CommentWithUser>) msg.obj);
				break;
			case COMMENT_LISTVIEW_FIRST_LOADING_ERROR:
				common_progress_dialog_iv_rotate.clearAnimation();
				progressDialog.dismiss();
				comment_tv_no_comment.setText("网络连接错误");
				comment_rel_no_comment.setVisibility(View.VISIBLE);
				commentContentFlag = "nocontent";
				break;
			case COMMENT_LISTVIEW_FIRST_LOADING_NOCONTENT:
				common_progress_dialog_iv_rotate.clearAnimation();
				progressDialog.dismiss();
				comment_tv_no_comment.setText("暂时没有评论，请写评论");
				comment_rel_no_comment.setVisibility(View.VISIBLE);
				commentContentFlag = "nocontent";
				break;
				
			}
		};
	};
	
	public void addToListComments(List<CommentWithUser> list){
		for(CommentWithUser cwu : list){
			comments.add(cwu);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			CommentActivity.this.finish();
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
					CommentActivity.this.finish();
					overridePendingTransition(R.anim.stay_in_place, R.anim.out_to_right);
					return true;
				}else if(e2.getX() - e1.getX() < -200){
					return false;
				}
			}else {
				return false;
			}
			
			return false;
		}
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
