package com.example.lying.myword.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lying.myword.MyDBHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CreateModuleTxtUtil {

    private Context context;
    private MyDBHelper mydatabasehelper;
    private SQLiteDatabase mydatabase;

    //从数据库读取的数据
    private String[] WordSpellingArray;
    private String[] WordChineseMeanArray;
    private String[] WordPhoneticSymbol;

    public CreateModuleTxtUtil(Context context){
        this.context = context;
        mydatabasehelper=new MyDBHelper(context);
        mydatabase=mydatabasehelper.getWritableDatabase();
    }

    //根据模块名创建“模块txt”文件
    public void createTxtOfModuleName(String ModuleName){
        String FileName = ModuleName+".txt";
        initData(ModuleName);
        File_read_write file_read_write=new File_read_write(context,FileName);
        if(null != WordSpellingArray && null != WordChineseMeanArray &&
                null != WordPhoneticSymbol){
            for(int i = 0;i<WordSpellingArray.length;i++){
                String content = WordSpellingArray[i] + "*" + WordChineseMeanArray[i]
                        + "*" + WordPhoneticSymbol[i] + "*" + "-1" + "*"
                        + "-1" + "*" + "-1\n";
                file_read_write.write(content,Context.MODE_APPEND);
            }
        }
    }

    //根据模块名创建“review模块txt”文件
    public void createReviewTxtOfModuleName(String ModuleName){
        String FileName = "review"+ModuleName+".txt";
        File_read_write file_read_write=new File_read_write(context,FileName);
        try {
            FileInputStream inStream = context.openFileInput(ModuleName+".txt");
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            int i = 0;//行数(在学习txt文件中的行号)
            int j = 0;//行数(在复习txt文件中的行号)
            String Target ;
            while (null != (Target = bf.readLine())) {
                i++;
                if(Target.contains("*")){
                    int count = Target.split("\\*").length;
                    if(count == 6){
                        if(!Target.split("\\*")[4].equals("0") && !Target.split("\\*")[4].equals("-1")){
                            j++;
                            Target = i+"-"+j+"*"+Target.split("\\*")[0]+"*"+Target.split("\\*")[1]+"*"
                                    +Target.split("\\*")[2]+"*"+Target.split("\\*")[4]+"*"
                                    +Target.split("\\*")[5]+"\n";
                            file_read_write.write(Target,Context.MODE_APPEND);
                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //根据指针位置更新 学习表（更新学习状态和指针位置）,更新学习状态为学习中
    public void updateStudyTable(String ModuleName,int StudyPosition){
        if(null == mydatabasehelper) mydatabasehelper=new MyDBHelper(context);
        if(!mydatabase.isOpen()) mydatabase=mydatabasehelper.getWritableDatabase();
        Cursor cursor_study = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningModule=?",new String[]{ModuleName});
        if(cursor_study.getCount() == 1){
            ContentValues contentValues_study = new ContentValues();
            contentValues_study.put("LearningState",0);
            contentValues_study.put("TxtLearningPosition",StudyPosition);
            mydatabase.update(MyDBHelper.Table_LearningPlan_NANE,contentValues_study,"LearningModule=?",new String[]{ModuleName});
        }
        cursor_study.close();
        mydatabase.close();
        mydatabasehelper.close();
    }

    private void initData(String ModuleName){
        if(null == mydatabasehelper) mydatabasehelper=new MyDBHelper(context);
        if(!mydatabase.isOpen()) mydatabase=mydatabasehelper.getWritableDatabase();
        Cursor cursor = mydatabase.rawQuery("select WordSpelling,WordChineseMean,WordPhoneticSymbol from " + MyDBHelper.Table_Words_NANE + " where WordOfModule=?", new String[]{ModuleName},null);
        int count = cursor.getCount();
        if(count > 0 ){
            int i =0;
            WordSpellingArray = new String[count];
            WordChineseMeanArray = new String[count];
            WordPhoneticSymbol = new String[count];
            while(cursor.moveToNext()){
                WordSpellingArray[i]=cursor.getString(0);
                WordChineseMeanArray[i]=cursor.getString(1);
                WordPhoneticSymbol[i]=cursor.getString(2);
                i++;
            }
            cursor.close();
            mydatabase.close();
            mydatabasehelper.close();
        }
    }
}
