package com.qiubai.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.bt.qiubai.CharacterDetailActivity;
import com.bt.qiubai.R;
import com.qiubai.adapter.CharacterBaseAdapter;
import com.qiubai.entity.Character;
import com.qiubai.service.CharacterService;
import com.qiubai.view.CharacterListView;
import com.qiubai.view.CharacterListView.OnRefreshListener;
import com.qiubai.view.CharacterListView.onLoadListener;

public class CharacterFragment extends Fragment implements OnRefreshListener,onLoadListener{
	private static String TAG = "CharacterFragment";
/*
	private String[] fcd_support_text = new String[] { "12", "23", "34", "123" };
	private String[] fcd_tread_text = new String[] { "3219", "4329", "5439",
			"22" };
	private String[] fcd_follow_text = new String[] { "6", "7", "8", "9" };

	private String[] fcd_context = new String[] {
			"通过上面的例子可以看到Map中图片项的value是资源id，这是针对项目中已存在的图片文件，为什么要用资源id而不是其他（比如Bitmap类型）呢，这是因为adapter的bindView()方法是负责解析图片并将其显示到ImageView中，但它只针对资源id类型做了判断。然而有一种情况，比如你的图片是从网络读取的Bitmap类型，你就需要对代码进行改写了。分",
			"通过前面的例子可以看到，ListView的所有item使用的都是相同的布局，如果想使用不同的布局呢？",
			"后像自定义ListView的步骤一样使用就行了，只是把SimpleAdapter替换为CustomImageAdapter，Map中图片项的value变为Bitmap类型了",
			"3.9日凌晨发生在上海新锦江大酒店的事情过去两天了，我寝食难安。通过两天的闭门思过，认识到，该事件的是非曲直对我本人来说已经不重要了，错了，就要有代价。我牵挂的是你们！我深深地感到痛心的是，无冤无仇，从未某过面的司机师傅，因与我的争执而受轻伤躺在医院。我深深地感到追悔莫及的是，重情重义的三位好兄弟，因此而遭受牵连，失去自由。在此，我郑重的对受伤司机师傅以及另外两位司机师傅道歉，请原谅因我而起的非我主观意愿的这个结果。我郑重的对受到牵" };
*/
	private TextView share_text;
	private CharacterService characterService;
	private final static int GET_CHARACTER = 1;
	
	private int character_start = 0;
	private int character_count = CharacterListView.pageSize;
	//private ListView listCharacterView;
	//变化
	private CharacterListView listCharacterView;
//	private String characterURL = "http://192.168.1.69:8081/QiuBaiServer/rest/CharacterService/getCharacters";
	
	//private String characterURL = CommonUtil.getCharacterUrl();
	
	View head_view;
	
	CharacterBaseAdapter characterAdapter;
	List<Character> listChars =new ArrayList<Character>();
	
	List<Character> listResult = new ArrayList<Character>();
	
	Map<String, String> map;
	private Intent intent; 
	
	private String fcd_char_support;//点赞的人数
	private String fcd_char_oppose;//点吐槽的人数
	private String fcd_char_comment;//评论的人数
	private String fcd_context;//正文
	private String fcd_char_title;//标题
	private String fcd_user;//用户id
	private String fcd_char_time;//发布的时间
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			
			List<Character> result = (List<Character>) msg.obj;
			listCharacterView.setResultSize(result.size());
//			System.out.println("result:"+result.size());
			switch (msg.what) {
			case CharacterListView.REFRESH:
//				character_start = 0;
				listCharacterView.onRefreshComplete();
//				System.out.println("REFRESH：listChars长度前："+listResult.size());
				
				listResult.clear();
				listResult.addAll(0, result);
//				System.out.println("listChars长度后："+listResult.size());
//				System.out.println("result长度："+result.size());
				characterAdapter.changeValue(listResult);
				break;
			case CharacterListView.LOAD:
//				System.out.println("LOAD：listChars长度前："+listResult.size());

//				character_start = character_start+20;
				listCharacterView.onLoadComplete();
				listResult.addAll(result);
				characterAdapter.changeValue(listResult);
				break;
				
			/*case CharacterBaseAdapter.SUPPORT:
				int pos = (Integer) msg.obj;
				View view = characterAdapter.getView(pos, null, null);
				TextView textview = (TextView) view.findViewById(R.id.fragment_character_detail_support_text);
				String supportText = String.valueOf(Integer.parseInt(textview.getText().toString())+1);
				System.out.println("supportText:"+supportText);
				textview.setText("3");
				break;*/

			}
			
