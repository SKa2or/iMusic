package com.example.imusic;
//封装单个音乐信息
public class Music {
	private String mid;		//音乐id
	private String title;	//音乐名
	private String singer;	//歌手
	private String url;		//音乐下载地址
	
	
	public Music(String mid, String title, String singer, String url) {
		this.mid = mid;
		this.title = title;
		this.singer = singer;
		this.url = url;
	}
	
	public String getMid() {
		return mid;
	}
	public String getTitle() {
		return title;
	}
	public String getSinger() {
		return singer;
	}
	public String getUrl() {
		return url;
	}
}
