package com.example.lying.myword;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class StartUpActivity extends AppCompatActivity {
    //ImageView StartUpImageView;//利用图片控件来显示全屏图片
    Button Jump_To_MainActivity_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去title
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);//去掉Activity上面的状态栏
        setContentView(R.layout.activity_startup);
        getWindow().setBackgroundDrawableResource(R.drawable.myword4);
        /*StartUpImageView=(ImageView)findViewById(R.id.StartUpImage);//利用图片控件来显示全屏图片
        StartUpImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.myword4));*///利用图片控件来显示全屏图片
        //跳转页面操作
        final Intent it = new Intent(this, MainActivity.class);
        //利用定时器实现定时跳转
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(it); //执行
                StartUpActivity.this.finish();
            }
        };
        timer.schedule(task, 1000 * 5); //5秒后
        //点击按钮实现快速跳转
        Jump_To_MainActivity_Button=(Button)findViewById(R.id.Jump_To_MainActivity_Button);
        Jump_To_MainActivity_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(it); //执行
                finish();
                timer.cancel();
            }
        });
    }
}
