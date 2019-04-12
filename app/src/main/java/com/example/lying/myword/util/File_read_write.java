package com.example.lying.myword.util;

import android.content.Context;
import android.util.Log;

import com.example.lying.myword.Survey;
import com.example.lying.myword.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_APPEND;

public class File_read_write {
    private Context context;
    private String FileName;
    public File_read_write(Context context,String filename){
        this.context=context;
        this.FileName=filename;
    }
    public String getFileName(){
        return this.FileName;
    }
    public String read() {
        try {
            if(FileName==null||FileName.equals(""))
                return null;
            FileInputStream inStream = context.openFileInput(FileName);
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder();
            while ((hasRead = inStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead));
            }

            inStream.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String msg,int mode){
        // 步骤1：获取输入值
        if(msg == null) return;
        try {
            // 步骤2:创建一个FileOutputStream对象,MODE_APPEND追加模式,Context.MODE_PRIVATE覆盖模式
            FileOutputStream fos = context.openFileOutput(FileName,mode);
            // 步骤3：将获取过来的值放入文件
            fos.write(msg.getBytes());
            // 步骤4：关闭数据流
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //读取指定的行
    public String getDataFromLineNum(int LineNum){
        try {
//            File file = new File(FileName);
//            Log.i("File_read_write",file.isFile()+""+" "+file.getAbsolutePath());
//            FileReader in = new FileReader(file);//此处异常 TODO
//            LineNumberReader reader = new LineNumberReader(in);
//            String TargetStr = "";
//            if (LineNum <= 0 || LineNum > getTotalLines(file)) {
//                return "参数错误";
//            }
//            int lines = 0;
//            while (TargetStr != null) {
//                lines++;
//                TargetStr = reader.readLine();
//                if((lines - LineNum) == 0) {
//                    return TargetStr;
//                }
//            }
//            reader.close();
//            in.close();
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            int i = 0;
            String Target ;
            if (LineNum <= 0 || LineNum > getTotalLines()) {
                return "参数错误";
            }
            while (null != (Target = bf.readLine())) {
                i++;
                if((i - LineNum) == 0){
                    Log.i("File_read_write",Target);
                    bf.close();
                    inputReader.close();
                    inStream.close();
                    return Target;
                }
            }
            bf.close();
            inputReader.close();
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //更改指定的行(其中的数据以*分割)
    public void setDataFromLineNum(int LineNum,String familiarity,String needReviewTime,String studyTime){
        try {
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            int i = 0;
            StringBuffer sb = new StringBuffer();
            String Target ;
            if (LineNum <= 0 || LineNum > getTotalLines()) {
                return;
            }
            while (null != (Target = bf.readLine())) {
                i++;
                if((i - LineNum) == 0){
                    if(Target.contains("*")){
                        int count = Target.split("\\*").length;
                        if(count == 6){
                            Target = Target.split("\\*")[0]+"*"+Target.split("\\*")[1]+"*"
                                    +Target.split("\\*")[2]+"*"+familiarity+"*"+needReviewTime
                                    +"*"+studyTime;
                        }
                    }
                }
                sb.append(Target+"\n");
            }
            //若buf不为空则把buf添加入文件
            if(null != sb && sb.length()>0){
                write(sb.toString(),Context.MODE_PRIVATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //更改指定的行(其中的数据以*分割)
    public void setDataFromLineNum(int LineNum,String familiarity,String needReviewTime){
        try {
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            int i = 0;
            StringBuffer sb = new StringBuffer();
            String Target ;
            if (LineNum <= 0 || LineNum > getTotalLines()) {
                return;
            }
            while (null != (Target = bf.readLine())) {
                i++;
                if((i - LineNum) == 0){
                    if(Target.contains("*")){
                        int count = Target.split("\\*").length;
                        if(count == 6){
                            Target = Target.split("\\*")[0]+"*"+Target.split("\\*")[1]+"*"
                                    +Target.split("\\*")[2]+"*"+familiarity+"*"+needReviewTime
                                    +"*"+Target.split("\\*")[5];
                        }
                    }
                }
                sb.append(Target+"\n");
            }
            //若buf不为空则把buf添加入文件
            if(null != sb && sb.length()>0){
                write(sb.toString(),Context.MODE_PRIVATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //更改指定的行(其中的数据以*分割)
    public void setDataFromLineNum(int LineNum,String needReviewTime,String studyTime,int type){
        try {
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            int i = 0;
            StringBuffer sb = new StringBuffer();
            String Target ;
            if (LineNum <= 0 || LineNum > getTotalLines()) {
                return;
            }
            while (null != (Target = bf.readLine())) {
                i++;
                if((i - LineNum) == 0){
                    if(Target.contains("*")){
                        int count = Target.split("\\*").length;
                        if(count == 6){
                            Target = Target.split("\\*")[0]+"*"+Target.split("\\*")[1]+"*"
                                    +Target.split("\\*")[2]+"*"+Target.split("\\*")[3]+"*"+needReviewTime
                                    +"*"+studyTime;
                        }
                    }
                }
                sb.append(Target+"\n");
            }
            //若buf不为空则把buf添加入文件
            if(null != sb && sb.length()>0){
                write(sb.toString(),Context.MODE_PRIVATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //更改指定的行(其中的数据以*分割)
    public void setDataFromLineNum(int LineNum,String studyTime){
        try {
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            int i = 0;
            StringBuffer sb = new StringBuffer();
            String Target ;
            if (LineNum <= 0 || LineNum > getTotalLines()) {
                return;
            }
            while (null != (Target = bf.readLine())) {
                i++;
                if((i - LineNum) == 0){
                    if(Target.contains("*")){
                        int count = Target.split("\\*").length;
                        if(count == 6){
                            Target = Target.split("\\*")[0]+"*"+Target.split("\\*")[1]+"*"
                                    +Target.split("\\*")[2]+"*"+Target.split("\\*")[3]+"*"
                                    +Target.split("\\*")[4] +"*"+studyTime;
                        }
                    }
                }
                sb.append(Target+"\n");
            }
            //若buf不为空则把buf添加入文件
            if(null != sb && sb.length()>0){
                write(sb.toString(),Context.MODE_PRIVATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //更改reviewTxt文件中的指定行
    public void setReviewTxtFromLineNum(int LineNum,String needStudyTime,String studyTime){
        try {
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            int i = 0;
            StringBuffer sb = new StringBuffer();
            String Target ;
            if (LineNum <= 0 || LineNum > getTotalLines()) {
                return;
            }
            while (null != (Target = bf.readLine())) {
                i++;
                if((i - LineNum) == 0){
                    if(Target.contains("*")){
                        int count = Target.split("\\*").length;
                        if(count == 6){
                            Target = Target.split("\\*")[0]+"*"+Target.split("\\*")[1]+"*"
                                    +Target.split("\\*")[2]+"*"+Target.split("\\*")[3]+"*"
                                    +needStudyTime +"*"+studyTime;
                        }
                    }
                }
                sb.append(Target+"\n");
            }
            //若buf不为空则把buf添加入文件
            if(null != sb && sb.length()>0){
                write(sb.toString(),Context.MODE_PRIVATE);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //获取文件中 未学习、复习中、已掌握 的数量
    public String[] currentModuleStudyState(){
        String[] content = new String[3];
        int x = 0;//未学习数量
        int y = 0;//需复习数量
        int z = 0;//已掌握数量
        try {
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            String Target ;
            while (null != (Target = bf.readLine())) {
                if(Target.contains("*")){
                    int count = Target.split("\\*").length;
                    if(count == 6){
                        if(Target.split("\\*")[3].equals("-1")){
                            x++;
                        }
                        if(!Target.split("\\*")[4].equals("0") && !Target.split("\\*")[4].equals("-1")){
                            y++;
                        }else if(Target.split("\\*")[4].equals("0")){
                            z++;
                        }
                    }
                }
            }
            content[0] = ""+x;
            content[1] = ""+y;
            content[2] = ""+z;
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取模块学习概况对象
    public Survey getSurvey(){
        Survey survey = new Survey(FileName.substring(0,FileName.length()-4));
        int unLearningNum = 0;//未学习数量
        int reviewingNum = 0;//需复习数量
        int masterNum = 0;//已掌握数量
        List<Word> unLearning = new ArrayList<>();//未学习的单词
        List<Word> reviewing = new ArrayList<>();//需复习的单词
        List<Word> master = new ArrayList<>();//已掌握单词
        try {
            FileInputStream inStream = context.openFileInput(FileName);
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bf = new BufferedReader(inputReader);
            String Target ;
            while (null != (Target = bf.readLine())) {
                if(Target.contains("*")){
                    int count = Target.split("\\*").length;
                    if(count == 6){
                        if(Target.split("\\*")[3].equals("-1")){
                            unLearningNum++;
                            String spelling = Target.split("\\*")[0];
                            String chinese = Target.split("\\*")[1];
                            String phoneticSymbol = Target.split("\\*")[2];
                            Word word = new Word(spelling,phoneticSymbol,chinese);
                            unLearning.add(word);
                        }
                        if(!Target.split("\\*")[4].equals("0") && !Target.split("\\*")[4].equals("-1")){
                            reviewingNum++;
                            String spelling = Target.split("\\*")[0];
                            String chinese = Target.split("\\*")[1];
                            String phoneticSymbol = Target.split("\\*")[2];
                            Word word = new Word(spelling,phoneticSymbol,chinese);
                            reviewing.add(word);
                        }else if(Target.split("\\*")[4].equals("0")){
                            masterNum++;
                            String spelling = Target.split("\\*")[0];
                            String chinese = Target.split("\\*")[1];
                            String phoneticSymbol = Target.split("\\*")[2];
                            Word word = new Word(spelling,phoneticSymbol,chinese);
                            master.add(word);
                        }
                    }
                }
            }
            survey.setUnLearningNum(unLearningNum);
            survey.setReviewingNum(reviewingNum);
            survey.setMasterNum(masterNum);
            survey.setUnLearning(unLearning);
            survey.setReviewing(reviewing);
            survey.setMaster(master);
            return survey;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 文件内容的总行数。
    public int getTotalLines() throws IOException {
//        FileReader in = new FileReader(file);
//        LineNumberReader reader = new LineNumberReader(in);
//        String s = reader.readLine();
//        int lines = 0;
//        while (s != null) {
//            lines++;
//            s = reader.readLine();
//        }
//        reader.close();
//        in.close();
//        return lines;
        FileInputStream inStream = context.openFileInput(FileName);
        InputStreamReader inputReader = new InputStreamReader(inStream);
        BufferedReader bf = new BufferedReader(inputReader);
        int i = 0;
        while (null != bf.readLine()) {
            i++;
        }
        bf.close();
        inputReader.close();
        inStream.close();
        return i;
    }

}
