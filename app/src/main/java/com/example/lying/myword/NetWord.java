package com.example.lying.myword;

import org.json.JSONArray;
import org.json.JSONObject;

public class NetWord {

    String word_name = "";//单词
    String word_pl = "";//单词复数
    String word_past = "";//单词过去时
    String word_done = "";//单词过去分词
    String word_ing = "";//单词进行时
    String word_third = "";
    String word_er = "";
    String word_est = "";
    String ph_en = "";//单词英式音标
    String ph_am = "";//单词美式音标
    String ph_other = "";//单词其他音标
    String ph_en_mp3 = "";//单词英式发音
    String ph_am_mp3 = "";//单词美式发音
    String ph_tts_mp3 = "";//单词tts发音
    int parts_num = 0;//单词词义数目
    String[] partsArray ;//单词词义和相应含义(词义，含义。交替存储)

    String word_name_CN = "";//中文
    String word_symbol_CN = "";//中文拼音
    int parts_num_CN = 0;//中文词义数目
    String[] partsArray_CN;//中文词义和相应含义(词义，含义。交替存储)

    public NetWord (JSONObject json,String type){
        try{
            //输入的是英文
            if(type.equals("1")){
                word_name = json.getString("word_name");
                JSONObject exchange = json.getJSONObject("exchange");
                word_pl = exchange.getString("word_pl");
                word_past = exchange.getString("word_past");
                word_done = exchange.getString("word_done");
                word_ing = exchange.getString("word_ing");
                word_third = exchange.getString("word_third");
                word_er = exchange.getString("word_er");
                word_est = exchange.getString("word_est");
                JSONArray symbols = json.getJSONArray("symbols");
                JSONObject json2 = symbols.getJSONObject(0);
                ph_en = json2.getString("ph_en");
                ph_am = json2.getString("ph_am");
                ph_other = json2.getString("ph_other");
                ph_en_mp3 = json2.getString("ph_en_mp3");
                ph_am_mp3 = json2.getString("ph_am_mp3");
                ph_tts_mp3 = json2.getString("ph_tts_mp3");
                JSONArray parts = json2.getJSONArray("parts");
                parts_num = parts.length();
                partsArray = new String[parts_num*2];
                for(int i = 0;i < parts_num;i++){
                    partsArray[i*2] = parts.getJSONObject(i).getString("part");
                    String s = parts.getJSONObject(i).getString("means");
                    partsArray[i*2+1] = s.substring(1,s.length()-1);
//                    partsArray[i*2+1] = parts.getJSONObject(i).getJSONArray("means").toString();
                }
            }else if(type.equals("0")){ //输入的是中文
                word_name_CN = json.getString("word_name");
                JSONArray symbols = json.getJSONArray("symbols");
                JSONObject json2 = symbols.getJSONObject(0);
                word_symbol_CN = json2.getString("word_symbol");
                JSONArray parts = json2.getJSONArray("parts");
                JSONObject json3 = parts.getJSONObject(0);
                JSONArray means = json3.getJSONArray("means");
                parts_num_CN = means.length();
                partsArray_CN = new String[parts_num_CN];
                for(int i = 0;i < parts_num_CN;i++){
                    partsArray_CN[i] = means.getJSONObject(i).getString("word_mean");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //英文的get方法
    public String getWord_name(){
        return word_name;
    }
    public String getPh_am(){
        if(ph_am != null && !ph_am.equals("")){
            return "/"+ph_am+"/";
        }
        return ph_am;
    }
    public String getChineseMean(){
        return getString(partsArray,"1");
    }

    //中文的get方法
    public String getWord_name_CN(){
        return word_name_CN;
    }
    public String getPinYin(){
        if(word_symbol_CN != null && !word_symbol_CN.equals("")){
            return "/"+word_symbol_CN+"/";
        }
        return word_symbol_CN;
    }
    public String getEnglishMean(){
        return getString(partsArray_CN,"0");
    }

    //将英文和中文数组输出为String
    private String getString(String[] array,String type){
        String arrayString = "";
        //英文
        if(type.equals("1")){
            int count = array.length / 2;
            for(int i = 0;i < count;i++){
                arrayString = arrayString + "△" + array[i*2] + " " + array[i*2+1]+"\n";
            }
        }else if(type.equals("0")){
            int count = array.length;
            for(int i = 0;i < count;i++){
                arrayString = arrayString + "△" + array[i]+"\n";
            }
        }
        return arrayString;
    }
}
