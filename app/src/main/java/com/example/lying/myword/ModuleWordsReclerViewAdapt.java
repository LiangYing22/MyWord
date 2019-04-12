package com.example.lying.myword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ModuleWordsReclerViewAdapt extends RecyclerView.Adapter<ModuleWordsReclerViewAdapt.ViewHolder>{

    String TAG = "ModuleWordsReclerViewAdapt";

    //创建ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView WordSpelling,WordChineseMean,AddToNewWords;
        public ViewHolder(View view){
            super(view);
            WordSpelling = (TextView)view.findViewById(R.id.WordSpelling);
            WordChineseMean = (TextView)view.findViewById(R.id.WordChineseMean);
            AddToNewWords = (TextView)view.findViewById(R.id.AddToNewWords);
        }
    }

    //数据库对象
    MyDBHelper mydatabasehelper;
    SQLiteDatabase mydatabase;

    //Context对象 模块名
    Context context;
    String ModuleName;

    //单词数量 单词数组 中文含义数组
    int count;
    String[] WordSpellingArray;
    String[] WordPhoneticSymbolArray;
    String[] WordChineseMeanArray;

    //构造函数
    public ModuleWordsReclerViewAdapt(Context context,String ModuleName){
        this.context = context;
        this.ModuleName = ModuleName;
        mydatabasehelper=new MyDBHelper(context);
        mydatabase=mydatabasehelper.getWritableDatabase();
        initData();
        Log.i(TAG,"count="+count);
    }

    private void initData(){
        Cursor cursor = mydatabase.rawQuery("select WordSpelling,WordPhoneticSymbol,WordChineseMean from " + MyDBHelper.Table_Words_NANE + " where WordOfModule=?", new String[]{ModuleName},null);
        count = cursor.getCount();
        if(count > 0 ){
            int i =0;
            WordSpellingArray = new String[count];
            WordPhoneticSymbolArray = new String[count];
            WordChineseMeanArray = new String[count];
            while(cursor.moveToNext()){
                WordSpellingArray[i]=cursor.getString(0);
                WordPhoneticSymbolArray[i]=cursor.getString(1);
                WordChineseMeanArray[i]=cursor.getString(2);
                i++;
            }
            cursor.close();
//            mydatabase.close();
//            mydatabasehelper.close();
        }
    }

    @Override
    public ModuleWordsReclerViewAdapt.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.modulewords_recyclerview_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ModuleWordsReclerViewAdapt.ViewHolder holder, final int position) {
        holder.WordSpelling.setText(WordSpellingArray[position]);
        holder.WordChineseMean.setText(WordChineseMeanArray[position]);
        //判断单词是否在生词本中（通过数据库查询）
        Cursor cursor_newWordsBook = mydatabase.rawQuery("select * from "+MyDBHelper.Table_NewWordsBook_NAME+" where NewWordSpelling=?",new String[]{WordSpellingArray[position]});
        if(cursor_newWordsBook.getCount() == 0){
            //不在
            holder.AddToNewWords.setEnabled(true);
            holder.AddToNewWords.setBackgroundResource(R.drawable.addtonewwords);
        }else if(cursor_newWordsBook.getCount() > 0){
            //在
            holder.AddToNewWords.setEnabled(false);
            holder.AddToNewWords.setBackgroundResource(R.drawable.hadaddtonewwords);
        }
        holder.AddToNewWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.AddToNewWords.setEnabled(false);
                holder.AddToNewWords.setBackgroundResource(R.drawable.hadaddtonewwords);
                //加入数据库
                Cursor cursor_newwords = mydatabase.rawQuery("select * from " + MyDBHelper.Table_NewWordsBook_NAME, null);
                int count_newwords = cursor_newwords.getCount();
                if(count_newwords >= 0){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("NewWordID",count_newwords+1);
                    contentValues.put("NewWordSpelling",WordSpellingArray[position]);
                    contentValues.put("NewWordPhoneticSymbol",WordPhoneticSymbolArray[position]);
                    contentValues.put("NewWordChineseMean",WordChineseMeanArray[position]);
                    mydatabase.insert(MyDBHelper.Table_NewWordsBook_NAME, null, contentValues);
                }
                cursor_newwords.close();
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }
}
