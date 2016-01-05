package com.example.imusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowItemActivity extends Activity {
	Button mBtn =null;			//返回按钮
    TextView mTv_uid = null;	//评论者id
    TextView mTv_mid = null;	//歌曲id
    TextView mTv_content = null;//评论内容
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_item);
		
		mBtn = (Button)findViewById(R.id.btn_backToComment);
		mBtn.getBackground().setAlpha(100);	//调整为半透明
		mTv_uid = (TextView)findViewById(R.id.item_uid);
		mTv_mid = (TextView)findViewById(R.id.item_mid);
		mTv_content = (TextView)findViewById(R.id.item_content);
        //更新textView，显示当前项评论具体内容
        Bundle bundle = this.getIntent().getExtras();
        mTv_uid.setText("评论者：" + bundle.getString("uid"));
        mTv_mid.setText("歌曲名：" + bundle.getString("mid"));
        mTv_content.setText("评论：" + bundle.getString("content"));

        View.OnClickListener btnL = new View.OnClickListener(){//Back按钮的点击响应函数
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ShowItemActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mBtn.setOnClickListener(btnL);	//注册监听器
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
