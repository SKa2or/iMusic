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
	//�����������
	private EditText edit_id;		//������id
	private EditText edit_music;	//���۵ĸ�����id
	private EditText edit_content;	//��������
	private Button btn_addComment;	//������۰�ť
	private Button btn_commentBack;	//���ذ�ť
	//���������õ���webService��ַ
	private static final String url = "http://115.28.155.212/update";
	private static final int UPDATE_CONTENT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//ȥ��������
		setContentView(R.layout.activity_add_commet);
		
		//�󶨱�����ؼ�
		edit_id = (EditText)findViewById(R.id.edit_commentId);
		edit_music = (EditText)findViewById(R.id.edit_commentMusic);
		edit_content = (EditText)findViewById(R.id.edit_commentContent);
		btn_addComment = (Button)findViewById(R.id.btn_addComment);
		btn_addComment.getBackground().setAlpha(100);	//����Ϊ��͸��
		btn_commentBack = (Button)findViewById(R.id.btn_commentBack);
		btn_commentBack.getBackground().setAlpha(100);	//����Ϊ��͸��
		//��������
		Bundle bundle = this.getIntent().getExtras();
		edit_id.setText(bundle.getString("uid"));
		Log.d("debug", bundle.getString("uid"));
		edit_music.setText(bundle.getString("mid"));
		Log.d("debug", bundle.getString("mid"));
		
		//����������۰�ť�ļ�������
		btn_addComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendRequestWithHttpUrlConnection();	//���������������
			}
		});
		//�������ذ�ť��������
		btn_commentBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(AddCommentActivity.this,MainActivity.class);
				startActivity(i);					//����������
				AddCommentActivity.this.finish();	//������ӻ
			}
		});
	}

	//����������۵�������(�������۵�sql���)����÷�����Ϣ
    private void sendRequestWithHttpUrlConnection(){
        new Thread(new Runnable() {//web������Ҫ�½�һ���߳�ִ��
            @Override
            public void run() {//���߳�ִ������
                HttpURLConnection connection = null;//����һ��http����
                try{
                    //�½�һ��URL���󣬴�����
                    connection = (HttpURLConnection) (new URL(url.toString()).openConnection());
                    connection.setRequestMethod("POST");//��������ʽ��post-�ύ���ݸ�ָ���ķ�����
                    connection.setConnectTimeout(8000);//���ӵȴ�ʱ��
                    connection.setReadTimeout(8000);//��ȡ�ȴ�ʱ��
                    //���͵�web Service���ַ�������������ʽ������д�뵽connection��
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String id = edit_id.getText().toString();
                    String music = edit_music.getText().toString();
                    String content = edit_content.getText().toString();
                    String sqlSentence = String.format("insert into comment(uid,mid,content)" +
                    		"values('%s','%s','%s')",id,music,content);	//���������sql���
                    Log.d("debug", sqlSentence);
                    out.writeBytes("sql="+sqlSentence);
                    //���õ�������ת��Ϊ�ַ���
                    InputStream in = connection.getInputStream();//��web Service�õ����ַ���
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();//��վ����Ӧ����

                    String line;//��ȡ���е��ַ���
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }

                    Message message = new Message();//�½�message�����ݸ�handler
                    message.what=UPDATE_CONTENT;
                    //��ȡ��������ַ�������������
                    message.obj = response.toString();

                    handler.sendMessage(message);//���ݸ�handler
                } catch (Exception e){
                    e.printStackTrace();
                } finally {//�Ͽ�����
                    if(connection!=null){
                        connection.disconnect();
                    }
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
        				if(userIsRight.equals("false")){	//�������ʧ��ʱ
    						edit_content.setText("");		//�������
    						edit_content.setHint("�û�id�����id����");
    						edit_content.requestFocus();	//�༭���ȡ����
    					}else{								//������۳ɹ�ʱ
    						edit_content.setText("");		//�������
    						edit_content.setHint("������۳ɹ���");
    						edit_content.requestFocus();	//�༭���ȡ����
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
