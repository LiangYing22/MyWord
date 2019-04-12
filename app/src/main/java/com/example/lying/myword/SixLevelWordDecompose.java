package com.example.lying.myword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class SixLevelWordDecompose {
    //全局变量，用于分开逻辑
    int i=1;//用于判断第一行
    int j=0;//用于计算单词数量
    //用于显示时间
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //获取当前时间
    Date date = new Date(System.currentTimeMillis());
    String thedate=simpleDateFormat.format(date);
    //
    private static SixLevelWordDecompose sixLevelWordDecompose=null;//用于单例模式
    private static Context context;
    private String bookname="";
    //数据库
    MyDBHelper mydatabasehelper;
    SQLiteDatabase mydatabase;
    //成员方法
    public static SixLevelWordDecompose getSixLevelWordDecompose(Context context){//单例模式的工厂方法
        if(sixLevelWordDecompose==null){
            return new SixLevelWordDecompose(context);
        }else{
            return sixLevelWordDecompose;
        }
    }
    private SixLevelWordDecompose(){}//私有的构造函数
    private SixLevelWordDecompose(Context context){//私有的构造函数
        this.context=context;
        mydatabasehelper=new MyDBHelper(context);
        mydatabase=mydatabasehelper.getWritableDatabase();
    }

    /**
     *
     * @param TxtFileName(文件名，根据此文件名导入)
     * @throws IOException
     */
    public void WordsBookToDBFile(String TxtFileName,String WordsBookName,int ModuleType) throws IOException {//用于把文本文档“英语词汇”导入数据库
        // 要根据ModulType设置模块类型TODO

        //先清空数据库
        int DeleteDataNum = emptyDB();
        Log.i("SixLevelWordDecompose","删除的数据数量"+DeleteDataNum);

        //词库名
        bookname=WordsBookName;

        InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open(TxtFileName),"Unicode" );
        BufferedReader bufReader = new BufferedReader(inputReader);
        String line="";//用于暂存读取的每行数据
        String Result[]=null;//用于保存每行拆分数据

        //减少导入时间——开始事务
        mydatabase.beginTransaction();

        Cursor cursor_table_words = mydatabase.rawQuery("select * from " + MyDBHelper.Table_Words_NANE, null);//查询导入单词前单词表中的数据
        int count_table_words=cursor_table_words.getCount();//单词表中的单词个数
        //循环导入数据库
        while ((line = bufReader.readLine()) != null) {
            if(!"常用2000单词.txt".equals(TxtFileName) && line.contains("/")){
                Result=line.split("/");
                //Log.i("词本信息",Result[0] + " , " + Result[1] + " , " + Result[2]);//此处可导入数据库
                ContentValues contentValues = new ContentValues();
                contentValues.put("WordID",count_table_words+1);
                contentValues.put("WordSpelling",Result[0].replace((char) 12288, ' ').trim());//先将全角空格替换成半角空格再调用trim()方法。trim()只能去半角空格
                contentValues.put("WordChineseMean",Result[2].trim());
                contentValues.put("WordPhoneticSymbol","/"+Result[1].trim()+"/");
//                contentValues.put("WordOfModule",Result[0].replace((char) 12288, ' ').trim().substring(0,1).toUpperCase());
                //设置相应的模块类型
                setModuleType(ModuleType,contentValues,Result[0].replace((char) 12288, ' ').trim().substring(0,1).toUpperCase());
                mydatabase.insert(MyDBHelper.Table_Words_NANE, null, contentValues);
                count_table_words++;
                j++;
            }
            if(line.contains("[")&&line.contains("]")){
                Result=new String[3];
                Result[0]=line.split("\\[",2)[0];
                Result[1]=line.split("\\[",2)[1].split("\\]",2)[0];
                Result[2]=line.split("\\[",2)[1].split("\\]",2)[1];
                //Log.i("词本信息",Result[0] + " , " + Result[1] + " , " + Result[2]);//此处可导入数据库
                ContentValues contentValues = new ContentValues();
                contentValues.put("WordID",count_table_words+1);
                contentValues.put("WordSpelling",Result[0].replace((char) 12288, ' ').trim());
                contentValues.put("WordChineseMean",Result[2].trim());
                contentValues.put("WordPhoneticSymbol","/"+Result[1].trim()+"/");
//                contentValues.put("WordOfModule",Result[0].replace((char) 12288, ' ').trim().substring(0,1).toUpperCase());
                //设置相应的模块类型
                setModuleType(ModuleType,contentValues,Result[0].replace((char) 12288, ' ').trim().substring(0,1).toUpperCase());
                mydatabase.insert(MyDBHelper.Table_Words_NANE, null, contentValues);
                count_table_words++;
                j++;
            }
            i++;
        }
        cursor_table_words.close();
        String[] module = DataToWordsBook();//把数据导入词库表
        PerfectTable(module);//完善表中数据
        //减少导入时间——标志事务成功
        mydatabase.setTransactionSuccessful();
        //减少导入时间——若事务成功则提交
        mydatabase.endTransaction();
        mydatabase.close();
        mydatabasehelper.close();
        bufReader.close();
        inputReader.close();

    }
    //把数据导入词库表
    private String[] DataToWordsBook(){
        Cursor cursor_for_module_num = mydatabase.rawQuery("select distinct WordOfModule from " + MyDBHelper.Table_Words_NANE, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("WordsBookID",1);
        contentValues.put("WordsBookName",bookname);
        contentValues.put("AddTime",thedate);
        contentValues.put("WordsNumber",j);
        contentValues.put("ModuleNumber",cursor_for_module_num.getCount());
        mydatabase.insert(MyDBHelper.Table_WordsBook_NANE, null, contentValues);
        String[] module=new String[cursor_for_module_num.getCount()];
        int count=0;
        while(cursor_for_module_num.moveToNext()){
            module[count]=cursor_for_module_num.getString(0);
            count++;
        }
        cursor_for_module_num.close();
        return module;
    }
    //完善表中数据
    private void PerfectTable(String[] module){
        //完善数据库表中的数据
        //完善单词数量、模块数量(移至DataToWordsBook方法)

        //查询模块表(开始为空表)，完善模块表（除模块单词数量）
        Cursor cursor_for_module = mydatabase.rawQuery("select * from " + MyDBHelper.Table_WordsModule_NANE, null);
        int count_for_module_number=cursor_for_module.getCount();//模块数量
        for(int k=0;k<module.length;k++){
            ContentValues contentValues_module = new ContentValues();//更新模块表
            contentValues_module.put("ModuleID",count_for_module_number+1);
            contentValues_module.put("ModuleName",module[k]);
            contentValues_module.put("ModuleWordsNumber",-1);
            contentValues_module.put("ModuleOfWordsBook",bookname);
            mydatabase.insert(MyDBHelper.Table_WordsModule_NANE, null, contentValues_module);
            count_for_module_number++;
        }
        cursor_for_module.close();
        //查询模块表，完善模块单词数量
        Cursor cursor_modulewordnum=mydatabase.rawQuery("SELECT count(*) WordOfModule,WordOfModule FROM "+MyDBHelper.Table_Words_NANE+" GROUP BY WordOfModule HAVING count(*) > 0",null);
        int k=0;
        while (cursor_modulewordnum.moveToNext()){
            ContentValues contentValues_module = new ContentValues();//更新模块表
            contentValues_module.put("ModuleWordsNumber",cursor_modulewordnum.getInt(0));
            mydatabase.update(MyDBHelper.Table_WordsModule_NANE,contentValues_module,"ModuleName=?",new String[]{cursor_modulewordnum.getString(1)});
            k++;
        }
        cursor_modulewordnum.close();

        //查询学习表(开始为空表)，完善学习表(根据方法参数module)
        Cursor cursor_for_study = mydatabase.rawQuery("select * from " + MyDBHelper.Table_LearningPlan_NANE, null);
        int count_for_study_num = cursor_for_study.getCount();//此时表中模块数量
        for(int p=0;p<module.length;p++){
            ContentValues contentValues_study = new ContentValues();//更新学习表
            contentValues_study.put("LearningPlanID",count_for_study_num+1);
            contentValues_study.put("LearningModule",module[p]);
            contentValues_study.put("LearningState",-1);
            contentValues_study.put("TxtLearningPosition",-1);
            contentValues_study.put("ReviewingState",-1);
            mydatabase.insert(MyDBHelper.Table_LearningPlan_NANE,null,contentValues_study);
            count_for_study_num++;
        }
        cursor_for_study.close();
    }

    //清空数据库
    private int emptyDB(){
        //清空数据库
        int WordsBookTableDataNum = mydatabase.delete(MyDBHelper.Table_WordsBook_NANE,null,null);
        int WordsTableDataNum = mydatabase.delete(MyDBHelper.Table_Words_NANE,null,null);
        int WordsModuleTableDataNum = mydatabase.delete(MyDBHelper.Table_WordsModule_NANE,null,null);
        int LearningPlanTableDataNum = mydatabase.delete(MyDBHelper.Table_LearningPlan_NANE,null,null);
//        int NewWordsTableDataNum = mydatabase.delete(MyDBHelper.Table_NewWordsBook_NAME,null,null);
        //清空模块txt文件
        deleteFilesByDirectory(context.getFilesDir());
        return WordsBookTableDataNum+WordsTableDataNum+WordsModuleTableDataNum+LearningPlanTableDataNum;
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     *
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    //根据参数设置模块类型
    private void setModuleType(int ModuleType,ContentValues contentValues,String Initials){
        //MouduleType=1时，设置为首字母相同
        //MouduleType=2时，设置为乱序
        if(ModuleType == 1){
            contentValues.put("WordOfModule",Initials);
        }else if(ModuleType == 2){
            contentValues.put("WordOfModule",randomModuleName());
        }
    }
    //随机生成模块名（最多20个模块）
    private String randomModuleName(){
        int min = 1;
        int max = 20;
        Random random = new Random();
        int num = random.nextInt(max)%(max-min+1) + min;
        String[] moduleName = {"蓝天","白云","绿草","青山","红桃","小溪","泉水","麦芽","流水","东风",
                "古钟","猫咪","碧水","大海","长江","故乡","水稻","花生","铃铛","风语"};
        if(num >= 1 && num <= 20){
            return moduleName[num-1];
        }
        return "乱序模块";
    }
}
