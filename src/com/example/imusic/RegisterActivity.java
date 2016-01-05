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
//ע�����
public class RegisterActivity extends Activity {
	//����ע������������
	private EditText edit_username = null;	//�û����༭��
	private EditText edit_nickname = null;	//�ǳ������
	private EditText edit_password = null;	//���������
	private EditText edit_password2 = null;	//����ȷ�Ͽ�
	private Button btn_register = null;		//ע�ᰴť
	private Button btn_back = null;			//���ذ�ť
	//���������õ���webService��ַ
	private final String url = "http://115.28.155.212/update";
	private final int UPDATE_CONTENT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//ȥ��������
		setContentView(R.layout.activity_register);
		//�󶨱������Ӧ�ؼ�
		edit_username = (EditText)findViewById(R.id.edit_username);
		edit_nickname = (EditText)findViewById(R.id.edit_nickname);
		edit_password = (EditText)findViewById(R.id.edit_password);
		edit_password2 = (EditText)findViewById(R.id.edit_password2);
		btn_register = (Button)findViewById(R.id.btn_register);
		btn_register.getBackground().setAlpha(100);	//����Ϊ��͸��
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_back.getBackground().setAlpha(100);		//����Ϊ��͸��
		//���÷��ذ�ť�����¼������ص�¼����
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//����Intent:��RegisterActivity����LoginActivity
				Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
				startActivity(i);				//��ϵͳ����Intent�����ص�¼����
				RegisterActivity.this.finish();	//������Acitivity
			}
		});
		//����ע�ᰴť�����¼���ע���û�
		btn_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String username = edit_username.getText().toString();
				String nickname = edit_nickname.getText().toString();
				String psw = edit_password.getText().toString();
				String psw2 = edit_password2.getText().toString();
				if(username.equals("")){
					edit_username.setHint("�û�������Ϊ�գ�");
					edit_username.requestFocus();	//�û����༭���ȡ����
				}else if(nickname.equals("")){
					edit_nickname.setHint("�ǳ�Ϊ��");
					edit_nickname.requestFocus();	//�ǳƱ༭���ȡ����
				}else if(psw.equals("")){
					edit_password.setHint("���벻��Ϊ�գ�");
					edit_password.requestFocus();	//����༭���ȡ����
				}else if(psw2.equals("")){
					edit_password2.setHint("��ȷ������");
					edit_password2.requestFocus();	//����ȷ�Ͽ��ȡ����
				}else if(!psw.equals(psw2)){
					edit_password.setText("");
					edit_password2.setText("");
					edit_password.setHint("�����������벻ͬ��");
					edit_password2.setHint("");
					edit_password.requestFocus();	//����༭���ȡ����
				}else{
					sendRequestWithHttpUrlConnection();
				}
			}
		});
	}
	
	//����ע���û���������(�����û���Ϣ��sql���)����÷�����Ϣ
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web������Ҫ�½�һ���߳�ִ��
            @Override
            public void run() {//���߳�ִ������
            	MySQLService register = null;			//����Mysql�����http����
            	String user = edit_username.getText().toString();
                String nickname = edit_nickname.getText().toString();
                String psw = edit_password.getText().toString();
                try{
                	register = new MySQLService(url);	//�½�Mysql�����http����
                	Message message = new Message();	//�����洢������Ϣ
					if(register.sendInsert(user, nickname, psw)){	//������ע����Ϣ�ɹ�
						message  = register.getMessage();
                		handler.sendMessage(message);	//���ݸ�handler
                	}else{
                		Log.d("debug","����ע����Ϣʧ��");
                	}
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//�Ͽ�����
                	register.onDestroy();
                }
            }
        }).start();
    }
    //handler�������߳���UI�̵߳�ͨѶ������UI��
    //����������Ϣ���ж�ע���Ƿ�ɹ�
    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                	try {
        				JSONObject obj = new JSONObject(message.obj.toString());
        				String userIsRight = obj.getString("success");
        				Log.d("debug","������Ϣ��"+userIsRight);
        				if(userIsRight.equals("false")){	//ע��ʧ��ʱ
    						edit_password.setText("");
    						edit_password2.setText("");		//�������
    						edit_password.setHint("ע��ʧ�ܣ�");
    						edit_password.requestFocus();	//����༭���ȡ����
    					}else{
    						edit_password.setText("");
    						edit_password2.setText("");		//�������
    						edit_password.setHint("ע��ɹ���");
    						edit_password.requestFocus();	//����༭���ȡ����
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
