package com.example.lying.myword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StudySurveyOfSpecificReclerViewAdapt extends RecyclerView.Adapter<StudySurveyOfSpecificReclerViewAdapt.ViewHolder> {

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

    //数据源
    List<Word> WordData = new ArrayList<Word>();

    //构造函数
    public StudySurveyOfSpecificReclerViewAdapt(Context context){
        this.context = context;
        mydatabasehelper=new MyDBHelper(context);
        mydatabase=mydatabasehelper.getWritableDatabase();
    }

    public void setData(List<Word> wordData){
        if(wordData != null){
            this.WordData = wordData;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.modulewords_recyclerview_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.WordSpelling.setText(WordData.get(position).getWordSpell());
        holder.WordChineseMean.setText(WordData.get(position).getChineseMean());
        //判断单词是否在生词本中（通过数据库查询）
        Cursor cursor_newWordsBook = mydatabase.rawQuery("select * from "+MyDBHelper.Table_NewWordsBook_NAME+" where NewWordSpelling=?",new String[]{WordData.get(position).getWordSpell()});
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
                    contentValues.put("NewWordSpelling",WordData.get(position).getWordSpell());
                    contentValues.put("NewWordPhoneticSymbol",WordData.get(position).getPhoneticSymbol());
                    contentValues.put("NewWordChineseMean",WordData.get(position).getChineseMean());
                    mydatabase.insert(MyDBHelper.Table_NewWordsBook_NAME, null, contentValues);
                }
                cursor_newwords.close();
            }
        });
    }

    @Override
    public int getItemCount() {
        return WordData.size();
    }

}
