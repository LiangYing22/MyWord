package com.example.lying.myword.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class countTimeUtil {

    public interface CallBack {
        //执行回调操作的方法
        void doSometing();
    }

    public static void countTime(CallBack callBack,String TAG) {
        long startTime= System.currentTimeMillis(); //起始时间
        callBack.doSometing(); ///进行回调操作
        long endTime = System.currentTimeMillis(); //结束时间
        Log.i(TAG,String.format("方法使用时间 %d ms", endTime - startTime)); //打印使用时间
    }

    //返回系统当前时间
    public static String getCurrentTime(){
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String str_time = sdf.format(dt);
        return str_time;
    }

    //比较两个时间的大小
    public static int CompareTime(String time1,String time2){
        //时间格式为：2019-02-03-13-02-03(2019年2月3日13点2分3秒)
        //前面参数(time1)大返回正数，后面参数(time2)大返回负数,相等返回0；
        if(null != time1 && null != time2 && !"".equals(time1) && !"".equals(time2) ){
            return time1.compareTo(time2);
        }
        return 0;
    }

    //判断字符串中是否含有中文
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
