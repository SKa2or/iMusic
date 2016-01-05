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
//��¼����
public class LoginActivity extends Activity {
	//������¼�����������
	private EditText edit_username = null;	//�û��������
	private EditText edit_password = null;	//���������
	private Button btn_login = null;		//��¼��ť
	private Button btn_register = null;		//ע�ᰴť
	//���������õ���webService��ַ
	private final String url = "http://115.28.155.212/query";
	private final int UPDATE_CONTENT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//ȥ��������
		setContentView(R.layout.activity_login);
		//�󶨱������Ӧ�ؼ�
		edit_username = (EditText)findViewById(R.id.edit_username);
		edit_password = (EditText)findViewById(R.id.edit_password);
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_login.getBackground().setAlpha(100);	//����Ϊ��͸��
		btn_register = (Button)findViewById(R.id.btn_register);
		btn_register.getBackground().setAlpha(100);	//����Ϊ��͸��
		//����ע�ᰴť�����¼�����ת��ע�����
		btn_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//����Intent:��RegisterActivity����LoginActivity
				Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(i);				//��ϵͳ����Intent����ת��ע�����
			}
		});
		//���õ�¼��ť�����¼�����¼��������
		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//����Intent:��RegisterActivity����LoginActivity
				String username = edit_username.getText().toString();
				String psw = edit_password.getText().toString();
				if(username.equals("")){
					edit_username.setHint("�û���Ϊ��");
					edit_username.requestFocus();	//�û����༭���ȡ����
				}else if(psw.equals("")){
					edit_password.setHint("����Ϊ��");
					edit_password.requestFocus();	//����༭���ȡ����
				}else{
					//�����������󣬲���Hander���ж��û����������Ƿ���ȷ
					sendRequestWithHttpUrlConnection();
				}
			}
		});
	}
	
	//�����ж��û��������������������÷�����Ϣ
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web������Ҫ�½�һ���߳�ִ��
            @Override
            public void run() {//���߳�ִ������
            	MySQLService login = null;			//����Mysql�����http����
            	String user = edit_username.getText().toString();
                String psw = edit_password.getText().toString();
                try{
                	login = new MySQLService(url);		//�½�Mysql�����http����
                	String sql = String.format("SELECT uid FROM user WHERE user='%s' AND password='%s'",user,psw);
                	Message message = new Message();	//�����洢������Ϣ
					if(login.sendQuery(sql)){			//�����͵�¼��Ϣ�ɹ�
						message  = login.getMessage();	//��¼�Ƿ�����û���Ϊuser����Ϊpwd���û�
                		handler.sendMessage(message);	//���ݸ�handler
                	}else{
                		Log.d("debug","���͵�¼��Ϣʧ��");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//�Ͽ�����
                	login.onDestroy();
                }
            }
        }).start();
    }
    //handler�������߳���UI�̵߳�ͨѶ������UI��
    //����������Ϣ���ж��û����������Ƿ���ȷ
    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String userIsRight = obj.getString("success");
        				Log.d("debug","������Ϣ��"+userIsRight);
        				if(userIsRight.equals("false")){	//��ѯʧ��ʱ
    						edit_password.setText("");
    						edit_password.setHint("��ѯʧ�ܣ�");
    						edit_password.requestFocus();	//����༭���ȡ����
    					}else{
    						edit_password.setText("");
    						edit_password.setHint("��ѯ�ɹ���");
    						JSONArray ja = new JSONArray(obj.getString("result"));
    						int len = ja.length();
    						if(len == 0){		//δ�鵽���˺ţ��û������������
    							edit_password.setText("");
        						edit_password.setHint("�û������������");
    						}else{				//�鵽���˺�
    							edit_password.setText("");
        						edit_password.setHint("��¼�ɹ���");
    							Intent i = new Intent(LoginActivity.this,MainActivity.class);
        						Bundle bundle = new Bundle();	//�����û�����������
        						bundle.putString("userId",ja.getJSONObject(0).getString("uid"));
        						i.putExtras(bundle);
        						startActivity(i);				//��ϵͳ����Intent����¼��������
        						LoginActivity.this.finish();	//����������
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
