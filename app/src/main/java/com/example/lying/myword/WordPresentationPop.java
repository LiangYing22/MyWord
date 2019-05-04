package com.example.lying.myword;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordPresentationPop extends PopupWindow {
    private String word;//单词
    private String ph;//音标
    private String means;//含义

    //控件
    TextView wordTextView,phTextView,meansTextView,NWS_speak;

    private Context context;

    //构造函数
    public WordPresentationPop(Context context, String word, String ph, String means, final TextToSpeech speak){
        super(context);
        setBackgroundDrawable(new BitmapDrawable());//去掉自带的背景（半透明，黑的，丑的一批）
        setOutsideTouchable(true);//点击外面关闭pop，不加点击外面不关闭
        this.context = context;
        this.word = word;
        this.ph = ph;
        this.means = means;
        //加载布局
        View popView = LayoutInflater.from(context).inflate(R.layout.newword_wordstudy,null);
        setContentView(popView);
        //绑定控件
        wordTextView = (TextView)popView.findViewById(R.id.NWS_word);
        phTextView = (TextView)popView.findViewById(R.id.NWS_ph);
        meansTextView = (TextView)popView.findViewById(R.id.NWS_means);
        NWS_speak = (TextView)popView.findViewById(R.id.NWS_speak);
        NWS_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak.speak(WordPresentationPop.this.word,TextToSpeech.QUEUE_ADD, null);
            }
        });
//        //判断单词为英文时显示
//        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//        Matcher m = p.matcher(this.word);
//        if (!m.find()) {
//            NWS_speak.setVisibility(View.VISIBLE);
//        }
        //显示数据
        wordTextView.setText(this.word);
        phTextView.setText(this.ph);
        meansTextView.setText(this.means);
    }

    //显示popWindow
    public void show(Activity activity){
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
}
