package com.example.imusic;

import org.json.JSONArray;
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
//登录界面
public class LoginActivity extends Activity {
	//声明登录界面所需变量
	private EditText edit_username = null;	//用户名输入框
	private EditText edit_password = null;	//密码输入框
	private Button btn_login = null;		//登录按钮
	private Button btn_register = null;		//注册按钮
	//定义我们用到的webService网址
	private final String url = "http://115.28.155.212/query";
	private final int UPDATE_CONTENT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//去掉标题栏
		setContentView(R.layout.activity_login);
		//绑定变量与对应控件
		edit_username = (EditText)findViewById(R.id.edit_username);
		edit_password = (EditText)findViewById(R.id.edit_password);
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_login.getBackground().setAlpha(100);	//调整为半透明
		btn_register = (Button)findViewById(R.id.btn_register);
		btn_register.getBackground().setAlpha(100);	//调整为半透明
		//设置注册按钮监听事件，跳转至注册界面
		btn_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//定义Intent:由RegisterActivity启动LoginActivity
				Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(i);				//由系统处理Intent，跳转至注册界面
			}
		});
		//设置登录按钮监听事件，登录至主界面
		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//定义Intent:由RegisterActivity启动LoginActivity
				String username = edit_username.getText().toString();
				String psw = edit_password.getText().toString();
				if(username.equals("")){
					edit_username.setHint("用户名为空");
					edit_username.requestFocus();	//用户名编辑框获取焦点
				}else if(psw.equals("")){
					edit_password.setHint("密码为空");
					edit_password.requestFocus();	//密码编辑框获取焦点
				}else{
					//发送连接请求，并在Hander中判断用户名和密码是否正确
					sendRequestWithHttpUrlConnection();
				}
			}
		});
	}
	
	//发送判断用户名与密码的请求函数，获得返回信息
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web服务需要新建一个线程执行
            @Override
            public void run() {//子线程执行请求
            	MySQLService login = null;			//建立Mysql服务的http连接
            	String user = edit_username.getText().toString();
                String psw = edit_password.getText().toString();
                try{
                	login = new MySQLService(url);		//新建Mysql服务的http连接
                	String sql = String.format("SELECT uid FROM user WHERE user='%s' AND password='%s'",user,psw);
                	Message message = new Message();	//用来存储返回信息
					if(login.sendQuery(sql)){			//若发送登录信息成功
						message  = login.getMessage();	//记录是否存在用户名为user密码为pwd的用户
                		handler.sendMessage(message);	//传递给handler
                	}else{
                		Log.d("debug","发送登录信息失败");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//断开连接
                	login.onDestroy();
                }
            }
        }).start();
    }
    //handler用于子线程与UI线程的通讯，更新UI：
    //分析返回信息，判断用户名和密码是否正确
    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String userIsRight = obj.getString("success");
        				Log.d("debug","返回信息："+userIsRight);
        				if(userIsRight.equals("false")){	//查询失败时
    						edit_password.setText("");
    						edit_password.setHint("查询失败！");
    						edit_password.requestFocus();	//密码编辑框获取焦点
    					}else{
    						edit_password.setText("");
    						edit_password.setHint("查询成功！");
    						JSONArray ja = new JSONArray(obj.getString("result"));
    						int len = ja.length();
    						if(len == 0){		//未查到此账号，用户名或密码错误
    							edit_password.setText("");
        						edit_password.setHint("用户名或密码错误！");
    						}else{				//查到此账号
    							edit_password.setText("");
        						edit_password.setHint("登录成功！");
    							Intent i = new Intent(LoginActivity.this,MainActivity.class);
        						Bundle bundle = new Bundle();	//传递用户名到主界面
        						bundle.putString("userId",ja.getJSONObject(0).getString("uid"));
        						i.putExtras(bundle);
        						startActivity(i);				//由系统处理Intent，登录至主界面
        						LoginActivity.this.finish();	//结束本界面
    						}
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
		getMenuInflater().inflate(R.menu.login, menu);
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
