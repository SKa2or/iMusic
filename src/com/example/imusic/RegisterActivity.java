package com.example.imusic;

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
//注册界面
public class RegisterActivity extends Activity {
	//声明注册界面所需变量
	private EditText edit_username = null;	//用户名编辑框
	private EditText edit_nickname = null;	//昵称输入框
	private EditText edit_password = null;	//密码输入框
	private EditText edit_password2 = null;	//密码确认框
	private Button btn_register = null;		//注册按钮
	private Button btn_back = null;			//返回按钮
	//定义我们用到的webService网址
	private final String url = "http://115.28.155.212/update";
	private final int UPDATE_CONTENT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//去掉标题栏
		setContentView(R.layout.activity_register);
		//绑定变量与对应控件
		edit_username = (EditText)findViewById(R.id.edit_username);
		edit_nickname = (EditText)findViewById(R.id.edit_nickname);
		edit_password = (EditText)findViewById(R.id.edit_password);
		edit_password2 = (EditText)findViewById(R.id.edit_password2);
		btn_register = (Button)findViewById(R.id.btn_register);
		btn_register.getBackground().setAlpha(100);	//调整为半透明
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_back.getBackground().setAlpha(100);		//调整为半透明
		//设置返回按钮监听事件，返回登录界面
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//定义Intent:由RegisterActivity启动LoginActivity
				Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
				startActivity(i);				//由系统处理Intent，返回登录界面
				RegisterActivity.this.finish();	//结束本Acitivity
			}
		});
		//设置注册按钮监听事件，注册用户
		btn_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String username = edit_username.getText().toString();
				String nickname = edit_nickname.getText().toString();
				String psw = edit_password.getText().toString();
				String psw2 = edit_password2.getText().toString();
				if(username.equals("")){
					edit_username.setHint("用户名不能为空！");
					edit_username.requestFocus();	//用户名编辑框获取焦点
				}else if(nickname.equals("")){
					edit_nickname.setHint("昵称为空");
					edit_nickname.requestFocus();	//昵称编辑框获取焦点
				}else if(psw.equals("")){
					edit_password.setHint("密码不能为空！");
					edit_password.requestFocus();	//密码编辑框获取焦点
				}else if(psw2.equals("")){
					edit_password2.setHint("请确认密码");
					edit_password2.requestFocus();	//密码确认框获取焦点
				}else if(!psw.equals(psw2)){
					edit_password.setText("");
					edit_password2.setText("");
					edit_password.setHint("两次输入密码不同！");
					edit_password2.setHint("");
					edit_password.requestFocus();	//密码编辑框获取焦点
				}else{
					sendRequestWithHttpUrlConnection();
				}
			}
		});
	}
	
	//发送注册用户的请求函数(插入用户信息的sql语句)，获得返回信息
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web服务需要新建一个线程执行
            @Override
            public void run() {//子线程执行请求
            	MySQLService register = null;			//建立Mysql服务的http连接
            	String user = edit_username.getText().toString();
                String nickname = edit_nickname.getText().toString();
                String psw = edit_password.getText().toString();
                try{
                	register = new MySQLService(url);	//新建Mysql服务的http连接
                	Message message = new Message();	//用来存储返回信息
					if(register.sendInsert(user, nickname, psw)){	//若发送注册信息成功
						message  = register.getMessage();
                		handler.sendMessage(message);	//传递给handler
                	}else{
                		Log.d("debug","发送注册信息失败");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//断开连接
                	register.onDestroy();
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
        				Log.d("debug","返回信息："+userIsRight);
        				if(userIsRight.equals("false")){	//注册失败时
    						edit_password.setText("");
    						edit_password2.setText("");		//清空数据
    						edit_password.setHint("注册失败！");
    						edit_password.requestFocus();	//密码编辑框获取焦点
    					}else{
    						edit_password.setText("");
    						edit_password2.setText("");		//清空数据
    						edit_password.setHint("注册成功！");
    						edit_password.requestFocus();	//密码编辑框获取焦点
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
		getMenuInflater().inflate(R.menu.register, menu);
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
