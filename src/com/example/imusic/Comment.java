package com.example.imusic;
//封装单条评论
public class Comment {
	private String alias;	//用户昵称
	private String music;	//音乐名
	private String comment;	//评论内容

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
