package com.example.lying.myword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class StudySurveyOfSpecificActivity extends AppCompatActivity {

    private String ModuleName = "";
    private List<Word>WordData = new ArrayList<>();
    private int Type = 0;
    TextView SpecificModuleName;
    RecyclerView SpecificRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_words_recler_view);

        //绑定控件
        SpecificModuleName = (TextView)findViewById(R.id.specificModuleName);
        SpecificRecyclerView = (RecyclerView)findViewById(R.id.ModuleWordsRecyclerView);

        //注册
        EventBus.getDefault().register(this);
    }

    //处理//获取传过来的数据
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(StudySurveyEvent event){
        ModuleName = event.getModuleName();
        WordData = event.getWordData();
        Type = event.getType();

        //显示数据
        if(ModuleName != null && !"".equals(ModuleName) && !WordData.isEmpty()){
            if(Type == 1){
                SpecificModuleName.setText("'"+ModuleName+"'"+"模块"+"未学习单词");
            }else if(Type == 2){
                SpecificModuleName.setText("'"+ModuleName+"'"+"模块"+"需复习单词");
            }else if(Type == 3){
                SpecificModuleName.setText("'"+ModuleName+"'"+"模块"+"已掌握单词");
            }
            //设置布局管理器
            LinearLayoutManager layoutManager = new LinearLayoutManager(StudySurveyOfSpecificActivity.this);
            //设置为垂直布局，这也是默认的设置
            layoutManager.setOrientation(OrientationHelper.VERTICAL);
            //给RecyclerView设置布局管理器
            SpecificRecyclerView.setLayoutManager(layoutManager);
            StudySurveyOfSpecificReclerViewAdapt adapt =new StudySurveyOfSpecificReclerViewAdapt(StudySurveyOfSpecificActivity.this) ;
            adapt.setData(WordData);
            adapt.notifyDataSetChanged();
            SpecificRecyclerView.setAdapter(adapt);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁
        EventBus.getDefault().unregister(this);
//        Log.i("StudySurveyOfSpecificActivity","Type:"+Type);
//        Log.i("StudySurveyOfSpecificActivity","WordData:"+WordData.get(0).getWordSpell());
    }
}
