package com.example.imusic;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
//自定义的评论的适配器
public class CommentAdapter extends ArrayAdapter<Comment>{
	private int resourceId;
	
	public CommentAdapter(Context context, int textViewResourceId, List<Comment> comments){
        super(context, textViewResourceId, comments);
        resourceId = textViewResourceId;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent){
		Comment comment = getItem(position);

		if(convertView == null){	//只有当convertView不存在时，才需要重新载入；否则，直接使用它
			convertView = LayoutInflater.from(this.getContext()).inflate(resourceId, null);
		}
        TextView text_user = (TextView)convertView.findViewById(R.id.commentUser);
        TextView text_music = (TextView)convertView.findViewById(R.id.commentMusic);
        TextView text_comment = (TextView)convertView.findViewById(R.id.commentContent);

        text_user.setText("评论者："+comment.getAlias());
        text_music.setText("歌曲名："+comment.getMusic());
        text_comment.setText("评论："+comment.getComment());

        return convertView;
    }
}
