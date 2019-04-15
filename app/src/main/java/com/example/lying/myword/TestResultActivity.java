package com.example.lying.myword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TestResultActivity extends AppCompatActivity {

    TextView TestResultTitle,TestResultTotal,TestResultRight,TestResultError,TestResultNotTest;
    RecyclerView TestResultRecyclerView;

    String ModuleName = "";
    List<Word> WordData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        TestResultTitle = (TextView)findViewById(R.id.TestResultTitle);//标题
        TestResultTotal = (TextView)findViewById(R.id.TestResultTotal);//总数
        TestResultRight = (TextView)findViewById(R.id.TestResultRight);//正确数
        TestResultError = (TextView)findViewById(R.id.TestResultError);//错误数
        TestResultNotTest = (TextView)findViewById(R.id.TestResultNotTest);//未测试
        TestResultRecyclerView = (RecyclerView)findViewById(R.id.TestResultRecyclerView);//RecycleView
        //注册
        EventBus.getDefault().register(this);

    }

    //处理//获取传过来的数据
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(WordListEvent event){
        ModuleName = event.getModuleName();
        WordData = event.getWordData();
        if(WordData == null)WordData = new ArrayList<>();
        int total = WordData.size();
        int right = 0;
        int error = 0;
        int notTest = 0;
        if(total > 0){
            for(int i = 0;i<total;i++){
                if(WordData.get(i).getIsTestRight() == -1){
                    notTest++;
                }else if(WordData.get(i).getIsTestRight() == 0){
                    error++;
                }else if(WordData.get(i).getIsTestRight() == 1){
                    right++;
                }
            }
        }
        //设置显示
        TestResultTitle.setText(ModuleName+"模块测试成绩");
        TestResultTotal.setText(""+total);
        TestResultRight.setText(""+right);
        TestResultError.setText(""+error);
        TestResultNotTest.setText(""+notTest);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的设置
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        TestResultRecyclerView.setLayoutManager(layoutManager);
        //给RecycleView设置设配器
        TestResultRecyclerViewAdapt adapter = new TestResultRecyclerViewAdapt(WordData);
        TestResultRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁
        EventBus.getDefault().unregister(this);
    }
}
