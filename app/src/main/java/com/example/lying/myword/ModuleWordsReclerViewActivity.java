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
import android.widget.TextView;

public class ModuleWordsReclerViewActivity extends AppCompatActivity {

    TextView moduleName;
    RecyclerView recyclerView;
    ImageView moduleWordBack;
    String ModuleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_module_words_recler_view);

        //绑定控件
        moduleName = (TextView)findViewById(R.id.specificModuleName);
        recyclerView = (RecyclerView)findViewById(R.id.ModuleWordsRecyclerView);
        moduleWordBack = (ImageView)findViewById(R.id.moduleWordBack);

        //返回
        moduleWordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //获取传过来的数据
        Intent intent = getIntent();
        if(null != intent){
            ModuleName = intent.getStringExtra("ModuleName");
        }

        //初始化TextView数据
        if(null != ModuleName && !"".equals(ModuleName)){
            moduleName.setText(ModuleName);
        }else{
            moduleName.setText("暂无模块");
        }

        //
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的设置
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        recyclerView.setLayoutManager(layoutManager);

        //给RecycleView设置设配器
        ModuleWordsReclerViewAdapt adapter = new ModuleWordsReclerViewAdapt(this,ModuleName);
        recyclerView.setAdapter(adapter);
    }
}
