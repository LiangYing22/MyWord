package com.example.lying.myword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class StudySurveyActivity extends AppCompatActivity {

    RecyclerView SurveyRecyclerView;
    ImageView surveyBack ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_study_survey);

        //绑定控件
        SurveyRecyclerView = (RecyclerView)findViewById(R.id.studySurverRecyclerView);
        surveyBack = (ImageView)findViewById(R.id.surveyBack);

        //返回
        surveyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的设置
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        SurveyRecyclerView.setLayoutManager(layoutManager);

        //给RecycleView设置设配器
        StudySurveyActivityReclerViewAdapt adapter = new StudySurveyActivityReclerViewAdapt (this);
        //设置响应事件
        adapter.setListener(new StudySurveyActivityReclerViewAdapt.SurveyClickListener(){
            @Override
            public void unLearningClickListener(final String ModuleName, final List<Word> unlearning) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //发送事件
//                        EventBus.getDefault().post(new StudySurveyEvent(ModuleName,unlearning,1));
//                    }
//                }).start();
                //发送事件
                EventBus.getDefault().postSticky(new StudySurveyEvent(ModuleName,unlearning,1));
                Intent intent = new Intent(StudySurveyActivity.this,StudySurveyOfSpecificActivity.class);
                startActivity(intent);
            }

            @Override
            public void reviewClickListener(final String ModuleName, final List<Word> review) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //发送事件
//                        EventBus.getDefault().post(new StudySurveyEvent(ModuleName,review,2));
//                    }
//                }).start();
                //发送事件
                EventBus.getDefault().postSticky(new StudySurveyEvent(ModuleName,review,2));
                Intent intent = new Intent(StudySurveyActivity.this,StudySurveyOfSpecificActivity.class);
                startActivity(intent);
            }

            @Override
            public void masterClickListener(final String ModuleName, final List<Word> master) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //发送事件
//                        EventBus.getDefault().post(new StudySurveyEvent(ModuleName,master,3));
//                    }
//                }).start();
                //发送事件
                EventBus.getDefault().postSticky(new StudySurveyEvent(ModuleName,master,3));
                Intent intent = new Intent(StudySurveyActivity.this,StudySurveyOfSpecificActivity.class);
                startActivity(intent);
            }
        });
        SurveyRecyclerView.setAdapter(adapter);
    }
}
