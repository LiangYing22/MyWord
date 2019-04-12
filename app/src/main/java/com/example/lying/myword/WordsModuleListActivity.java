package com.example.lying.myword;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lying.myword.util.getAssetsFileUtil;

import java.lang.reflect.Array;
import java.util.Arrays;

public class WordsModuleListActivity extends AppCompatActivity {

    TextView activity_moduleTextName;
    ListView activity_moduleListName;
    //数据库
    MyDBHelper mydatabasehelper;
    SQLiteDatabase mydatabase;
    //数据库中的数据
    String[] ModuleName;
    int[] ModuleWordsNum;
    //模块数量
    int ModuleNum=0;
    //适配器对象
    BaseAdapter mybaseadapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_module_list);

        //获取intent中的数据
        String WordsBookName = getIntent().getStringExtra("wordsbook");

        //bindView
        activity_moduleTextName = (TextView)findViewById(R.id.activity_moduleTextName);
        activity_moduleListName = (ListView) findViewById(R.id.activity_moduleListName);
        //bindDB
        mydatabasehelper = new MyDBHelper(this);
        mydatabase = mydatabasehelper.getWritableDatabase();
        //getModule
        if(null != WordsBookName && !"".equals(WordsBookName)){
            activity_moduleTextName.setText(WordsBookName+"的模块");
            activity_moduleTextName.setTypeface(getAssetsFileUtil.getAssetsTtfFile(this));
            Cursor cursor=mydatabase.rawQuery("select ModuleName,ModuleWordsNumber from " + MyDBHelper.Table_WordsModule_NANE+" where ModuleOfWordsBook=?", new String[]{WordsBookName},null);
            ModuleNum= cursor.getCount();
            if(ModuleNum>0){
                int i=0;//用于计数
                ModuleName = new String[ModuleNum];
                ModuleWordsNum = new int[ModuleNum];
                while(cursor.moveToNext()){
                    ModuleName[i]=cursor.getString(0);
                    ModuleWordsNum[i]=cursor.getInt(1);
                    i++;
                }
                cursor.close();
            }
        }
        //自定义适配器
        mybaseadapter=new BaseAdapter() {

            @Override
            public int getCount() {
                return ModuleNum;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            class ViewHolder{
                private TextView WordsModuleName;
                private TextView WordsNum;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                ViewHolder viewHolder = null;

                if(view == null){

                    LayoutInflater inflater =  (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.wordsmodule_listview_item,null);

                    viewHolder = new ViewHolder();
                    viewHolder.WordsModuleName=(TextView)view.findViewById(R.id.WordsModuleName);
                    viewHolder.WordsNum=(TextView)view.findViewById(R.id.WordsNum);

                    view.setTag(viewHolder);

                }else{
                    viewHolder =(ViewHolder) view.getTag();
                }
                if(ModuleName != null){
                    Log.i("模块界面", Arrays.toString(ModuleName));
                    Log.i("模块界面", ""+i);
                    String str="<font color='#FFD0F5F5'>模块:</font><font color='#7BCDE1F0'>"+ModuleName[i]+"</font>";
                    viewHolder.WordsModuleName.setText(Html.fromHtml(str));
                    viewHolder.WordsNum.setText("单词数量:"+ModuleWordsNum[i]);
                }
                return view;
            }
        };
        activity_moduleListName.setAdapter(mybaseadapter);
        //ListView 的单击事件
        activity_moduleListName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO
                //实现跳转到具体的模块单词列表中
                Intent intent = new Intent(WordsModuleListActivity.this,ModuleWordsReclerViewActivity.class);
                intent.putExtra("ModuleName",ModuleName[i]);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mydatabase.close();
        mydatabasehelper.close();
    }
}
