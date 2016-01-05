package com.example.imusic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Message;
import android.util.Log;

//数据库操作服务
public class MySQLService {
	private HttpURLConnection connection = null;//建立一个http连接
	private Message message = null;				//建立message，储存从web获取的信息
	private final int UPDATE_CONTENT = 0;
	
	public MySQLService(String url){
		try{
			//新建一个URL对象，打开连接
	        connection = (HttpURLConnection) (new URL(url.toString()).openConnection());
	        connection.setRequestMethod("POST");//设置请求方式，post-提交数据给指定的服务器
	        connection.setConnectTimeout(8000);//连接等待时间
	        connection.setReadTimeout(8000);//读取等待时间
	        //建立message，储存从web获取的信息
	        message = new Message();			//新建message
	        Log.d("debug","建立连接");
		} catch (Exception e){
            e.printStackTrace();
		}	
	}
	
	//写onDestroy函数，实现销毁回收功能
	public void onDestroy(){
		if(connection != null){
            connection.disconnect();
        }
	}
	
	//获取message
	public Message getMessage(){
		Log.d("debug","获取返回信息");
		return this.message;
	}
	
	/* url = "http://115.28.155.212/update" */
	//发送插入信息，并接受返回信息，返回值为是否发送并接收成功
	public boolean sendInsert(String user,String nickname,String psw){
		Log.d("debug","发送注册信息");
		try{
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			String sqlSentence = String.format("insert into user(user,alias,password)" +
	        		"values('%s','%s','%s')",user,nickname,psw);	//插入操作的sql语句
			Log.d("debug","sql = " + sqlSentence);
			out.writeBytes("sql="+sqlSentence);
			InputStream in = connection.getInputStream();//从web Service得到的字符流
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder response = new StringBuilder();//网站的响应数据
	
	        String line;//读取所有的字符串
	        while ((line=reader.readLine())!=null){
	            response.append(line);
	        }

	        message.what= UPDATE_CONTENT;
	        message.obj = response.toString();	//获取解析后返回信息，即插入结果
	        return true;
		} catch (Exception e){
            e.printStackTrace();
		}
		return false;
	}
	
	/* url = "http://115.28.155.212/query" */
	//发送查询信息，并接受返回信息，返回值为是否发送并接收成功
	public boolean sendQuery(String sqlSentence){
		Log.d("debug","发送查询信息");
		try{
			//发送到web Service的字符流，以流的形式将请求写入到connection中
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            Log.d("debug","sql = " + sqlSentence);Log.d("debug","sql = " + sqlSentence);
            out.writeBytes("sql="+sqlSentence);
            //将得到的数据转化为字符串
            InputStream in = connection.getInputStream();//从web Service得到的字符流
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();//网站的响应数据

            String line;//读取所有的字符串
            while ((line=reader.readLine())!=null){
                response.append(line);
            }

		    message.what= UPDATE_CONTENT;
		    message.obj = response.toString();	//获取解析后返回信息，即插入结果
		    return true;
		} catch (Exception e){
	        e.printStackTrace();
		}
		return false;
	}
	
	/* url = "http://115.28.155.212/login" */
	//发送登录信息，并接受返回信息，返回值为是否发送并接收成功
	public boolean sendUser(String user,String psw){
		Log.d("debug","发送登录信息");
			try{
				//发送到web Service的字符流，以流的形式将请求写入到connection中
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes("user="+user + "&password="+psw);
                //将得到的数据转化为字符串
                InputStream in = connection.getInputStream();//从web Service得到的字符流
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();//网站的响应数据

                String line;//读取所有的字符串
                while ((line=reader.readLine())!=null){
                    response.append(line);
                }

		        message.what= UPDATE_CONTENT;
		        message.obj = response.toString();	//获取解析后返回信息，即插入结果
		        return true;
			} catch (Exception e){
	            e.printStackTrace();
			}
			return false;
		}
}
