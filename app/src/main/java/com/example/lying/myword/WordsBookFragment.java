package com.example.lying.myword;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.lying.myword.util.MyToast;
import com.example.lying.myword.util.countTimeUtil;
import com.example.lying.myword.util.getAssetsFileUtil;

import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordsBookFragment extends Fragment {

    //用于判断用户选择的模块类型1：首字母相同。2：乱序。默认为首字母相同，即默认为1；
    int ModuleType = 1;

    //用于回调
    public CallBack callBack=null;

    //TAG值
    public final static String TAG="WordsBookFragment";
    //assets文件下存放的txt文件
    List<String> list;
    //进度对话框
    private ProgressDialog pDialog = null;

    ListView WordsBookListView;
    TextView selectWordsbookName;
    TextView selectWordsbookModuleNum;
    TextView selectWordsbookWrodsNum;

    BaseAdapter mybaseadapter=null;
    //SharedPreferences对象
    SharedPreferences sharedPreferences = null;
    //数据库
    MyDBHelper mydatabasehelper;
    SQLiteDatabase mydatabase;
    //获取数据库中词本的信息
    int WordsBookID ;
    String WordsBookName ;
    String AddTime ;
    int WordsNumber ;
    int ModuleNumber ;


    //根据数据库数据初始化
    public void initDataFromDB(){
        list = getAssetsFileUtil.getAssetsTxtFile(getContext());
        mydatabasehelper=new MyDBHelper(getContext());
        mydatabase=mydatabasehelper.getWritableDatabase();
        flashTextView();
    }
    private void flashTextView(){
        Cursor cursor_wordsbook= mydatabase.rawQuery("select * from " + MyDBHelper.Table_WordsBook_NANE, null);
        int cursor_wordsbook_count=cursor_wordsbook.getCount();
        if(cursor_wordsbook_count>0){
            while(cursor_wordsbook.moveToNext()){
                WordsBookID = cursor_wordsbook.getInt(0);
                WordsBookName = cursor_wordsbook.getString(1);
                AddTime = cursor_wordsbook.getString(2);
                WordsNumber = cursor_wordsbook.getInt(3);
                ModuleNumber =cursor_wordsbook.getInt(4);
            }
            //显示数据
            selectWordsbookName.setText(WordsBookName);
            selectWordsbookModuleNum.setText("模块数量："+ModuleNumber);
            selectWordsbookWrodsNum.setText("单词数量："+WordsNumber);
        }
        cursor_wordsbook.close();
    }
    public void setCallBack(CallBack callBack){
        this.callBack = callBack;
    }

    /**
     * 初始化界面（回调方法）
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View WordsBookView=inflater.inflate(R.layout.wordsbook,container,false);

        selectWordsbookName=(TextView)WordsBookView.findViewById(R.id.selectWordsbookName);
        selectWordsbookModuleNum = (TextView)WordsBookView.findViewById(R.id.selectWordsbookModuleNum) ;
        selectWordsbookWrodsNum = (TextView)WordsBookView.findViewById(R.id.selectWordsbookWrodsNum) ;
        WordsBookListView=(ListView)WordsBookView.findViewById(R.id.WordsBookListView);

        //根据数据库数据初始化
        initDataFromDB();
        //点击事件
        selectWordsbookName.setOnClickListener(new MyonClickListener());
        selectWordsbookModuleNum.setOnClickListener(new MyonClickListener());
        selectWordsbookWrodsNum.setOnClickListener(new MyonClickListener());

        //自定义适配器
        mybaseadapter=new BaseAdapter() {

            //自定义方法
            public void setButtonText(Button button,String string){
                button.setText(string);
            }

            //获取ListView子项目的个数
            @Override
            public int getCount() {
                //Log.i(TAG,"获取的assets文件中的个数"+list.size());
                return list.size();
                //return 1;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            //获取ListView子项目的布局
            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                if(view==null){
                    //根据反射获取子项目布局的view
                    view= LayoutInflater.from(getActivity()).inflate(R.layout.wordsbook_listview_item,null);
                    //子项目布局中的TextView
                    TextView BookNameTextView=(TextView)view.findViewById(R.id.item_bookname);
                    //BookNameTextView.setText("英语六级词汇");
                    BookNameTextView.setText(list.get(i).replace(".txt",""));
                    //子项目布局中的Button
                    final Button AddBookButton=(Button)view.findViewById(R.id.item_add);
                    //初始化sharePreferences
                    sharedPreferences=getContext().getSharedPreferences("CurrentWordsBook",MODE_PRIVATE);
                    //根据（数据）判断是否应用了单词本，若应用了则设置按钮不可点击
                    //先判断sharedPreferences中的数据
                    if(sharedPreferences!=null){
                        String currentWrodsBook = sharedPreferences.getString("wordsbook","默认值");
                        //Log.i(TAG,"值为："+currentWrodsBook+i);
                        if(currentWrodsBook.equals(list.get(i).replace(".txt",""))){
                            AddBookButton.setText("已应用");
                            AddBookButton.setEnabled(false);
                        }
                    }
                    //设置子项目布局中的按钮，并设置按钮的点事件
                    AddBookButton.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            //先弹出一个对话框，让用户选择模块类型
                            final ModuleSelectDialog moduleSelectDialog =new ModuleSelectDialog(getContext());
                            moduleSelectDialog.setRadioCheckListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                    switch (i){
                                        case R.id.SelectForLetter:
                                            ModuleType = 1;
                                            break;
                                        case R.id.SelectForDisorder:
                                            ModuleType = 2;
                                            break;
                                        default:
                                            ModuleType = 1;
                                            break;
                                    }
                                }
                            });
                            moduleSelectDialog.setCancelListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    moduleSelectDialog.dismiss();
                                }
                            });
                            moduleSelectDialog.setConfirmListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(ModuleType == 1)
                                        MyToast.getToast(getContext(),"模块类型为：模块中首字母相同!");
                                    else
                                        MyToast.getToast(getContext(),"模块类型为：模块中单词随机!");
                                    //开始导入数据库操作
                                    Log.i(TAG,"发送的id:"+AddBookButton.getId());
                                    if(sharedPreferences==null){
                                        sharedPreferences=getContext().getSharedPreferences("CurrentWordsBook",MODE_PRIVATE);
                                    }
                                    //获取编辑对象
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    //写入数据
                                    editor.putString("wordsbook", list.get(i).replace(".txt",""));
                                    //提交,用于判断用户应用了哪个词本，重新进入时把此Item变成“已应用”
                                    editor.commit();
                                    //创建一个进度对话框
                                    // 创建ProgressDialog对象
                                    pDialog = new ProgressDialog(getActivity());
                                    // 设置进度条风格，风格为圆形，旋转的
                                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    // 设置ProgressDialog 标题
                                    pDialog.setTitle(list.get(i).replace(".txt",""));
                                    // 设置ProgressDialog 提示信息
                                    pDialog.setMessage("开始应用……");
                                    // 设置ProgressDialog 标题图标
                                    pDialog.setIcon(R.drawable.danciben);
                                    // 设置ProgressDialog 进度条进度
                                    pDialog.setProgress(100);
                                    // 设置ProgressDialog 的进度条是否不明确
                                    pDialog.setIndeterminate(true);
                                    // 设置ProgressDialog 是否可以按退回按键取消
                                    pDialog.setCancelable(false);
                                    pDialog.show();
                                    //开启线程执行耗时操作
                                    Runnable run = new Runnable() {
                                        @Override
                                        public void run() {
                                            Message msg1 = new Message();
                                            msg1.what=1001;
                                            mHandler.sendMessage(msg1);
                                            final SixLevelWordDecompose sixLevelWordDecompose=SixLevelWordDecompose.getSixLevelWordDecompose(getContext());
                                            countTimeUtil.countTime(new countTimeUtil.CallBack() {
                                                @Override
                                                public void doSometing() {
                                                    try {
                                                        sixLevelWordDecompose.WordsBookToDBFile(list.get(i),list.get(i).replace(".txt",""),ModuleType);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },TAG);

                                            Message msg2 = new Message();
                                            msg2.what=1002;
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("iNum",AddBookButton.getId());  //往Bundle中存放数据
                                            msg2.setData(bundle);//mes利用Bundle传递数据
                                            mHandler.sendMessage(msg2);
                                        }
                                    };
                                    new Thread(run).start();
                                    //可以在这把所有按钮变成可点击状态，并设置为“应用”
                                    //TODO
                                    if(mydatabasehelper!=null)
                                        mybaseadapter.notifyDataSetChanged();
                                    AddBookButton.setText("已应用");
                                    AddBookButton.setEnabled(false);
                                    //结束导入数据库操作
                                    moduleSelectDialog.dismiss();
                                }
                            });
                            moduleSelectDialog.show();
                        }
                    });
                    return view;
                }else{
                    return view;
                }
            }
            //响应线程，当线程执行完成后弹出确认对话框，已关闭进度对话框。
            Handler mHandler = new Handler(){
                public void handleMessage(android.os.Message msg) {
                    switch (msg.what)
                    {
                        case 1001:
                            pDialog.setMessage("正在应用……");
                            break;
                        case 1002:
                            pDialog.setMessage("完成应用。");
                            //pDialog.dismiss();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("单词应用")
                                    .setMessage("完成应用")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            pDialog.dismiss();
                                            //刷新页面
                                            //TODO
                                            flashTextView();
                                            Log.i(TAG,"准备调用刷新Fragment方法");
                                            if(callBack!=null){
                                                callBack.reLoadWordsBookFragmentView();
                                                Log.i(TAG,"完成调用刷新Fragment方法");
                                            }else{
                                                Log.i(TAG,"callBack为空");
                                            }
                                        }
                                    })
                                    .show();

                            break;
                        default:
                            break;
                    }
                };
            };
        };
        //ListView设置设配器
        WordsBookListView.setAdapter(mybaseadapter);
        mybaseadapter.notifyDataSetChanged();
        return WordsBookView;
    }
    private class MyonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.selectWordsbookName :
                case R.id.selectWordsbookModuleNum:
                case R.id.selectWordsbookWrodsNum:
                    Intent intent =new Intent(getActivity(),WordsModuleListActivity.class);
                    intent.putExtra("wordsbook",WordsBookName);
                    startActivity(intent);
            }
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mydatabase.close();
        mydatabasehelper.close();
    }
}
