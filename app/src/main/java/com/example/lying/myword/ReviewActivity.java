package com.example.lying.myword;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lying.myword.util.CreateModuleTxtUtil;
import com.example.lying.myword.util.DisturbOptionsUtil;
import com.example.lying.myword.util.File_read_write;
import com.example.lying.myword.util.countTimeUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener{

    //传过来的数据
    String ModuleName = "";

    //文件的最大行号
    int maxPosition = -1;

    //当前指向排好序的List位置
    int position = -1;

    //随机数的范围（最小为1，对应A选项。最大为4，对应D选项）随机函数
    int min = 1;
    int max = 3;
    Random random = new Random();

    //当前单词所在选项
    int num = -1;

    //数据库对象
    private MyDBHelper mydatabasehelper;
    private SQLiteDatabase mydatabase;

    //存储数据的链表
    List<Word> ReviewWordData = new ArrayList<Word>();

    //需要显示的数据
    String TitelText = "还没有单词需要复习哦！";
    String wordText = "";
    String phoneticSymbolText = "";
    String chineseMeanText = "";
    String AText = "";
    String BText = "";
    String CText = "";
    String DText = "";

    //控件(标题、单词、音标)
    TextView ReviewTitle , ReviewWord , ReviewPhoneticSymbol;
    //控件(A选项、B选项、C选项、D选项)
    TextView AOption,BOption,COption,DOption;
    //控件(上一个、下一个)
    TextView ReviewPrevious,ReviewNext;

    //获取两个多余选项的对象
    DisturbOptionsUtil disturbOptionsUtil ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //绑定控件
        ReviewTitle = (TextView)findViewById(R.id.ReviewTitle);//标题
        ReviewWord = (TextView)findViewById(R.id.ReviewWord);//单词
        ReviewPhoneticSymbol = (TextView)findViewById(R.id.ReviewPhoneticSymbol);//音标
        AOption = (TextView)findViewById(R.id.AOption);//A选项
        BOption = (TextView)findViewById(R.id.BOption);//B选项
        COption = (TextView)findViewById(R.id.COption);//C选项
        DOption = (TextView)findViewById(R.id.DOption);//D选项
        ReviewPrevious = (TextView)findViewById(R.id.ReviewPrevious);//上一个
        ReviewNext = (TextView)findViewById(R.id.ReviewNext);//下一个

        //初始化数据库对象
        mydatabasehelper=new MyDBHelper(this);
        mydatabase=mydatabasehelper.getWritableDatabase();

        //获取干扰选项对象
        disturbOptionsUtil = new DisturbOptionsUtil(this);

        //传过来的数据(有数据肯定有表)
        ModuleName = getIntent().getStringExtra("ModuleName");
//        Log.i("ReviewActivity",ModuleName);

        initData();
        setData();

        //响应事件
        ReviewNext.setOnClickListener(this);//下一个
        ReviewPrevious.setOnClickListener(this);//上一个
        AOption.setOnClickListener(this);
        BOption.setOnClickListener(this);
        COption.setOnClickListener(this);
        DOption.setOnClickListener(this);
    }

    //初始化显示的数据
    private void initData(){
        if(null != ModuleName && !"".equals(ModuleName)){
            //读取文件辅助对象
            File_read_write file_read_write = new File_read_write(this,"review"+ModuleName+".txt");
            try {
                //若文件存在则获取 总行数
                maxPosition = file_read_write.getTotalLines();
                //修改学习表中的复习标志位为正在复习（0）
                Cursor cursor_study = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningModule=?",new String[]{ModuleName});
                if(cursor_study.getCount() == 1){
                    ContentValues contentValues_study = new ContentValues();
                    contentValues_study.put("ReviewingState",0);
                    mydatabase.update(MyDBHelper.Table_LearningPlan_NANE,contentValues_study,"LearningModule=?",new String[]{ModuleName});
                }
                cursor_study.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(maxPosition > 0){//若文件中有数据则初始化
            TitelText = "'"+ModuleName+"'"+"模块复习";
            //读取文件辅助对象
            File_read_write file_read_write = new File_read_write(this,"review"+ModuleName+".txt");
            if(ReviewWordData != null && ReviewWordData.size() > 0){
                ReviewWordData.clear();
            }
            //根据文件生成List
            for(int i = 0;i<maxPosition;i++){
                String content = file_read_write.getDataFromLineNum(i+1);
                if(null != content && content.contains("*") && content.split("\\*").length == 6){
                    ReviewWordData.add(getWordFromString(content));
                }
            }
            //根据每个单词的学习时间排序List(时间晚的在前)
            Collections.sort(ReviewWordData, new Comparator<Word>() {
                @Override
                public int compare(Word word, Word t1) {
                    return -countTimeUtil.CompareTime(word.getStudyTime(),t1.getStudyTime());
                }
            });
            Log.i("ReviewActivity",ReviewWordData.toString());
            if(position == -1){
                position = 0;
            }
            //position小于maxPosition时
            if(position < maxPosition && !ReviewWordData.get(position).getNeedReviewTime().equals("0")){
                wordText = ReviewWordData.get(position).getWordSpell();
                phoneticSymbolText = ReviewWordData.get(position).getPhoneticSymbol();
                chineseMeanText = ReviewWordData.get(position).getChineseMean();
                num = ReviewWordData.get(position).getOption();
            }
        }
    }
    //刷新数据
    private void flashData(){
        //position小于maxPosition时
        if(position < maxPosition && position >= 0 && !ReviewWordData.get(position).getNeedReviewTime().equals("0")){
            wordText = ReviewWordData.get(position).getWordSpell();
            phoneticSymbolText = ReviewWordData.get(position).getPhoneticSymbol();
            chineseMeanText = ReviewWordData.get(position).getChineseMean();
            num = ReviewWordData.get(position).getOption();
        }
    }
    //显示数据
    private void setData(){
        OriginalOptionStyle();
        ReviewTitle.setText(TitelText);
        ReviewWord.setText(wordText);
        ReviewPhoneticSymbol.setText(phoneticSymbolText);
        if(num == 1){
            AOption.setText(chineseMeanText);
            BOption.setText(disturbOptionsUtil.getDisturbOption(chineseMeanText)[0]);
            COption.setText(disturbOptionsUtil.getDisturbOption(chineseMeanText)[1]);
        }else if(num == 2){
            BOption.setText(chineseMeanText);
            AOption.setText(disturbOptionsUtil.getDisturbOption(chineseMeanText)[0]);
            COption.setText(disturbOptionsUtil.getDisturbOption(chineseMeanText)[1]);
        }else if(num == 3){
            COption.setText(chineseMeanText);
            AOption.setText(disturbOptionsUtil.getDisturbOption(chineseMeanText)[0]);
            BOption.setText(disturbOptionsUtil.getDisturbOption(chineseMeanText)[1]);
        }
    }

    //根据String字符串生成Word对象
    private Word getWordFromString(String wordStr){
        String word1 = wordStr.split("\\*")[0].split("-")[0];//单词在学习txt文件中的行号
        String word11 = wordStr.split("\\*")[0].split("-")[1];//单词在复习txt文件中的行号
        String word2 = wordStr.split("\\*")[1];//单词拼写
        String word3 = wordStr.split("\\*")[2];//单词中文含义
        String word4 = wordStr.split("\\*")[3];//单词音标
        String word5 = wordStr.split("\\*")[4];//单词需要复习的次数
        String word6 = wordStr.split("\\*")[5];//单词学习的时间
        Word word = new Word(word1,word11,word2,word3,word4,word5,word6);
        if(word.getOption() == -1){
            int option = random.nextInt(max)%(max-min+1) + min;
            word.setOption(option);
        }
        return word;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ReviewNext://下一个
                if(position < maxPosition-1){
                    //保存当前位置，当指针在while循环中变得比maxPosition-1大时回滚
                    int reviewPosition = position;
                    position++;
                    //跳过需复习次数为0的数据
                    while(ReviewWordData.get(position).getNeedReviewTime().equals("0")){
                        position++;
                        //若跳过需复习次数为0的数据后，指针大于或等于maxPosition需要跳出，因为ReviewWordData.get(position)中的position超出范围，抛出异常
                        if(position >= maxPosition){
                            break;
                        }
                    }
                    //若position没超出范围时退出循环，则表示后面有数据，正常运行。
                    if(position <= maxPosition-1){
                        flashData();
                        setData();
                    }else if(position > maxPosition-1){//若超出范围退出，两种可能。1：复习完成(从第一个需复习次数为0就开始跳过，且后面都为0)。2：前面不为0后面都为0
                        //要判断ReviewWordData中的数据是否又要复习的
                        int hasNeedReviewNum = 0;
                        for(int i = 0;i<ReviewWordData.size();i++){
                            if(!ReviewWordData.get(i).getNeedReviewTime().equals("0")){
                                hasNeedReviewNum++;
                            }
                        }
                        //若有则复习
                        if(hasNeedReviewNum > 0){
                            position = -1;
                            initData();
                            setData();
                        }else {//没有则表示当前模块复习完成
                            //更新学习txt文件
                            upStudyTxt();
                            //先把学习表的标志为改变（若学习状态为1，学习完成的，则把复习状态置为1，完成复习。若学习状态为0，则不做任何操作）
                            Cursor cursor_study = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningModule=?",new String[]{ModuleName});
                            if(cursor_study.getCount() == 1){
                                while(cursor_study.moveToNext()){
                                    //判断学习状态,若学习完成，则改变
                                    if(cursor_study.getInt(2) == 1){
                                        ContentValues contentValues_study = new ContentValues();
                                        contentValues_study.put("ReviewingState",1);
                                        mydatabase.update(MyDBHelper.Table_LearningPlan_NANE,contentValues_study,"LearningModule=?",new String[]{ModuleName});
                                    }
                                }
                            }
                            cursor_study.close();
                            //获取下一个模块名
                            String nextModule = NextReviewMoudle();
                            //若不为空则修改当前模块名为 获取的下一个模块名
                            if(null != nextModule && !nextModule.equals("0")){
                                ModuleName = nextModule;
                                initData();
                                setData();
                            }else{
                                //所有已学习的单词都复习完了
                                new AlertDialog.Builder(this)
                                        .setTitle("提示").setMessage("所有已学单词都复习完啦！")
                                        .setPositiveButton("确定", null)
                                        .show();
                            }
                        }
                    }
                }else if(position == maxPosition-1){
                    //要判断ReviewWordData中的数据是否有要复习的
                    int hasNeedReviewNum = 0;
                    for(int i = 0;i<ReviewWordData.size();i++){
                        if(!ReviewWordData.get(i).getNeedReviewTime().equals("0")){
                            hasNeedReviewNum++;
                        }
                    }
                    //若有则复习
                    if(hasNeedReviewNum > 0){
                        position = -1;
                        initData();
                        setData();
                    }else {//没有则表示当前模块复习完成
                        //更新学习txt文件
                        upStudyTxt();
                        //先把学习表的标志为改变（若学习状态为1，学习完成的，则把复习状态置为1，完成复习。若学习状态为0，则不做任何操作）
                        Cursor cursor_study = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningModule=?",new String[]{ModuleName});
                        if(cursor_study.getCount() == 1){
                            while(cursor_study.moveToNext()){
                                //判断学习状态,若学习完成，则改变
                                if(cursor_study.getInt(2) == 1){
                                    ContentValues contentValues_study = new ContentValues();
                                    contentValues_study.put("ReviewingState",1);
                                    mydatabase.update(MyDBHelper.Table_LearningPlan_NANE,contentValues_study,"LearningModule=?",new String[]{ModuleName});
                                }
                            }
                        }
                        cursor_study.close();
                        //获取下一个模块名
                        String nextModule = NextReviewMoudle();
                        //若不为空则修改当前模块名为 获取的下一个模块名
                        if(null != nextModule && !nextModule.equals("0")){
                            ModuleName = nextModule;
                            initData();
                            setData();
                        }else{
                            //所有已学习的单词都复习完了
                            new AlertDialog.Builder(this)
                                    .setTitle("提示").setMessage("所有已学单词都复习完啦！")
                                    .setPositiveButton("确定", null)
                                    .show();
                        }
                    }
                }
                break;
            case R.id.ReviewPrevious://上一个
                if(position > 0){
                    position--;
                    flashData();
                    setData();
                }
                break;
            case R.id.AOption://A选项
                changeOptionStyle();
                //若A是正确答案
                if(num == 1){
                    //原需复习次数
                    String needTime = ReviewWordData.get(position).getNeedReviewTime();
                    int time = Integer.valueOf(needTime).intValue();
                    time = time - 1;
                    //修改需要学习次数数据
                    ReviewWordData.get(position).setNeedReviewTime(String.valueOf(time));
                    //修改学习日期
                    ReviewWordData.get(position).setStudyTime(countTimeUtil.getCurrentTime());
                    flashReviewTxt(ReviewWordData);
                }
                break;
            case R.id.BOption://B选项
                changeOptionStyle();
                if(num == 2){
                    //原需复习次数
                    String needTime = ReviewWordData.get(position).getNeedReviewTime();
                    int time = Integer.valueOf(needTime).intValue();
                    time = time - 1;
                    //修改需要学习次数数据
                    ReviewWordData.get(position).setNeedReviewTime(String.valueOf(time));
                    //修改学习日期
                    ReviewWordData.get(position).setStudyTime(countTimeUtil.getCurrentTime());
                    flashReviewTxt(ReviewWordData);
                }
                break;
            case R.id.COption://C选项
                changeOptionStyle();
                if(num == 3){
                    //原需复习次数
                    String needTime = ReviewWordData.get(position).getNeedReviewTime();
                    int time = Integer.valueOf(needTime).intValue();
                    time = time - 1;
                    //修改需要学习次数数据
                    ReviewWordData.get(position).setNeedReviewTime(String.valueOf(time));
                    //修改学习日期
                    ReviewWordData.get(position).setStudyTime(countTimeUtil.getCurrentTime());
                    flashReviewTxt(ReviewWordData);
                }
                break;
            case R.id.DOption://D选项
                changeOptionStyle();
                break;
        }
    }
    //选项点击后
    private void changeOptionStyle(){
        if(AOption.getText().equals(chineseMeanText)){
            AOption.setBackgroundColor(Color.argb(183,128,250,160));
            BOption.setBackgroundColor(Color.argb(183,250,128,164));
            COption.setBackgroundColor(Color.argb(183,250,128,164));
            DOption.setBackgroundColor(Color.argb(183,250,128,164));
        }else if(BOption.getText().equals(chineseMeanText)){
            BOption.setBackgroundColor(Color.argb(183,128,250,160));
            AOption.setBackgroundColor(Color.argb(183,250,128,164));
            COption.setBackgroundColor(Color.argb(183,250,128,164));
            DOption.setBackgroundColor(Color.argb(183,250,128,164));
        }else if(COption.getText().equals(chineseMeanText)){
            COption.setBackgroundColor(Color.argb(183,128,250,160));
            AOption.setBackgroundColor(Color.argb(183,250,128,164));
            BOption.setBackgroundColor(Color.argb(183,250,128,164));
            DOption.setBackgroundColor(Color.argb(183,250,128,164));
        }
        AOption.setEnabled(false);
        BOption.setEnabled(false);
        COption.setEnabled(false);
        DOption.setEnabled(false);
    }
    //选项点击前
    private void OriginalOptionStyle(){
        AOption.setText("A选项");
        BOption.setText("B选项");
        COption.setText("C选项");
        AOption.setBackgroundColor(Color.argb(183,246,213,246));
        BOption.setBackgroundColor(Color.argb(183,246,213,246));
        COption.setBackgroundColor(Color.argb(183,246,213,246));
        DOption.setBackgroundColor(Color.argb(183,246,213,246));
        AOption.setEnabled(true);
        BOption.setEnabled(true);
        COption.setEnabled(true);
        DOption.setEnabled(true);
    }
    //把List中的数据更新到txt表中
    private void flashReviewTxt(List<Word> data){
        File_read_write file_read_write = new File_read_write(this,"review"+ModuleName+".txt");
        if(null != data && !data.isEmpty()){
            for(int i = 0;i<data.size();i++){
                Word word = data.get(i);
                int reviewPosition = Integer.valueOf(word.getWordReviewPosition()).intValue();
                file_read_write.setReviewTxtFromLineNum(reviewPosition,word.getNeedReviewTime(),word.getStudyTime());
            }
        }
    }

    //更新学习txt
    private void upStudyTxt(){
        File_read_write file_read_write_review = new File_read_write(this,"review"+ModuleName+".txt");
        File_read_write file_read_write_study = new File_read_write(this,ModuleName+".txt");
        for(int i = 1;i<= maxPosition;i++){
            String strLine = file_read_write_review.getDataFromLineNum(i);
            //更新学习表中的哪一行
            if(null != strLine && strLine.contains("*") && strLine.split("\\*").length == 6){
                int needUpLineNum = Integer.valueOf(strLine.split("\\*")[0].split("-")[0]).intValue();
                file_read_write_study.setDataFromLineNum(needUpLineNum,strLine.split("\\*")[4],strLine.split("\\*")[5],1);
            }
        }
    }

    //若当前模块学习完成，则查找下一个模块
    private String NextReviewMoudle(){
        //查询学习计划表,获取当前需要复习的模块名
        String NextReviewModuleName = "";
        //先查询 学习状态为0(正在学习)且复习状态为0(正在复习)的模块
        Cursor cursor_studytable00 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                " where LearningState=? and ReviewingState=? limit 1",new String[]{"0","0"});
        if(cursor_studytable00.getCount() == 1){
            while(cursor_studytable00.moveToNext()){
                NextReviewModuleName = cursor_studytable00.getString(0);
            }
            //先删除文件（查找到了就删除）
            deleteFile(NextReviewModuleName,2);
            //重新创建文件
            CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(this);
            createModuleTxtUtil.createReviewTxtOfModuleName(NextReviewModuleName);
        }
        cursor_studytable00.close();
        //再查询 学习状态为0(正在学习)且复习状态为-1(未复习)的模块
        if(null == NextReviewModuleName || NextReviewModuleName.equals("") || !isFileExist(NextReviewModuleName,2)){
            Cursor cursor_studytable0_1 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                    " where LearningState=? and ReviewingState=? limit 1",new String[]{"0","-1"});
            if(cursor_studytable0_1.getCount() == 1){
                while(cursor_studytable0_1.moveToNext()){
                    NextReviewModuleName = cursor_studytable0_1.getString(0);
                }
                //先删除文件（查找到了就删除）
                deleteFile(NextReviewModuleName,2);
                //重新创建文件
                CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(this);
                createModuleTxtUtil.createReviewTxtOfModuleName(NextReviewModuleName);
            }
            cursor_studytable0_1.close();
        }
        //再查询 学习状态为1(完成学习)且复习状态为0(正在复习)的模块
        if(null == NextReviewModuleName || NextReviewModuleName.equals("") || !isFileExist(NextReviewModuleName,2)){
            Cursor cursor_studytable10 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                    " where LearningState=? and ReviewingState=? limit 1",new String[]{"1","0"});
            if(cursor_studytable10.getCount() == 1){
                while(cursor_studytable10.moveToNext()){
                    NextReviewModuleName = cursor_studytable10.getString(0);
                }
                //先删除文件（查找到了就删除）
                deleteFile(NextReviewModuleName,2);
                //重新创建文件
                CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(this);
                createModuleTxtUtil.createReviewTxtOfModuleName(NextReviewModuleName);
            }
            cursor_studytable10.close();
        }
        //最后查询 学习状态为1(完成学习)且复习状态为-1(未复习)的模块
        if(null == NextReviewModuleName || NextReviewModuleName.equals("") || !isFileExist(NextReviewModuleName,2)){
            Cursor cursor_studytable1_1 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                    " where LearningState=? and ReviewingState=? limit 1",new String[]{"1","-1"});
            if(cursor_studytable1_1.getCount() == 1){
                while(cursor_studytable1_1.moveToNext()){
                    NextReviewModuleName = cursor_studytable1_1.getString(0);
                }
                //先删除文件（查找到了就删除）
                deleteFile(NextReviewModuleName,2);
                //重新创建文件
                CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(this);
                createModuleTxtUtil.createReviewTxtOfModuleName(NextReviewModuleName);
            }
            cursor_studytable1_1.close();
        }
        //传递要复习的模块
        //有文件的时候才传递
        if(isFileExist(NextReviewModuleName,2)){
            return NextReviewModuleName;
        }
        return null;
    }

    //根据文件名判断 data目录 下 files目录 中 文件 是否存在
    private boolean isFileExist(String ModuleName,int Type){//Type = 1 表示创建学习txt  Type = 2 表示创建复习txt
        String FileName = "";
        if(Type == 1){
            FileName = ModuleName + ".txt";
        }else if(Type == 2){
            FileName = "review"+ModuleName + ".txt";
        }
        //file目录
        File directory = this.getFilesDir();
        if ("" != ModuleName && null != ModuleName && directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if(FileName.equals(item.getName())){
                    return true;
                }
            }
        }
        return false;
    }
    //根据文件名(若文件存在)删除 data目录 下 files目录 中 文件
    private void deleteFile(String ModuleName,int Type){//Type = 1 表示创建学习txt  Type = 2 表示创建复习txt
        String FileName = "";
        if(Type == 1){
            FileName = ModuleName + ".txt";
        }else if(Type == 2){
            FileName = "review"+ModuleName + ".txt";
        }
        //file目录
        File directory = this.getFilesDir();
        if ("" != ModuleName && null != ModuleName && directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if(FileName.equals(item.getName())){
                    item.delete();
                    return;
                }
            }
        }
    }

    //在onStop里由reviewTxt更新学习txt
    @Override
    protected void onPause(){
        super.onPause();
        upStudyTxt();
    }
}
