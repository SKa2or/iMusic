package com.example.imusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
	//ͨ��binder������Activity��Service��ͨ��
	public final IBinder binder = new MyBinder();
	public class MyBinder extends Binder{
		MusicService getService(){
			return MusicService.this;
		}
	}
	//ʹ��MediaPlayer�������󲢳�ʼ��
	public static MediaPlayer mp = new MediaPlayer();
	public MusicService(){
		try{
			mp.setDataSource("/data/You.mp3");
			mp.prepare();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//ʵ�֡�����/��ͣ������
	public void playOrPause(){
		if(mp.isPlaying()){
			mp.pause();
		}
		else{
			mp.start();
		}
	}
	//ʵ�֡�ֹͣ������
	public void stop(){
		if(mp != null){
			mp.stop();
		}
		try{
			mp.prepare();
			mp.seekTo(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//��дonDestroy������ʵ�����ٻ��չ���
	@Override
	public void onDestroy(){
		mp.stop();
		mp.release();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
