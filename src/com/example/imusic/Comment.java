package com.example.imusic;
//��װ��������
public class Comment {
	private String alias;	//�û��ǳ�
	private String music;	//������
	private String comment;	//��������

	public Comment(String alias,String music,String comment){
		this.alias = alias;
		this.music = music;
		this.comment = comment;
	}
	
	public String getAlias(){
		return this.alias;
	}
	
	public String getMusic(){
		return this.music;
	}
	
	public String getComment(){
		return this.comment;
	}
}
