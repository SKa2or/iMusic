package com.example.imusic;
//��װ����������Ϣ
public class Music {
	private String mid;		//����id
	private String title;	//������
	private String singer;	//����
	private String url;		//�������ص�ַ
	
	
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
