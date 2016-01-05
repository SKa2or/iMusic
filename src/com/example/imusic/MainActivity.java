package com.example.imusic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements OnClickListener {
	//声明切换界面所需变量
	private ViewPager mViewPager;		// 用来放置界面切换
	private PagerAdapter mPagerAdapter;	// 初始化View适配器
	private List<View> mViews = new ArrayList<View>();// 用来存放Tab
	private View mView = null;			//用来获取mViews中的单个View
	View tab01 = null;
	View tab02 = null;
	View tab03 = null;

	/*底部界面元素*/
	private Button btn_allMusic;
	private Button btn_music;
	private Button btn_notice;

	/*顶部界面元素*/
	private Button btn_add;		//”评论“按钮

	/*公告栏界面元素*/
	//定义我们用到的webService网址
	private static final String url = "http://115.28.155.212/query";
	private static final int UPDATE_CONTENT = 0;
	//评论listView显示所需变量
	private ListView comments = null;
    private List<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter adapter = null;
    private Button btn_freshen = null;
    private TextView noticeTitle;

    /*在线音乐界面元素*/
    //音乐listView显示所需变量
  	private ListView musics = null;
    private List<Music> musicList = new ArrayList<Music>();
    private MusicAdapter musicAdapter = null;

    private TextView allMusicTitle;

    /*正在播放界面元素*/
    private TextView music_state = null;
    private TextView music_title = null;	//用来记录歌曲名
    private TextView music_singer = null;	//用来记录歌手名
    private ImageView imgBtn_play = null;
    private ImageView imgBtn_left = null;
    private ImageView imgBtn_right = null;
    private TextView music_rate = null;
    private SeekBar musicProgress = null;
    private Player player = null;
    
    boolean isPlaying = false;			//记录是否有正在播放的音乐
    
    private String music_id = null;		//用来记录歌曲id
    private String music_url = null;	//用来记录歌曲在线地址
    private int music_position = -1;	//用来记录正在播放歌曲在musicList中的位置

    private String userId = null;	//用来记录用户名
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//去掉标题栏
        setContentView(R.layout.activity_main);
        
        Log.d("debug","step1");
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
        	userId = bundle.getString("userId");
        	Log.d("debug","bundle: " + userId);
        }
        else
        	Log.d("debug","bundle is null!!!");
        
        Log.d("debug","step2");
        //切换界面
        initView();
		initViewPage();
		initEvent();

		Log.d("debug","step3");
        mView = mViews.get(0);										//获取子界面-推荐音乐界面
        allMusicTitle = (TextView)mView.findViewById(R.id.text_mainTitle);//这里必须通过子布局调用控件
        
        mView = mViews.get(1);										//获取子界面-正在播放界面
        music_rate = (TextView)mView.findViewById(R.id.music_rate);	//这里必须通过子布局调用控件
        music_state = (TextView)mView.findViewById(R.id.music_state);
        music_title = (TextView)mView.findViewById(R.id.music_title);
        music_singer = (TextView)mView.findViewById(R.id.music_singer);
        imgBtn_play = (ImageView)mView.findViewById(R.id.imgBtn_play);
        imgBtn_left = (ImageView)mView.findViewById(R.id.imgBtn_left);
        imgBtn_right = (ImageView)mView.findViewById(R.id.imgBtn_right);
        musicProgress = (SeekBar)mView.findViewById(R.id.seekBar_music);
        player = new Player(musicProgress,music_rate);
        imgBtn_play.setOnClickListener(new View.OnClickListener() {
        	@Override
    		public void onClick(View v) {
        		if(player != null){
        			if(isPlaying){	//若音乐正在播放，暂停音乐，图片变为播放
        				player.pause();
        				imgBtn_play.setImageResource(R.drawable.imgbtn_play);
        				isPlaying = false;
        				music_state.setText("已暂停");
        			}else{			//若音乐已暂停，播放音乐，图片变为暂停
        				player.play();
        				imgBtn_play.setImageResource(R.drawable.imgbtn_pause);
        				isPlaying = true;
        				music_state.setText("正在播放");
        			}
        		}
    		}
		});
        imgBtn_left.setOnClickListener(new View.OnClickListener() {
        	@Override
    		public void onClick(View v) {
        		if(music_position != -1){	//若有正在播放的音乐
        			if(music_position > 0){
            			playMusic(music_position-1);	//播放前一首歌
            		}else{
            			playMusic(musicList.size()-1);	//播放最后一首歌
            		}
        		}
    		}
		});
        imgBtn_right.setOnClickListener(new View.OnClickListener() {
        	@Override
    		public void onClick(View v) {
        		if(music_position != -1){	//若有正在播放的音乐
        			if(music_position < musicList.size()-1){
            			playMusic(music_position+1);	//播放后一首歌
            		}else{
            			playMusic(0);					//播放第一首歌
            		}
        		}
    		}
		});
        musicProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	int progress;

    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress,
    				boolean fromUser) {
    			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
    			this.progress = progress * player.mediaPlayer.getDuration()
    					/ seekBar.getMax();
    		}

    		@Override
    		public void onStartTrackingTouch(SeekBar seekBar) {

    		}

    		@Override
    		public void onStopTrackingTouch(SeekBar seekBar) {
    			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
    			player.mediaPlayer.seekTo(progress);
    		}
		});
        
        mView = mViews.get(2);										//获取子界面-评论界面
        noticeTitle = (TextView)mView.findViewById(R.id.text_noticeTitle);//这里必须通过子布局调用控件
        btn_freshen = (Button)mView.findViewById(R.id.btn_freshen);
        btn_freshen.getBackground().setAlpha(100);	//调整为半透明
        
        Log.d("debug","step4");
        sendRequestWithHttpUrlConnection();	//获取音乐列表
        Log.d("debug","step5");
        sendRequestWithHttpUrlConnection2();//获取评论
		
        //添加评论按钮监听事件
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(isPlaying == true){
					Intent i = new Intent(MainActivity.this,AddCommentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("uid", userId);
					bundle.putString("mid", music_id);
					Log.d("debug", "uid = " + userId);
					Log.d("debug", "mid = " + music_id);
					i.putExtras(bundle);
					startActivity(i);	//跳转至添加评论页面
				}
			}
		});
		
		btn_freshen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendRequestWithHttpUrlConnection2();//刷新评论
			}
		});
    }
	
	private void playMusic(int position){
		music_position = position;	//声明正在播放的歌曲位置已经改变
		
		music_id = musicList.get(position).getMid();
		music_state.setText("正在播放");
		music_title.setText("歌曲：" + musicList.get(position).getTitle());
		music_singer.setText("歌手：" + musicList.get(position).getSinger());
		music_url = musicList.get(position).getUrl();
		mViewPager.setCurrentItem(1);	//切换界面至正在播放界面
		if(music_url == null){
			music_state.setText("无法找到音乐地址");
		}else{
			final String path = "http://115.28.155.212/music?name=" + music_url;
			Log.d("debug", path);
			new Thread(new Runnable() {
				@Override
				public void run() {
					player.playUrl(path);
				}
			}).start();
		}
		isPlaying = true;				//声明音乐正在播放
		imgBtn_play.setImageResource(R.drawable.imgbtn_pause);
	}
	
	//handler用于子线程与UI线程的通讯，更新UI：
	//分析返回信息，获取音乐列表
    private Handler handler_music = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String isRight = obj.getString("success");
        				if(isRight.equals("false")){		//获取音乐列表出错时
        					allMusicTitle.setText("获取音乐列表出错！");
    					}else{
    						JSONArray ja = new JSONArray(obj.getString("result"));
    						int len = ja.length();
    						musicList.clear();		//插入前清空列表
    						for(int i = 0;i < len;i++){	//初始化commentList的内容（即预期ListView应有内容）
    							musicList.add(new Music(ja.getJSONObject(i).getString("mid"),
    														ja.getJSONObject(i).getString("title"),
    														ja.getJSONObject(i).getString("sname"),
    														ja.getJSONObject(i).getString("url")));
    						}
    						musicAdapter = new MusicAdapter(MainActivity.this, R.layout.musics_item, musicList);
    				        mView = mViews.get(0);										//获取子界面-全部音乐界面
    				        musics = (ListView)mView.findViewById(R.id.LV_musics);	//这里必须通过子布局调用控件
    				        musics.setAdapter(musicAdapter);
    				        musics.setOnItemClickListener(MusicCL);					//注册单项点击事件
    					}
        			} catch (JSONException e) {
        				e.printStackTrace();
        			}
                    break;
                default:break;
            }
        }
    };
    //音乐列表ListView的单项点击事件
    private AdapterView.OnItemClickListener MusicCL = new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			playMusic(position);	//播放musicList中位于position的歌曲
		}
    };
    
    //分析返回信息，获取评论
    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String isRight = obj.getString("success");
        				if(isRight.equals("false")){		//获取评论出错时
        					noticeTitle.setText("获取评论出错！");
    					}else{
    						JSONArray ja = new JSONArray(obj.getString("result"));
    						int len = ja.length();
    						commentList.clear();		//插入前清空列表
    						for(int i = 0;i < len;i++){	//初始化commentList的内容（即预期ListView应有内容）
    							commentList.add(new Comment(ja.getJSONObject(i).getString("alias"),
    														ja.getJSONObject(i).getString("title"),
    														ja.getJSONObject(i).getString("content")));
    						}
    				        adapter = new CommentAdapter(MainActivity.this, R.layout.comments_item, commentList);
    				        mView = mViews.get(2);										//获取子界面-评论界面
    				        comments = (ListView)mView.findViewById(R.id.LV_comments);	//这里必须通过子布局调用控件
    				        comments.setAdapter(adapter);
    				        comments.setOnItemClickListener(ItemCL);					//注册单项点击事件
    					}
        			} catch (JSONException e) {
        				e.printStackTrace();
        			}
                    break;
                default:break;
            }
        }
    };
    //评论ListView的单项点击事件
    private AdapterView.OnItemClickListener ItemCL = new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(MainActivity.this, ShowItemActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("uid", commentList.get(position).getAlias());
            bundle.putString("mid", commentList.get(position).getMusic());
            bundle.putString("content", commentList.get(position).getComment());
            intent.putExtras(bundle);
            startActivity(intent);
		}
    };

	/**
	 * 初始化设置
	 */
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpage);
		// 初始化化底部界面的按钮
		btn_allMusic = (Button) findViewById(R.id.tab_btn_allMusic);
		btn_allMusic.getBackground().setAlpha(100);	//调整为半透明
		btn_music = (Button) findViewById(R.id.tab_btn_music);
		btn_music.getBackground().setAlpha(100);	//调整为半透明
		btn_notice = (Button) findViewById(R.id.tab_btn_notice);
		btn_notice.getBackground().setAlpha(100);	//调整为半透明
		// 初始化顶部界面控件
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.getBackground().setAlpha(100);	//调整为半透明
		btn_add.setVisibility(View.GONE);	//隐藏“评论”按钮
	}

	/**
	 * 初始化ViewPage
	 */
	private void initViewPage() {
		// 初始化四个布局
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		if(tab01 == null){
			tab01 = mLayoutInflater.inflate(R.layout.tab_allmusic, null);
		}
		if(tab02 == null){
			tab02 = mLayoutInflater.inflate(R.layout.tab_music, null);
		}
		if(tab03 == null){
			tab03 = mLayoutInflater.inflate(R.layout.tab_notice, null);
		}
		if(mViews.isEmpty()){
			mViews.add(tab01);
			mViews.add(tab02);
			mViews.add(tab03);
		}
		
		// 适配器初始化并设置
		mPagerAdapter = new PagerAdapter() {

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(mViews.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = mViews.get(position);
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mViews.size();
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
	}

	/**
	 * 判断哪个要显示，及设置按钮图片
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.tab_btn_allMusic:
			mViewPager.setCurrentItem(0);
			btn_add.setVisibility(View.GONE);	//隐藏“评论”按钮
			break;
		case R.id.tab_btn_music:
			mViewPager.setCurrentItem(1);
			btn_add.setVisibility(View.VISIBLE);//显示“评论”按钮
			break;
		case R.id.tab_btn_notice:
			mViewPager.setCurrentItem(2);
			btn_add.setVisibility(View.GONE);	//隐藏“评论”按钮
			break;
		default:
			break;
		}
	}
	
	private void initEvent() {
		if(btn_allMusic == null){
			Log.d("debug","NULL");
		}else{
			Log.d("debug","Not NULL");
		}
		btn_allMusic.setOnClickListener(this);
		btn_music.setOnClickListener(this);
		btn_notice.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            /**
            *ViewPage左右滑动时
            */
			@Override
			public void onPageSelected(int arg0) {
				int currentItem = mViewPager.getCurrentItem();
				switch (currentItem) {
				case 0:
					btn_add.setVisibility(View.GONE);	//隐藏“评论”按钮
					break;
				case 1:
					btn_add.setVisibility(View.VISIBLE);//显示“评论”按钮
					break;
				case 2:
					btn_add.setVisibility(View.GONE);	//隐藏“评论”按钮
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	//发送获取音乐列表的请求函数，获得音乐列表
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web服务需要新建一个线程执行
            @Override
            public void run() {//子线程执行请求
            	MySQLService query = null;			//建立Mysql服务的http连接
                try{
                	query = new MySQLService(url);		//新建Mysql服务的http连接
                	Message message = new Message();	//用来存储返回信息
                	String sql = "SELECT m.mid, title, sname, url from (music m LEFT OUTER JOIN singer s on m.singer_id = s.sid) LEFT OUTER JOIN music_url u on m.mid = u.mid ORDER BY degree DESC";
					if(query.sendQuery(sql)){			//若发送查询信息成功
						message  = query.getMessage();	//记录查询结果
						handler_music.sendMessage(message);	//传递给handler
                	}else{
                		Log.d("debug","发送查询音乐信息失败");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//断开连接
                	query.onDestroy();
                }
            }
        }).start();
    }
	
	//发送获取评论的请求函数，获得评论信息
    private void sendRequestWithHttpUrlConnection2(){
        new Thread(new Runnable() {//web服务需要新建一个线程执行
            @Override
            public void run() {//子线程执行请求
            	MySQLService query = null;			//建立Mysql服务的http连接
                try{
                	query = new MySQLService(url);		//新建Mysql服务的http连接
                	Message message = new Message();	//用来存储返回信息
                	String sqlSentence = "SELECT m.title, u.alias, c.content from comment c, music m, user u where u.uid = c.uid and m.mid = c.mid";
					if(query.sendQuery(sqlSentence)){	//若发送查询信息成功
						message  = query.getMessage();	//记录查询结果
                		handler.sendMessage(message);	//传递给handler
                	}else{
                		Log.d("debug","发送查询信息失败");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//断开连接
                	query.onDestroy();
                }
            }
        }).start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
