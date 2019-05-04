package com.example.lying.myword;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewWordsBookReclerViewAdapt extends RecyclerView.Adapter<NewWordsBookReclerViewAdapt.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        //控件 (读音图标、选择图标)
        TextView itemNewWordSound,itemNewWordIsSelect;
        //控件 (单词、音标、中文含义)
        TextView itemNewWordWord,itemNewWordPhoneticSymbol,itemNewWordChineseMean;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNewWordSound = (TextView)itemView.findViewById(R.id.itemNewWordSound);
            itemNewWordIsSelect = (TextView)itemView.findViewById(R.id.itemNewWordIsSelect);
            itemNewWordWord = (TextView)itemView.findViewById(R.id.itemNewWordWord);
            itemNewWordPhoneticSymbol = (TextView)itemView.findViewById(R.id.itemNewWordPhoneticSymbol);
            itemNewWordChineseMean = (TextView)itemView.findViewById(R.id.itemNewWordChineseMean);
        }
    }

    //Context对象
    Context context;
    //数据库对象
    MyDBHelper mydatabasehelper;
    SQLiteDatabase mydatabase;
    //定义tts
    TextToSpeech speak = null;

    //数据源
    List<Word> wordData = new ArrayList<Word>();
    //判断显示的是哪个图片，默认读音图标（0表示读音图标   1表示选择图标）
    int whichPicture = 0;
    //判断选择图标是否选中，默认未选中 (0表示未选中   1表示选中)
    int isSelect = 0;

    //构造函数
    public NewWordsBookReclerViewAdapt(Context context,TextToSpeech speak){
        this.context = context;
        mydatabasehelper=new MyDBHelper(context);
        mydatabase=mydatabasehelper.getWritableDatabase();
        this.speak = speak;
        initData();
    }

    //删除某些数据
    public void deleteData(int[] whichNeedDelete){
        if(null != whichNeedDelete && whichNeedDelete.length > 0 && !wordData.isEmpty()){
            for(int i=0;i<whichNeedDelete.length;i++){
                int position = whichNeedDelete[i]-i;
                wordData.remove(position);
            }
//            wordData.notifyAll();
        }
        //更新数据库
        if(mydatabasehelper == null)
            mydatabasehelper = new MyDBHelper(context);
        if(!mydatabase.isOpen())
            mydatabase = mydatabasehelper.getWritableDatabase();
        //先清空数据库
        mydatabase.delete(MyDBHelper.Table_NewWordsBook_NAME,null,null);
        //再根据数据源更新数据库
        Cursor cursor_newwords = mydatabase.rawQuery("select * from " + MyDBHelper.Table_NewWordsBook_NAME, null);
        int count_newwords = cursor_newwords.getCount();
        if(count_newwords >= 0 && !wordData.isEmpty()){
            for(int i =0;i<wordData.size();i++){
                Word word = wordData.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("NewWordID",i+1);
                contentValues.put("NewWordSpelling",word.getWordSpell());
                contentValues.put("NewWordPhoneticSymbol",word.getPhoneticSymbol());
                contentValues.put("NewWordChineseMean",word.getChineseMean());
                mydatabase.insert(MyDBHelper.Table_NewWordsBook_NAME, null, contentValues);
            }
        }
        cursor_newwords.close();
        mydatabase.close();
        mydatabasehelper.close();
    }

    //获取选中的项的位置
    public int[] getSelectItemPosition(){
        int selectNum = 0;
        if(!wordData.isEmpty()){
            for(int i=0;i<wordData.size();i++){
                Word word = wordData.get(i);
                if(word.getIsItemSelect() == 1){
                    selectNum++;
                }
            }
            int[] position;
            int j =0;
            if(selectNum > 0){
                position = new int[selectNum];
                for(int i=0;i<wordData.size();i++){
                    Word word = wordData.get(i);
                    if(word.getIsItemSelect() == 1){
                        position[j] = i;
                        j++;
                    }
                }
                return position;
            }
        }
        return null;
    }

    //初始化数据
    private void initData(){
        if(mydatabasehelper == null)
            mydatabasehelper = new MyDBHelper(context);
        if(!mydatabase.isOpen())
            mydatabase = mydatabasehelper.getWritableDatabase();
        //查询数据库
        Cursor cursor = mydatabase.rawQuery("select * from " + MyDBHelper.Table_NewWordsBook_NAME ,null);
        int count = cursor.getCount();
        if(count > 0){
            while (cursor.moveToNext()){
                String spelling = cursor.getString(1);
                String phoneticSymbol = cursor.getString(2);
                String chineseMean = cursor.getString(3);
                Word word = new Word(spelling,phoneticSymbol,chineseMean);
                wordData.add(word);
            }
        }
        cursor.close();
        mydatabase.close();
        mydatabasehelper.close();
    }

    //设置数据源
    public void setWordData(List<Word> wordData){
        this.wordData = wordData;
    }
    //提供外部访问数据源
    public List getWordData(){
        return wordData;
    }
    public void setWhichPicture(int whichPicture){
        this.whichPicture = whichPicture;
    }
    public void setIsSelect(int isSelect){
        this.isSelect = isSelect;
    }

    @Override
    public NewWordsBookReclerViewAdapt.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.newwordsbook_recyclerview_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewWordsBookReclerViewAdapt.ViewHolder holder, final int position) {
        Word word = wordData.get(position);

        //whichPicture = 0显示读音图标
        if(whichPicture == 0){
            holder.itemNewWordSound.setVisibility(View.VISIBLE);
            holder.itemNewWordIsSelect.setVisibility(View.GONE);
            wordData.get(position).setIsItemSelect(-1);
        }
        //whichPicture = 1显示选择图标
        if(whichPicture == 1){
            holder.itemNewWordSound.setVisibility(View.GONE);
            holder.itemNewWordIsSelect.setVisibility(View.VISIBLE);
            //判断显示那张图片
            if(isSelect == 0){//显示未选中
                holder.itemNewWordIsSelect.setBackgroundResource(R.drawable.unselect);
                wordData.get(position).setIsItemSelect(0);
            }
            if(isSelect == 1){//显示选中
                holder.itemNewWordIsSelect.setBackgroundResource(R.drawable.select);
                wordData.get(position).setIsItemSelect(1);
            }
        }

        holder.itemNewWordWord.setText(word.getWordSpell());
        holder.itemNewWordPhoneticSymbol.setText(word.getPhoneticSymbol());
        holder.itemNewWordChineseMean.setText(word.getChineseMean());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wordData.get(position).getIsItemSelect() == 0){  //显示未选中图标时
                    holder.itemNewWordIsSelect.setBackgroundResource(R.drawable.select);
                    wordData.get(position).setIsItemSelect(1);
                }else if(wordData.get(position).getIsItemSelect() == 1){ //显示选中图标时
                    holder.itemNewWordIsSelect.setBackgroundResource(R.drawable.unselect);
                    wordData.get(position).setIsItemSelect(0);
                }else if(wordData.get(position).getIsItemSelect() == -1){ //初始状态(显示读音图标)
                    //弹出学习卡片(PopWindow)
                    WordPresentationPop wordPresentationPop = new WordPresentationPop(context,
                            wordData.get(position).getWordSpell(),wordData.get(position).getPhoneticSymbol(),
                            wordData.get(position).getChineseMean(),speak);
                    wordPresentationPop.show((Activity) context);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
