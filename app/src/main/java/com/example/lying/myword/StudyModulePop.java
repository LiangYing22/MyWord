package com.example.lying.myword;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class StudyModulePop extends PopupWindow {

    private Context context;
    private AdapterView.OnItemClickListener myOnItemClickListener;
    private List<String> myItems;
    private PopItemClickListener popItemClickListener;

    //RecyclerView控件
    RecyclerView recyclerView ;
    //popWindow的宽
    int width = 0;

    //构造函数
    public StudyModulePop(Context context,List<String> myItems){
        super(context);
        setBackgroundDrawable(new BitmapDrawable());//去掉自带的背景（半透明，黑的，丑的一批）
        setOutsideTouchable(true);//点击外面关闭pop，不加点击外面不关闭
        this.context = context;
        this.myItems = myItems;
//        setWidth(width);
//        setHeight(height);
        //加载布局
        View popView = LayoutInflater.from(context).inflate(R.layout.popwindow,null);
        /**
         * 很多时候要用到popupwindow 的宽和高来精确放置它，但是经常使用popupwindow.getWidth 值为0 或者 －2。
         * 原因是普通的view都会经历Measure－》layout然后显示在window上。
         * popupwindow是响应事件出现的，所以在它出现在屏幕之前，我们是不知道它的大小的。
         * 然后要获取popwindow的宽和高，只需要getMeasuredWidth()就能获取它的宽了。高度也是一样的。
         */
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);//测量加载的布局大小，
        width = popView.getMeasuredWidth();
        setContentView(popView);
        //绑定控件
        recyclerView = (RecyclerView)popView.findViewById(R.id.PopWindowModuleRecycler);
        //给recyclerview设置LayoutManager和适配器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        OneTextViewRecyclerViewAdapt adapt = new OneTextViewRecyclerViewAdapt(myItems);
        adapt.setListener(new OneTextViewRecyclerViewAdapt.Listener(){

            @Override
            public void onClickListener(int position) {
                popItemClickListener.onItemClickListener(StudyModulePop.this,StudyModulePop.this.myItems,position);
            }
        });
        recyclerView.setAdapter(adapt);
    }

    //设置监听对象
    public void setListener(PopItemClickListener popItemClickListener){
        this.popItemClickListener = popItemClickListener;
    }

    //显示popWindow
    public void show(View view){
        showAsDropDown(view, view.getWidth()/2-width/2, 0);
        Log.i("弹窗","view.getWidth()="+view.getWidth()+",width="+width);
    }

    //事件接口
    interface PopItemClickListener{
        void onItemClickListener(PopupWindow popupWindow,List<String> myItems,int position);
    }

}
