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
	//�����л������������
	private ViewPager mViewPager;		// �������ý����л�
	private PagerAdapter mPagerAdapter;	// ��ʼ��View������
	private List<View> mViews = new ArrayList<View>();// �������Tab
	private View mView = null;			//������ȡmViews�еĵ���View
	View tab01 = null;
	View tab02 = null;
	View tab03 = null;

	/*�ײ�����Ԫ��*/
	private Button btn_allMusic;
	private Button btn_music;
	private Button btn_notice;

	/*��������Ԫ��*/
	private Button btn_add;		//�����ۡ���ť

	/*����������Ԫ��*/
	//���������õ���webService��ַ
	private static final String url = "http://115.28.155.212/query";
	private static final int UPDATE_CONTENT = 0;
	//����listView��ʾ�������
	private ListView comments = null;
    private List<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter adapter = null;
    private Button btn_freshen = null;
    private TextView noticeTitle;

    /*�������ֽ���Ԫ��*/
    //����listView��ʾ�������
  	private ListView musics = null;
    private List<Music> musicList = new ArrayList<Music>();
    private MusicAdapter musicAdapter = null;

    private TextView allMusicTitle;

    /*���ڲ��Ž���Ԫ��*/
    private TextView music_state = null;
    private TextView music_title = null;	//������¼������
    private TextView music_singer = null;	//������¼������
    private ImageView imgBtn_play = null;
    private ImageView imgBtn_left = null;
    private ImageView imgBtn_right = null;
    private TextView music_rate = null;
    private SeekBar musicProgress = null;
    private Player player = null;
    
    boolean isPlaying = false;			//��¼�Ƿ������ڲ��ŵ�����
    
    private String music_id = null;		//������¼����id
    private String music_url = null;	//������¼�������ߵ�ַ
    private int music_position = -1;	//������¼���ڲ��Ÿ�����musicList�е�λ��

    private String userId = null;	//������¼�û���
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//ȥ��������
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
        //�л�����
        initView();
		initViewPage();
		initEvent();

		Log.d("debug","step3");
        mView = mViews.get(0);										//��ȡ�ӽ���-�Ƽ����ֽ���
        allMusicTitle = (TextView)mView.findViewById(R.id.text_mainTitle);//�������ͨ���Ӳ��ֵ��ÿؼ�
        
        mView = mViews.get(1);										//��ȡ�ӽ���-���ڲ��Ž���
        music_rate = (TextView)mView.findViewById(R.id.music_rate);	//�������ͨ���Ӳ��ֵ��ÿؼ�
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
        			if(isPlaying){	//���������ڲ��ţ���ͣ���֣�ͼƬ��Ϊ����
        				player.pause();
        				imgBtn_play.setImageResource(R.drawable.imgbtn_play);
        				isPlaying = false;
        				music_state.setText("����ͣ");
        			}else{			//����������ͣ���������֣�ͼƬ��Ϊ��ͣ
        				player.play();
        				imgBtn_play.setImageResource(R.drawable.imgbtn_pause);
        				isPlaying = true;
        				music_state.setText("���ڲ���");
        			}
        		}
    		}
		});
        imgBtn_left.setOnClickListener(new View.OnClickListener() {
        	@Override
    		public void onClick(View v) {
        		if(music_position != -1){	//�������ڲ��ŵ�����
        			if(music_position > 0){
            			playMusic(music_position-1);	//����ǰһ�׸�
            		}else{
            			playMusic(musicList.size()-1);	//�������һ�׸�
            		}
        		}
    		}
		});
        imgBtn_right.setOnClickListener(new View.OnClickListener() {
        	@Override
    		public void onClick(View v) {
        		if(music_position != -1){	//�������ڲ��ŵ�����
        			if(music_position < musicList.size()-1){
            			playMusic(music_position+1);	//���ź�һ�׸�
            		}else{
            			playMusic(0);					//���ŵ�һ�׸�
            		}
        		}
    		}
		});
        musicProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	int progress;

    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress,
    				boolean fromUser) {
    			// ԭ����(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
    			this.progress = progress * player.mediaPlayer.getDuration()
    					/ seekBar.getMax();
    		}

    		@Override
    		public void onStartTrackingTouch(SeekBar seekBar) {

    		}

    		@Override
    		public void onStopTrackingTouch(SeekBar seekBar) {
    			// seekTo()�Ĳ����������ӰƬʱ������֣���������seekBar.getMax()��Ե�����
    			player.mediaPlayer.seekTo(progress);
    		}
		});
        
        mView = mViews.get(2);										//��ȡ�ӽ���-���۽���
        noticeTitle = (TextView)mView.findViewById(R.id.text_noticeTitle);//�������ͨ���Ӳ��ֵ��ÿؼ�
        btn_freshen = (Button)mView.findViewById(R.id.btn_freshen);
        btn_freshen.getBackground().setAlpha(100);	//����Ϊ��͸��
        
        Log.d("debug","step4");
        sendRequestWithHttpUrlConnection();	//��ȡ�����б�
        Log.d("debug","step5");
        sendRequestWithHttpUrlConnection2();//��ȡ����
		
        //������۰�ť�����¼�
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
					startActivity(i);	//��ת���������ҳ��
				}
			}
		});
		
		btn_freshen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendRequestWithHttpUrlConnection2();//ˢ������
			}
		});
    }
	
	private void playMusic(int position){
		music_position = position;	//�������ڲ��ŵĸ���λ���Ѿ��ı�
		
		music_id = musicList.get(position).getMid();
		music_state.setText("���ڲ���");
		music_title.setText("������" + musicList.get(position).getTitle());
		music_singer.setText("���֣�" + musicList.get(position).getSinger());
		music_url = musicList.get(position).getUrl();
		mViewPager.setCurrentItem(1);	//�л����������ڲ��Ž���
		if(music_url == null){
			music_state.setText("�޷��ҵ����ֵ�ַ");
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
		isPlaying = true;				//�����������ڲ���
		imgBtn_play.setImageResource(R.drawable.imgbtn_pause);
	}
	
	//handler�������߳���UI�̵߳�ͨѶ������UI��
	//����������Ϣ����ȡ�����б�
    private Handler handler_music = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String isRight = obj.getString("success");
        				if(isRight.equals("false")){		//��ȡ�����б����ʱ
        					allMusicTitle.setText("��ȡ�����б����");
    					}else{
    						JSONArray ja = new JSONArray(obj.getString("result"));
    						int len = ja.length();
    						musicList.clear();		//����ǰ����б�
    						for(int i = 0;i < len;i++){	//��ʼ��commentList�����ݣ���Ԥ��ListViewӦ�����ݣ�
    							musicList.add(new Music(ja.getJSONObject(i).getString("mid"),
    														ja.getJSONObject(i).getString("title"),
    														ja.getJSONObject(i).getString("sname"),
    														ja.getJSONObject(i).getString("url")));
    						}
    						musicAdapter = new MusicAdapter(MainActivity.this, R.layout.musics_item, musicList);
    				        mView = mViews.get(0);										//��ȡ�ӽ���-ȫ�����ֽ���
    				        musics = (ListView)mView.findViewById(R.id.LV_musics);	//�������ͨ���Ӳ��ֵ��ÿؼ�
    				        musics.setAdapter(musicAdapter);
    				        musics.setOnItemClickListener(MusicCL);					//ע�ᵥ�����¼�
    					}
        			} catch (JSONException e) {
        				e.printStackTrace();
        			}
                    break;
                default:break;
            }
        }
    };
    //�����б�ListView�ĵ������¼�
    private AdapterView.OnItemClickListener MusicCL = new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			playMusic(position);	//����musicList��λ��position�ĸ���
		}
    };
    
    //����������Ϣ����ȡ����
    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String isRight = obj.getString("success");
        				if(isRight.equals("false")){		//��ȡ���۳���ʱ
        					noticeTitle.setText("��ȡ���۳���");
    					}else{
    						JSONArray ja = new JSONArray(obj.getString("result"));
    						int len = ja.length();
    						commentList.clear();		//����ǰ����б�
    						for(int i = 0;i < len;i++){	//��ʼ��commentList�����ݣ���Ԥ��ListViewӦ�����ݣ�
    							commentList.add(new Comment(ja.getJSONObject(i).getString("alias"),
    														ja.getJSONObject(i).getString("title"),
    														ja.getJSONObject(i).getString("content")));
    						}
    				        adapter = new CommentAdapter(MainActivity.this, R.layout.comments_item, commentList);
    				        mView = mViews.get(2);										//��ȡ�ӽ���-���۽���
    				        comments = (ListView)mView.findViewById(R.id.LV_comments);	//�������ͨ���Ӳ��ֵ��ÿؼ�
    				        comments.setAdapter(adapter);
    				        comments.setOnItemClickListener(ItemCL);					//ע�ᵥ�����¼�
    					}
        			} catch (JSONException e) {
        				e.printStackTrace();
        			}
                    break;
                default:break;
            }
        }
    };
    //����ListView�ĵ������¼�
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
	 * ��ʼ������
	 */
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpage);
		// ��ʼ�����ײ�����İ�ť
		btn_allMusic = (Button) findViewById(R.id.tab_btn_allMusic);
		btn_allMusic.getBackground().setAlpha(100);	//����Ϊ��͸��
		btn_music = (Button) findViewById(R.id.tab_btn_music);
		btn_music.getBackground().setAlpha(100);	//����Ϊ��͸��
		btn_notice = (Button) findViewById(R.id.tab_btn_notice);
		btn_notice.getBackground().setAlpha(100);	//����Ϊ��͸��
		// ��ʼ����������ؼ�
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.getBackground().setAlpha(100);	//����Ϊ��͸��
		btn_add.setVisibility(View.GONE);	//���ء����ۡ���ť
	}

	/**
	 * ��ʼ��ViewPage
	 */
	private void initViewPage() {
		// ��ʼ���ĸ�����
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
		
		// ��������ʼ��������
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
	 * �ж��ĸ�Ҫ��ʾ�������ð�ťͼƬ
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.tab_btn_allMusic:
			mViewPager.setCurrentItem(0);
			btn_add.setVisibility(View.GONE);	//���ء����ۡ���ť
			break;
		case R.id.tab_btn_music:
			mViewPager.setCurrentItem(1);
			btn_add.setVisibility(View.VISIBLE);//��ʾ�����ۡ���ť
			break;
		case R.id.tab_btn_notice:
			mViewPager.setCurrentItem(2);
			btn_add.setVisibility(View.GONE);	//���ء����ۡ���ť
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
            *ViewPage���һ���ʱ
            */
			@Override
			public void onPageSelected(int arg0) {
				int currentItem = mViewPager.getCurrentItem();
				switch (currentItem) {
				case 0:
					btn_add.setVisibility(View.GONE);	//���ء����ۡ���ť
					break;
				case 1:
					btn_add.setVisibility(View.VISIBLE);//��ʾ�����ۡ���ť
					break;
				case 2:
					btn_add.setVisibility(View.GONE);	//���ء����ۡ���ť
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
	
	//���ͻ�ȡ�����б������������������б�
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web������Ҫ�½�һ���߳�ִ��
            @Override
            public void run() {//���߳�ִ������
            	MySQLService query = null;			//����Mysql�����http����
                try{
                	query = new MySQLService(url);		//�½�Mysql�����http����
                	Message message = new Message();	//�����洢������Ϣ
                	String sql = "SELECT m.mid, title, sname, url from (music m LEFT OUTER JOIN singer s on m.singer_id = s.sid) LEFT OUTER JOIN music_url u on m.mid = u.mid ORDER BY degree DESC";
					if(query.sendQuery(sql)){			//�����Ͳ�ѯ��Ϣ�ɹ�
						message  = query.getMessage();	//��¼��ѯ���
						handler_music.sendMessage(message);	//���ݸ�handler
                	}else{
                		Log.d("debug","���Ͳ�ѯ������Ϣʧ��");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//�Ͽ�����
                	query.onDestroy();
                }
            }
        }).start();
    }
	
	//���ͻ�ȡ���۵������������������Ϣ
    private void sendRequestWithHttpUrlConnection2(){
        new Thread(new Runnable() {//web������Ҫ�½�һ���߳�ִ��
            @Override
            public void run() {//���߳�ִ������
            	MySQLService query = null;			//����Mysql�����http����
                try{
                	query = new MySQLService(url);		//�½�Mysql�����http����
                	Message message = new Message();	//�����洢������Ϣ
                	String sqlSentence = "SELECT m.title, u.alias, c.content from comment c, music m, user u where u.uid = c.uid and m.mid = c.mid";
					if(query.sendQuery(sqlSentence)){	//�����Ͳ�ѯ��Ϣ�ɹ�
						message  = query.getMessage();	//��¼��ѯ���
                		handler.sendMessage(message);	//���ݸ�handler
                	}else{
                		Log.d("debug","���Ͳ�ѯ��Ϣʧ��");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//�Ͽ�����
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
