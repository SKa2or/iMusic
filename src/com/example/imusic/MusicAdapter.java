package com.example.imusic;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//自定义的音乐ListView的适配器
public class MusicAdapter extends ArrayAdapter<Music>{
	private int resourceId;
	
	public MusicAdapter(Context context, int textViewResourceId, List<Music> musics){
        super(context, textViewResourceId, musics);
        resourceId = textViewResourceId;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent){
		Music music = getItem(position);

		if(convertView == null){	//只有当convertView不存在时，才需要重新载入；否则，直接使用它
			convertView = LayoutInflater.from(this.getContext()).inflate(resourceId, null);
		}
        TextView text_music = (TextView)convertView.findViewById(R.id.text_music);
        TextView text_singer = (TextView)convertView.findViewById(R.id.text_singer);

        text_music.setText("歌曲名称："+music.getTitle());
        text_singer.setText("歌手名字："+music.getSinger());

        return convertView;
    }
}