			characterAdapter.notifyDataSetChanged();
		};
	};
	


	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "==onCreate==");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		View characterLayout = inflater.inflate(
				R.layout.fragment_character_layout, container, false);
		// 取得ListView实例
		listCharacterView = (CharacterListView) characterLayout.findViewById(R.id.listView_fragment_character);
		
		characterAdapter = new CharacterBaseAdapter(getActivity(), listResult, listCharacterView);
		listCharacterView.setonRefreshListener(this);
		listCharacterView.setOnLoadListener(this);
		
		listCharacterView.setAdapter(characterAdapter);
		
		OnItemClickListener onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intent = new Intent(getActivity(), CharacterDetailActivity.class);
				fcd_char_title = listResult.get(position-1).getChar_title();
				fcd_context = listResult.get(position-1).getChar_context();
				fcd_char_support = listResult.get(position-1).getChar_support();
				fcd_char_oppose = listResult.get(position-1).getChar_oppose();
				fcd_char_comment = listResult.get(position-1).getChar_comment();
				fcd_char_time = listResult.get(position-1).getChar_time();
				fcd_user = listResult.get(position-1).getUserid()+"";
				intent.putExtra("fcd_char_title",fcd_char_title);
				intent.putExtra("fcd_context",fcd_context);
				intent.putExtra("fcd_char_support",fcd_char_support);
				intent.putExtra("fcd_char_oppose",fcd_char_oppose);
				intent.putExtra("fcd_char_comment",fcd_char_comment);
				intent.putExtra("fcd_char_time",fcd_char_time);
				intent.putExtra("fcd_user",fcd_user);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.stay_in_place);
//				Log.d(TAG, "fcd_char_title:"+fcd_char_title+"\n"+"fcd_context "+fcd_context);
				
			}
		};
		listCharacterView.setOnItemClickListener(onItemClickListener );
		loadData(CharacterListView.REFRESH);
		
		
		
		
		// 创建一个List集合，List集合的元素是Map
		//List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		
		

		/*
		 * for (int i = 0; i < fcd_support_text.length; i++) { Map<String,
		 * Object> listItem = new HashMap<String, Object>();
		 * listItem.put("context_text", fcd_context[i]);
		 * listItem.put("support_text", fcd_support_text[i]);
		 * listItem.put("tread_text", fcd_tread_text[i]);
		 * listItem.put("follow_text", fcd_follow_text[i]);
		 * listItems.add(listItem);
		 * 
		 * }
		 */
	
		
		return characterLayout;

	}
	
	private void loadData(final int what) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage();
				msg.what = what;
				characterService = new CharacterService();
				map = new HashMap<String, String>();
				if (what == CharacterListView.LOAD) {
					character_start = character_start + character_count;
					character_count = CharacterListView.pageSize;
				}
				if (what == CharacterListView.REFRESH) {
					character_count = character_count + character_start;
					character_start = 0;
				}
				map.put("offset", String.valueOf(character_start));
				map.put("rows", String.valueOf(character_count));
				String resultUrl = characterService.getCharacters(map);
				listChars = characterService.getCharacterByJson(resultUrl);
				msg.obj = listChars;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	
	@Override
	public void onLoad() {
		loadData(CharacterListView.LOAD);
	}

	@Override
	public void onRefresh() {
		loadData(CharacterListView.REFRESH);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "==onStart==");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "==onResume==");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "==onPause==");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "==onStop==");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "==onDestroyView==");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "==onDestroy==");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "==onDetach==");
	}

	



}
