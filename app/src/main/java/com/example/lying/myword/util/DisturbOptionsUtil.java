package com.example.lying.myword.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisturbOptionsUtil {

    private static List<String> optionsList = new ArrayList<>();
    private static Random random = new Random();

    //构造函数中初始化数据
    public DisturbOptionsUtil(){
        //先构造至少需要的三个不同的，以防词库中没有数据（至少不是两个，应为怕重复一个）
    }

    public static String[] getDisturbOption(String CorrectOption){
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
        TwoOption[2] = optionsList.get(num2);
        return TwoOption;
    }
}
