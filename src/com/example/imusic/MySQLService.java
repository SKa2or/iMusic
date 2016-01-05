package com.example.imusic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Message;
import android.util.Log;

//���ݿ��������
public class MySQLService {
	private HttpURLConnection connection = null;//����һ��http����
	private Message message = null;				//����message�������web��ȡ����Ϣ
	private final int UPDATE_CONTENT = 0;
	
	public MySQLService(String url){
		try{
			//�½�һ��URL���󣬴�����
	        connection = (HttpURLConnection) (new URL(url.toString()).openConnection());
	        connection.setRequestMethod("POST");//��������ʽ��post-�ύ���ݸ�ָ���ķ�����
	        connection.setConnectTimeout(8000);//���ӵȴ�ʱ��
	        connection.setReadTimeout(8000);//��ȡ�ȴ�ʱ��
	        //����message�������web��ȡ����Ϣ
	        message = new Message();			//�½�message
	        Log.d("debug","��������");
		} catch (Exception e){
            e.printStackTrace();
		}	
	}
	
	//дonDestroy������ʵ�����ٻ��չ���
	public void onDestroy(){
		if(connection != null){
            connection.disconnect();
        }
	}
	
	//��ȡmessage
	public Message getMessage(){
		Log.d("debug","��ȡ������Ϣ");
		return this.message;
	}
	
	/* url = "http://115.28.155.212/update" */
	//���Ͳ�����Ϣ�������ܷ�����Ϣ������ֵΪ�Ƿ��Ͳ����ճɹ�
	public boolean sendInsert(String user,String nickname,String psw){
		Log.d("debug","����ע����Ϣ");
		try{
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			String sqlSentence = String.format("insert into user(user,alias,password)" +
	        		"values('%s','%s','%s')",user,nickname,psw);	//���������sql���
			Log.d("debug","sql = " + sqlSentence);
			out.writeBytes("sql="+sqlSentence);
			InputStream in = connection.getInputStream();//��web Service�õ����ַ���
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder response = new StringBuilder();//��վ����Ӧ����
	
	        String line;//��ȡ���е��ַ���
	        while ((line=reader.readLine())!=null){
	            response.append(line);
	        }

	        message.what= UPDATE_CONTENT;
	        message.obj = response.toString();	//��ȡ�����󷵻���Ϣ����������
	        return true;
		} catch (Exception e){
            e.printStackTrace();
		}
		return false;
	}
	
	/* url = "http://115.28.155.212/query" */
	//���Ͳ�ѯ��Ϣ�������ܷ�����Ϣ������ֵΪ�Ƿ��Ͳ����ճɹ�
	public boolean sendQuery(String sqlSentence){
		Log.d("debug","���Ͳ�ѯ��Ϣ");
		try{
			//���͵�web Service���ַ�������������ʽ������д�뵽connection��
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            Log.d("debug","sql = " + sqlSentence);Log.d("debug","sql = " + sqlSentence);
            out.writeBytes("sql="+sqlSentence);
            //���õ�������ת��Ϊ�ַ���
            InputStream in = connection.getInputStream();//��web Service�õ����ַ���
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();//��վ����Ӧ����

            String line;//��ȡ���е��ַ���
            while ((line=reader.readLine())!=null){
                response.append(line);
            }

		    message.what= UPDATE_CONTENT;
		    message.obj = response.toString();	//��ȡ�����󷵻���Ϣ����������
		    return true;
		} catch (Exception e){
	        e.printStackTrace();
		}
		return false;
	}
	
	/* url = "http://115.28.155.212/login" */
	//���͵�¼��Ϣ�������ܷ�����Ϣ������ֵΪ�Ƿ��Ͳ����ճɹ�
	public boolean sendUser(String user,String psw){
		Log.d("debug","���͵�¼��Ϣ");
			try{
				//���͵�web Service���ַ�������������ʽ������д�뵽connection��
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes("user="+user + "&password="+psw);
                //���õ�������ת��Ϊ�ַ���
                InputStream in = connection.getInputStream();//��web Service�õ����ַ���
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();//��վ����Ӧ����

                String line;//��ȡ���е��ַ���
                while ((line=reader.readLine())!=null){
                    response.append(line);
                }

		        message.what= UPDATE_CONTENT;
		        message.obj = response.toString();	//��ȡ�����󷵻���Ϣ����������
		        return true;
			} catch (Exception e){
	            e.printStackTrace();
			}
			return false;
		}
}
