package com.example.lying.myword;



import android.app.Fragment;
import android.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements CallBack{

    //全局变量
    private TabHost mTabHost;//TabHost控件
    //选项卡图标
    private int[] TabIcons=new int[]{R.drawable.chadanci,R.drawable.home,R.drawable.danciben,R.drawable.shengciben};
    //选项卡标记
    private String[] TabTags=new String[]{"searchword","myhome","wordsbook","newwordsbook"};
    //选项卡标题
    private String[] TabNames=new String[]{"查单词","主页","单词本","生词本"};
    SearchWordFragment searchWordFragment = new SearchWordFragment();
    HomeFragment homeFragment = new HomeFragment();
    WordsBookFragment wordsBookFragment = new WordsBookFragment();
    NewWordsBookFragment newWordsBookFragment = new NewWordsBookFragment();
    //fragmentList
    private Fragment[] FragmentList=new Fragment[]{searchWordFragment,homeFragment,wordsBookFragment,newWordsBookFragment};
    private List<Fragment> FragmentArrys=Arrays.asList(FragmentList);
    private List FragmentArrys1=new ArrayList(FragmentArrys);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示标题栏
        setContentView(R.layout.activity_main);

        mTabHost=(TabHost)findViewById(R.id.mTabHost);//关联TabHost控件
        mTabHost.setup();
        //循环添加选项卡
        for(int i=0;i<TabIcons.length;i++){
            TabHost.TabSpec tabSpec=mTabHost.newTabSpec(TabTags[i]);//创建一个选项
            View view=getLayoutInflater().inflate(R.layout.tab,null);
            ImageView icon=(ImageView)view.findViewById(R.id.TabIcon);//关联选项卡中布局控件
            TextView tabname=(TextView)view.findViewById(R.id.TabName);//关联选项卡中布局控件
            icon.setImageResource(TabIcons[i]);//设置图标
            tabname.setText(TabNames[i]);
            tabSpec.setIndicator(view);//为选项卡设置view
            tabSpec.setContent(R.id.realcontent);//为选项卡设置控制的内容
            mTabHost.addTab(tabSpec);//为TabHost添加选项
        }
        mTabHost.setOnTabChangedListener(new MyTabChangeListener());
        mTabHost.setCurrentTabByTag("myhome");
        wordsBookFragment.setCallBack(this);
    }
    private class MyTabChangeListener implements TabHost.OnTabChangeListener {
        public void onTabChanged(String tabTag){
            FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();//开始事务
            if(tabTag.equalsIgnoreCase("myhome")){
                fragmentTransaction.replace(R.id.realcontent,homeFragment,"myhome");
                initImageView();//把所有选项图标设置成 未点击状态
                ImageView icon=(ImageView)mTabHost.getCurrentTabView().findViewById(R.id.TabIcon);//设置当前选项图标未点击状态
                icon.setImageResource(R.drawable.home2);//设置当前选项图标未点击状态
            }
            if(tabTag.equalsIgnoreCase("searchword")){
                fragmentTransaction.replace(R.id.realcontent,searchWordFragment,"searchword");
                initImageView();
                ImageView icon=(ImageView)mTabHost.getCurrentTabView().findViewById(R.id.TabIcon);
                icon.setImageResource(R.drawable.chadanci2);
            }
            if(tabTag.equalsIgnoreCase("wordsbook")){
                fragmentTransaction.replace(R.id.realcontent,wordsBookFragment,"wordsbook");
                initImageView();
                ImageView icon=(ImageView)mTabHost.getCurrentTabView().findViewById(R.id.TabIcon);
                icon.setImageResource(R.drawable.danciben2);
            }
            if(tabTag.equalsIgnoreCase("newwordsbook")){
                fragmentTransaction.replace(R.id.realcontent,newWordsBookFragment,"newwordsbook");
                initImageView();
                ImageView icon=(ImageView)mTabHost.getCurrentTabView().findViewById(R.id.TabIcon);
                icon.setImageResource(R.drawable.shengciben2);
            }
            fragmentTransaction.commit();//提交事务
        }
    }
    //把选项图标设置成 未点击状态\s+([A-Z]?[a-z]+)(.*)
    public void initImageView(){
        for(int i=0;i<TabIcons.length;i++){
            View mView = mTabHost.getTabWidget().getChildAt(i);
            ImageView icon=(ImageView)mView.findViewById(R.id.TabIcon);
            icon.setImageResource(TabIcons[i]);//设置图标
        }
    }
    @Override
    public void reLoadWordsBookFragmentView(){
        /*现将该fragment从fragmentList移除*/
        Log.i("MainActivity","进入方法");
        if (FragmentArrys1.contains(wordsBookFragment)){
            FragmentArrys1.remove(wordsBookFragment);
            Log.i("MainActivity","已从列表中移除WordsBookFramgment");
        }
        /*从FragmentManager中移除*/
        getFragmentManager().beginTransaction().remove(wordsBookFragment).commit();
        Log.i("MainActivity","已Manager中移除WordsBookFramgment");
        /*重新创建*/
        wordsBookFragment=new WordsBookFragment();
        wordsBookFragment.setCallBack(this);
        Log.i("MainActivity","已重新创建WordsBookFramgment");
        /*添加到fragmentList*/
        FragmentArrys1.add(wordsBookFragment);
        Log.i("MainActivity","FragmentArrys1中已重新添加WordsBookFramgment");
        /*显示*/
        getFragmentManager().beginTransaction().replace(R.id.realcontent,wordsBookFragment,"wordsbook").commit();
        Log.i("MainActivity","已重新显示WordsBookFramgment");
    }
}
