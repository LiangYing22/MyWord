package com.example.lying.myword.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lying.myword.MyDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisturbOptionsUtil {

    private List<String> optionsList = new ArrayList<>();
    private Random random = new Random();

    //数据库
    private MyDBHelper mydatabasehelper;
    private SQLiteDatabase mydatabase;

    private Context context;

    //构造函数中初始化数据
    public DisturbOptionsUtil(Context context){
        this.context = context;
        //先构造至少需要的三个不同的，以防词库中没有数据（至少不是两个，应为怕重复一个）
        optionsList.add("ad.完全的；绝对的");
        optionsList.add("ad.几乎，差不多");
        optionsList.add("n.参观，访问");
        //初始化数据库
        mydatabasehelper=new MyDBHelper(this.context);
        mydatabase=mydatabasehelper.getWritableDatabase();
        Cursor cursor_word = mydatabase.rawQuery("select WordChineseMean from "+MyDBHelper.Table_Words_NANE,null);
        int count = cursor_word.getCount();
        if(count > 0){
            while(cursor_word.moveToNext()){
                optionsList.add(cursor_word.getString(0));
            }
        }
        cursor_word.close();
    }

    public String[] getDisturbOption(String CorrectOption){
        int min=0;
        int max=optionsList.size()-1;
        int num1 = -1;
        int num2 = -1;
        while(num1 == num2){
            num1 = random.nextInt(max)%(max-min+1) + min;
            num2 = random.nextInt(max)%(max-min+1) + min;
            if(optionsList.get(num1).equals(CorrectOption) || optionsList.get(num2).equals(CorrectOption)){
                num1 = -1;
                num2 = -1;
            }
        }
        String[] TwoOption = new String[2];
        TwoOption[0] = optionsList.get(num1);
        TwoOption[1] = optionsList.get(num2);
        return TwoOption;
    }
}
