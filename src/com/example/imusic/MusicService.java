package com.example.imusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
	//通过binder来保持Activity和Service的通信
	public final IBinder binder = new MyBinder();
	public class MyBinder extends Binder{
		MusicService getService(){
			return MusicService.this;
		}
	}
	//使用MediaPlayer创建对象并初始化
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
	//实现“播放/暂停”功能
	public void playOrPause(){
		if(mp.isPlaying()){
			mp.pause();
		}
		else{
			mp.start();
		}
	}
	//实现“停止”功能
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
	//重写onDestroy函数，实现销毁回收功能
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
