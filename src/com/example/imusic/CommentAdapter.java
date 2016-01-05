package com.example.imusic;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
//�Զ�������۵�������
public class CommentAdapter extends ArrayAdapter<Comment>{
	private int resourceId;
	
	public CommentAdapter(Context context, int textViewResourceId, List<Comment> comments){
        super(context, textViewResourceId, comments);
        resourceId = textViewResourceId;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent){
		Comment comment = getItem(position);

		if(convertView == null){	//ֻ�е�convertView������ʱ������Ҫ�������룻����ֱ��ʹ����
			convertView = LayoutInflater.from(this.getContext()).inflate(resourceId, null);
		}
        TextView text_user = (TextView)convertView.findViewById(R.id.commentUser);
        TextView text_music = (TextView)convertView.findViewById(R.id.commentMusic);
        TextView text_comment = (TextView)convertView.findViewById(R.id.commentContent);

        text_user.setText("�����ߣ�"+comment.getAlias());
        text_music.setText("��������"+comment.getMusic());
        text_comment.setText("���ۣ�"+comment.getComment());

        return convertView;
    }
}
