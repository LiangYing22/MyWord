package com.example.lying.myword.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class getAssetsFileUtil {

    public static List<String> getAssetsTxtFile(Context context){
        List<String> list=new ArrayList<String>();
        try {
            String[] Files = context.getResources().getAssets().list("");
            String[] txtFiles = context.getResources().getAssets().list("txt");
            for(String s:txtFiles){
                if(s.contains(".txt")){
                    list.add(s);
                    Log.i("SixLevelWordDecompose","正确文件:"+s+"正确文件个数:"+list.size());
                    Log.i("SixLevelWordDecompose","路劲:"+context.getFilesDir().getPath() + "/");
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Typeface getAssetsTtfFile(Context context){
        //从asset 读取字体
        AssetManager mgr = context.getAssets();
        //根据路径得到Typeface
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/MFLangSong_Noncommercial-Regular.ttf");//仿宋
        return tf;
    }

    public static Typeface getAssetsTtfFile(Context context,String ttfType){
        //从asset 读取字体
        AssetManager mgr = context.getAssets();
        if(ttfType.equals("studytitle")){
            //根据路径得到Typeface
            Typeface tf = Typeface.createFromAsset(mgr, "fonts/studytitle.ttf");//字魂71号-御守锦书
            return tf;
        }
        return null;
    }

}
