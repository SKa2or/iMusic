package com.example.imusic;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//�Զ��������ListView��������
public class MusicAdapter extends ArrayAdapter<Music>{
	private int resourceId;
	
	public MusicAdapter(Context context, int textViewResourceId, List<Music> musics){
        super(context, textViewResourceId, musics);
        resourceId = textViewResourceId;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent){
		Music music = getItem(position);

		if(convertView == null){	//ֻ�е�convertView������ʱ������Ҫ�������룻����ֱ��ʹ����
			convertView = LayoutInflater.from(this.getContext()).inflate(resourceId, null);
		}
        TextView text_music = (TextView)convertView.findViewById(R.id.text_music);
        TextView text_singer = (TextView)convertView.findViewById(R.id.text_singer);

        text_music.setText("�������ƣ�"+music.getTitle());
        text_singer.setText("�������֣�"+music.getSinger());

        return convertView;
    }
}
