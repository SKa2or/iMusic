package com.example.imusic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class AddCommentActivity extends Activity {
	//声明所需变量
	private EditText edit_id;		//评论者id
	private EditText edit_music;	//评论的歌曲的id
	private EditText edit_content;	//评论内容
	private Button btn_addComment;	//添加评论按钮
	private Button btn_commentBack;	//返回按钮
	//定义我们用到的webService网址
	private static final String url = "http://115.28.155.212/update";
	private static final int UPDATE_CONTENT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//去掉标题栏
		setContentView(R.layout.activity_add_commet);
		
		//绑定变量与控件
		edit_id = (EditText)findViewById(R.id.edit_commentId);
		edit_music = (EditText)findViewById(R.id.edit_commentMusic);
		edit_content = (EditText)findViewById(R.id.edit_commentContent);
		btn_addComment = (Button)findViewById(R.id.btn_addComment);
		btn_addComment.getBackground().setAlpha(100);	//调整为半透明
		btn_commentBack = (Button)findViewById(R.id.btn_commentBack);
		btn_commentBack.getBackground().setAlpha(100);	//调整为半透明
		//接收数据
		Bundle bundle = this.getIntent().getExtras();
		edit_id.setText(bundle.getString("uid"));
		Log.d("debug", bundle.getString("uid"));
		edit_music.setText(bundle.getString("mid"));
		Log.d("debug", bundle.getString("mid"));
		
		//声明添加评论按钮的监听函数
		btn_addComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendRequestWithHttpUrlConnection();	//发送添加评论请求
			}
		});
		//声明返回按钮监听函数
		btn_commentBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(AddCommentActivity.this,MainActivity.class);
				startActivity(i);					//返回主界面
				AddCommentActivity.this.finish();	//结束添加活动
			}
		});
	}

	//发送添加评论的请求函数(插入评论的sql语句)，获得返回信息
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web服务需要新建一个线程执行
            @Override
            public void run() {//子线程执行请求
                HttpURLConnection connection = null;//建立一个http连接
                try{
                    //新建一个URL对象，打开连接
                    connection = (HttpURLConnection) (new URL(url.toString()).openConnection());
                    connection.setRequestMethod("POST");//设置请求方式，post-提交数据给指定的服务器
                    connection.setConnectTimeout(8000);//连接等待时间
                    connection.setReadTimeout(8000);//读取等待时间
                    //发送到web Service的字符流，以流的形式将请求写入到connection中
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String id = edit_id.getText().toString();
                    String music = edit_music.getText().toString();
                    String content = edit_content.getText().toString();
                    String sqlSentence = String.format("insert into comment(uid,mid,content)" +
                    		"values('%s','%s','%s')",id,music,content);	//插入操作的sql语句
                    Log.d("debug", sqlSentence);
                    out.writeBytes("sql="+sqlSentence);
                    //将得到的数据转化为字符串
                    InputStream in = connection.getInputStream();//从web Service得到的字符流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();//网站的响应数据

                    String line;//读取所有的字符串
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }

                    Message message = new Message();//新建message，传递给handler
                    message.what=UPDATE_CONTENT;
                    //获取解析后的字符串，即插入结果
                    message.obj = response.toString();

                    handler.sendMessage(message);//传递给handler
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//断开连接
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    //handler用于子线程与UI线程的通讯，更新UI：
    //分析返回信息，判断注册是否成功
    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String userIsRight = obj.getString("success");
        				if(userIsRight.equals("false")){	//添加评论失败时
    						edit_content.setText("");		//清空数据
    						edit_content.setHint("用户id或歌曲id错误！");
    						edit_content.requestFocus();	//编辑框获取焦点
    					}else{								//添加评论成功时
    						edit_content.setText("");		//清空数据
    						edit_content.setHint("添加评论成功！");
    						edit_content.requestFocus();	//编辑框获取焦点
    					}
        			} catch (JSONException e) {
        				e.printStackTrace();
        			}
                    break;
                default:break;
            }
        }
    };
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_commet, menu);
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
