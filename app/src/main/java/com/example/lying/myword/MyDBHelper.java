package com.example.lying.myword;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DB_NANE="database_mywords";//数据库名
    public static final String Table_WordsBook_NANE="table_wordsbook";//词库表名
    public static final String Table_WordsModule_NANE="table_wordsmodule";//单词模块表名
    public static final String Table_Words_NANE="table_words";//单词表名
    public static final String Table_LearningPlan_NANE="table_learningplan";//学习计划表名
    public static final String Table_ReviewPlan_NANE="table_reviewplan";//复习计划表名
    public static final String Table_TestPlan_NANE="table_testplan";//测试计划表名
    public static final String Table_NewWordsBook_NAME="table_newwordsbook";//生词本表名
    //已学模块表

    public MyDBHelper(Context context) {
        super(context, DB_NANE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //词库表
        sqLiteDatabase.execSQL("create table if not exists "+Table_WordsBook_NANE+"("
                +"WordsBookID"+" interger primary key,"
                +"WordsBookName"+" varchar(255),"
                +"AddTime"+" varchar(255),"
                +"WordsNumber"+" interger,"
                +"ModuleNumber"+" interger"
                +")");
        //单词表
        sqLiteDatabase.execSQL("create table if not exists "+Table_Words_NANE+"("
                +"WordID"+" interger primary key,"
                +"WordSpelling"+" varchar(255),"
                +"WordChineseMean"+" varchar(255),"
                +"WordPhoneticSymbol"+" varchar(255),"
                +"WordOfModule"+" varchar(255)"
                +")");
        //模块表
        sqLiteDatabase.execSQL("create table if not exists "+Table_WordsModule_NANE+"("
                +"ModuleID"+" interger primary key,"
                +"ModuleName"+" varchar(255),"
                +"ModuleWordsNumber"+" interger,"
                +"ModuleOfWordsBook"+" varchar(255)"
                +")");
        //学习计划表
        sqLiteDatabase.execSQL("create table if not exists "+Table_LearningPlan_NANE+"("
                +"LearningPlanID"+" interger primary key,"
                +"LearningModule"+" varchar(255),"
                +"LearningState"+" interger,"
                +"TxtLearningPosition"+" interger,"
                +"ReviewingState"+" interger"
                +")");
        //生词本表
        sqLiteDatabase.execSQL("create table if not exists "+Table_NewWordsBook_NAME+"("
                +"NewWordID"+" interger primary key,"
                +"NewWordSpelling"+" varchar(255),"
                +"NewWordPhoneticSymbol"+" varchar(255),"
                +"NewWordChineseMean"+" varchar(255)"
                + ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
