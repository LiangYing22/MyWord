package com.example.lying.myword;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lying.myword.util.CreateModuleTxtUtil;
import com.example.lying.myword.util.File_read_write;
import com.example.lying.myword.util.countTimeUtil;
import com.example.lying.myword.util.getAssetsFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class StudyActivity extends AppCompatActivity implements View.OnClickListener ,TextToSpeech.OnInitListener {

    //传过来的数据（模块名和指针位置）
    String ModuleName ="";
    int StudyPosition= -2;

    //下一个 模块名和指针位置 提示框显示的数据
    String nextModuleName = "";
    int nextStudyPosition=-2;
    String msg = "";

    //读取文件辅助对象
    File_read_write file_read_write;

    //数据库对象
    private MyDBHelper mydatabasehelper;
    private SQLiteDatabase mydatabase;

    //指针的最后位置（即 模块的单词数量）
    int MaxPosition = -1;

    //显示的数据
    String titleText = "模块学习";
    String wordText = "";
    String wordPhoneticSymbol = "";
    String wordChineseMean = "";
    String familiarityText = "-1";
    String needReViewTime = "-1";
    String inNewWordsBook = "-1";

    //控件(标题、单词、音标、中文含义)
    TextView StudyTitle,StudyWord,StudyWordPhoneticSymbol,StudyChineseMean;

    //控件(朗读、已掌握、认识、不认识)
    ImageView StudySpeak;
    TextView StudyMaster,StudyKnow,StudyUnKnow;

    //控件(上一个、加入生词本、下一个)
    TextView previousWord,StudyJoinNewWords,nextWord;

    //定义tts
    TextToSpeech speak = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_study);

        //初始化数据库对象
        mydatabasehelper=new MyDBHelper(this);
        mydatabase=mydatabasehelper.getWritableDatabase();

        //获取传过来的模块名和指针位置
        Intent intent = getIntent();
        ModuleName = intent.getStringExtra("ModuleName");
        StudyPosition = intent.getIntExtra("StudyPosition",-2);

        //初始化要显示的数据
        initData();

        //绑定控件
        StudyTitle = (TextView)findViewById(R.id.StudyTitle);//标题
        StudyWord = (TextView)findViewById(R.id.StudyWord);//单词
        StudyWordPhoneticSymbol = (TextView)findViewById(R.id.StudyWordPhoneticSymbol);//音标
        StudyChineseMean = (TextView)findViewById(R.id.StudyChineseMean);//中文含义
        previousWord = (TextView)findViewById(R.id.previousWord);//上一个
        nextWord = (TextView)findViewById(R.id.nextWord);//下一个
        StudyMaster = (TextView)findViewById(R.id.StudyMaster);//已掌握
        StudyKnow = (TextView)findViewById(R.id.StudyKnow); //认识
        StudyUnKnow = (TextView)findViewById(R.id.StudyUnKnow); //不认识
        StudyJoinNewWords = (TextView)findViewById(R.id.StudyJoinNewWords);//加入生词本
        StudySpeak = (ImageView)findViewById(R.id.StudySpeak);//朗读
        //设置读音ImageView的图片源
        StudySpeak.setBackgroundResource(R.drawable.speaking);

        //设置数据
        setData();

        //设置响应
        StudyTitle.setOnClickListener(this);
        nextWord.setOnClickListener(this);
        previousWord.setOnClickListener(this);
        StudyMaster.setOnClickListener(this);
        StudyKnow.setOnClickListener(this);
        StudyUnKnow.setOnClickListener(this);
        StudyJoinNewWords.setOnClickListener(this);
        StudySpeak.setOnClickListener(this);
    }

    private void initData(){
        //若传过来的ModuleName不为空，则初始化
        if(!"".equals(ModuleName) && null != ModuleName){
            //初始化TextToSpeech
            speak = new TextToSpeech(this,  this);
            titleText = "'"+ModuleName+"'"+"模块学习";
            file_read_write = new File_read_write(this,ModuleName+".txt");
            try {
                MaxPosition = file_read_write.getTotalLines();//文件中的最大行数
                Log.i("StudyActivity",ModuleName+"模块的指针最后位置(单词数量)="+MaxPosition);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //若传过来指针位置不等于初始值-2时，初始化
            if(StudyPosition != -2 && null != file_read_write){
                //若指针等于-1，表示未开始学习
                if(StudyPosition == -1){
                    StudyPosition = 1;
                    //更新学习表对应的行中学习状态为学习中，并更新指针位置
                    CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(this);
                    createModuleTxtUtil.updateStudyTable(ModuleName,StudyPosition);

                    String content = file_read_write.getDataFromLineNum(StudyPosition);
                    if(!"".equals(content) && null != content && content.contains("*")){
                        String[] contentArray = content.split("\\*");
                        if(contentArray.length == 6){
                            wordText = contentArray[0];
                            wordChineseMean = contentArray[1];
                            wordPhoneticSymbol = contentArray[2];
                            familiarityText = contentArray[3];
                            needReViewTime = contentArray[4];
                            inNewWordsBook = contentArray[5];
                        }
                    }
                }
                //若指针大于-1且小于等于MaxPosition
                if(StudyPosition > -1 && StudyPosition <= MaxPosition){
                    String content = file_read_write.getDataFromLineNum(StudyPosition);
                    if(!"".equals(content) && null != content && content.contains("*")){
                        String[] contentArray = content.split("\\*");
                        if(contentArray.length == 6){
                            wordText = contentArray[0];
                            wordChineseMean = contentArray[1];
                            wordPhoneticSymbol = contentArray[2];
                            familiarityText = contentArray[3];
                            needReViewTime = contentArray[4];
                            inNewWordsBook = contentArray[5];
                        }
                    }
                }
            }
        }else if("".equals(ModuleName) || null == ModuleName){ //若传过来的数据为空 两种可能：1.未导入词库 2.学习完成
            Cursor cursor_wordsbook = mydatabase.rawQuery("select * from "+MyDBHelper.Table_WordsBook_NANE,null);
            int count = cursor_wordsbook.getCount();
            if(count > 0){
                //大于0表示有词库，学习完成
                titleText = "恭喜你！学习完成！";
                wordChineseMean = "完成学习，快去复习和学习其他词库吧！";
            }else{
                titleText = "未开始学习！";
                wordChineseMean = "你还没有添加词库，快去添加学习吧！";
            }
        }
    }

    //设置数据
    private void setData(){
        //根据标题设置按钮
        if(titleText.equals("恭喜你！学习完成！") || titleText.equals("未开始学习！")){
            StudySpeak.setEnabled(false);
            StudySpeak.setBackgroundColor(Color.argb(0,0,0,0));
            setStyleAfterClick();
            StudyJoinNewWords.setBackgroundColor(Color.argb(100,201,250,151));
            StudyJoinNewWords.setEnabled(false);
            //显示的数据
            StudyTitle.setText(titleText);
            StudyWord.setText(wordText);
            StudyChineseMean.setText(wordChineseMean);
            StudyWordPhoneticSymbol.setText(wordPhoneticSymbol);
            return;
        }
        //显示的数据
        StudyTitle.setText(titleText);
        StudyTitle.setTypeface(getAssetsFileUtil.getAssetsTtfFile(this,"studytitle"));
        StudyWord.setText(wordText);
        StudyChineseMean.setText(wordChineseMean);
        StudyWordPhoneticSymbol.setText(wordPhoneticSymbol);
        //控制"已掌握","认识","不认识"的可点击性和颜色
        if(familiarityText.equals("-1") && needReViewTime.equals("-1") && inNewWordsBook.equals("-1")){
            //初始状态
            setOriginalText();
        }else if(familiarityText.equals("3")){
            setStyleAfterClick();
            StudyMaster.setBackgroundResource(R.drawable.stdudy_master_bg2);
        }else if(familiarityText.equals("2")){
            setStyleAfterClick();
            StudyKnow.setBackgroundResource(R.drawable.study_add_bg2);
        }else if(familiarityText.equals("1")){
            setStyleAfterClick();
            StudyUnKnow.setBackgroundResource(R.drawable.study_unknow_bg2);
        }
        //控制“加入生词本”的可点击性和颜色（通过判断单词是否在生词表中）
        Cursor cursor_newWordsBook = mydatabase.rawQuery("select * from "+MyDBHelper.Table_NewWordsBook_NAME+" where NewWordSpelling=?",new String[]{wordText});
        if(cursor_newWordsBook.getCount() == 0){
            //初始状态
            StudyJoinNewWords.setBackgroundResource(R.drawable.study_add_bg);
            StudyJoinNewWords.setText("加入生词本");
            StudyJoinNewWords.setEnabled(true);
        }else if(cursor_newWordsBook.getCount() > 0){
            StudyJoinNewWords.setBackgroundResource(R.drawable.study_add_bg2);
            StudyJoinNewWords.setText("已添加生词");
            StudyJoinNewWords.setEnabled(false);
        }
        cursor_newWordsBook.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.StudyTitle://标题

                break;
            case R.id.nextWord://下一个
                if(StudyPosition != -2 && "" != ModuleName && null != ModuleName && StudyPosition < MaxPosition && StudyPosition >= 1){
                    StudyPosition = StudyPosition + 1;
                    //更新学习表对应的行中学习状态为学习中，并更新指针位置
                    CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(this);
                    createModuleTxtUtil.updateStudyTable(ModuleName,StudyPosition);
                    if(null == file_read_write) file_read_write = new File_read_write(this,ModuleName+".txt");
                    String content = file_read_write.getDataFromLineNum(StudyPosition);
                    if(!"".equals(content) && null != content && content.contains("*")){
                        String[] contentArray = content.split("\\*");
                        if(contentArray.length == 6){
                            wordText = contentArray[0];
                            wordChineseMean = contentArray[1];
                            wordPhoneticSymbol = contentArray[2];
                            familiarityText = contentArray[3];
                            needReViewTime = contentArray[4];
                            inNewWordsBook = contentArray[5];
                        }
                    }
                    setData();
                    //若当前指针时最大值，则提示是否切换到下一个模块
                }else if(!"".equals(ModuleName) && null != ModuleName && StudyPosition == MaxPosition){
                    //因为nextModuleName是全局变量，所以当最后一个模块学习完成时，点"下一个"nextModuleName还是有值的(保存着最后一个模块对应的值)，所以用此变量来解决bug
                    int hasNext = 0;
                    //查询学习中的模块，若没有查询未学习的模块，若还是没有则表示该词本学习完成
                    Cursor cursor_learning = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningState=? and LearningModule !=? limit 1 ",new String[]{"0",ModuleName});
                    if(cursor_learning.getCount() >0){
                        while (cursor_learning.moveToNext()){
                            nextModuleName = cursor_learning.getString(1);
                            nextStudyPosition = cursor_learning.getInt(3);
                            hasNext = 1;
                        }
                        cursor_learning.close();
                    }else{
                        cursor_learning.close();
                        Cursor cursor_unlearning = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningState=? and LearningModule !=? limit 1 ",new String[]{"-1",ModuleName});
                        if (cursor_unlearning.getCount() > 0){
                            while(cursor_unlearning.moveToNext()){
                                nextModuleName = cursor_unlearning.getString(1);
                                nextStudyPosition = cursor_unlearning.getInt(3);
                                hasNext = 1;
                            }
                        }
                        cursor_unlearning.close();
                    }
                    //判断获取的下一个学习模块是否为空
                    if(hasNext == 1 && !"".equals(nextModuleName) && null != nextModuleName && nextStudyPosition > -2){
                        msg = "这是最后一个单词了，是否跳转到下一个模块学习？";
                    }else{
                        msg = "恭喜你！学习完成！";
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("提示").setMessage(msg)
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(msg.equals("这是最后一个单词了，是否跳转到下一个模块学习？")){
                                        //把当前模块的学习状态修改为完成学习
                                        Cursor cursor_study = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningModule=?",new String[]{ModuleName});
                                        if(cursor_study.getCount() == 1){
                                            ContentValues contentValues_study = new ContentValues();
                                            contentValues_study.put("LearningState",1);
                                            mydatabase.update(MyDBHelper.Table_LearningPlan_NANE,contentValues_study,"LearningModule=?",new String[]{ModuleName});
                                        }
                                        cursor_study.close();
                                        //修改要显示的数据为下一个模块的数据
                                        ModuleName = nextModuleName;
                                        StudyPosition = nextStudyPosition;
                                        //若模块文件不存在则创建
                                        if(StudyPosition == -1 && !isFileExist(ModuleName)){
                                            CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(StudyActivity.this);
                                            createModuleTxtUtil.createTxtOfModuleName(ModuleName);
                                        }
                                        initData();
                                        setData();
                                    }else if(msg.equals("恭喜你！学习完成！")){
                                        //把当前模块的学习状态修改为完成学习
                                        Cursor cursor_study = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningModule=?",new String[]{ModuleName});
                                        if(cursor_study.getCount() == 1){
                                            ContentValues contentValues_study = new ContentValues();
                                            contentValues_study.put("LearningState",1);
                                            mydatabase.update(MyDBHelper.Table_LearningPlan_NANE,contentValues_study,"LearningModule=?",new String[]{ModuleName});
                                        }
                                        cursor_study.close();
                                    }
                                }
                            }).show();
                }
                break;
            case R.id.previousWord://上一个
                if(StudyPosition != -2 && !"".equals(ModuleName) && null != ModuleName && StudyPosition > 1 && StudyPosition <= MaxPosition){
                    StudyPosition = StudyPosition - 1;
                    //更新学习表对应的行中学习状态为学习中，并更新指针位置
                    CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(this);
                    createModuleTxtUtil.updateStudyTable(ModuleName,StudyPosition);
                    if(null == file_read_write) file_read_write = new File_read_write(this,ModuleName+".txt");
                    String content = file_read_write.getDataFromLineNum(StudyPosition);
                    if(!"".equals(content) && null != content && content.contains("*")){
                        String[] contentArray = content.split("\\*");
                        if(contentArray.length == 6){
                            wordText = contentArray[0];
                            wordChineseMean = contentArray[1];
                            wordPhoneticSymbol = contentArray[2];
                            familiarityText = contentArray[3];
                            needReViewTime = contentArray[4];
                            inNewWordsBook = contentArray[5];
                        }
                    }
                    setData();
                }
                break;
            case R.id.StudyMaster://已掌握
                //点击"已掌握","认识","不认识"三个按钮时 背景改变 设置不可点击
                setStyleAfterClick();
                StudyMaster.setBackgroundResource(R.drawable.stdudy_master_bg2);
                if(null == file_read_write) file_read_write = new File_read_write(this,ModuleName+".txt");;
                file_read_write.setDataFromLineNum(StudyPosition,"3","0", countTimeUtil.getCurrentTime());
                break;
            case R.id.StudyKnow://认识
                setStyleAfterClick();
                StudyKnow.setBackgroundResource(R.drawable.study_add_bg2);
                if(null == file_read_write) file_read_write = new File_read_write(this,ModuleName+".txt");;
                file_read_write.setDataFromLineNum(StudyPosition,"2","2",countTimeUtil.getCurrentTime());
                break;
            case R.id.StudyUnKnow://不认识
                setStyleAfterClick();
                StudyUnKnow.setBackgroundResource(R.drawable.study_unknow_bg2);
                if(null == file_read_write) file_read_write = new File_read_write(this,ModuleName+".txt");;
                file_read_write.setDataFromLineNum(StudyPosition,"1","3",countTimeUtil.getCurrentTime());
                break;
            case R.id.StudyJoinNewWords://加入生词本
                StudyJoinNewWords.setBackgroundResource(R.drawable.study_add_bg2);
                StudyJoinNewWords.setText("已添加生词");
                StudyJoinNewWords.setEnabled(false);
//                //1.改变txt文件的标志位(先改成复习状态)
//                if(null == file_read_write) file_read_write = new File_read_write(this,ModuleName+".txt");;
//                file_read_write.setDataFromLineNum(StudyPosition,"1");
                //2.加入数据库
                Cursor cursor_newwords = mydatabase.rawQuery("select * from " + MyDBHelper.Table_NewWordsBook_NAME, null);
                int count_newwords = cursor_newwords.getCount();
                if(count_newwords >= 0){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("NewWordID",count_newwords+1);
                    contentValues.put("NewWordSpelling",wordText);
                    contentValues.put("NewWordPhoneticSymbol",wordPhoneticSymbol);
                    contentValues.put("NewWordChineseMean",wordChineseMean);
                    mydatabase.insert(MyDBHelper.Table_NewWordsBook_NAME, null, contentValues);
                }
                cursor_newwords.close();
                break;
            case R.id.StudySpeak:
                //初始朗读帧动画对象
                AnimationDrawable rocketAnimation = (AnimationDrawable) StudySpeak.getBackground();
                AnimationDrawable rocketAnimation1 = (AnimationDrawable) StudySpeak.getBackground();
                rocketAnimation.start();
                speak.speak(wordText,TextToSpeech.QUEUE_ADD,null);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mydatabase.close();
        mydatabasehelper.close();
        //关闭tts引擎
        if (speak != null)
            speak.shutdown();
    }

    //根据文件名判断 data目录 下 files目录 中 文件 是否存在
    private boolean isFileExist(String ModuleName){
        String FileName = ModuleName + ".txt";
        //file目录
        File directory = getFilesDir();
        if (!"".equals(ModuleName) && null != ModuleName && directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if(FileName.equals(item.getName())){
                    return true;
                }
            }
        }
        return false;
    }

    //当"已掌握","认识","不认识"三个按钮被点击时 背景改变 设置不可点击
    private void setStyleAfterClick(){
        //设置 三个按钮的 背景颜色
        StudyMaster.setBackgroundResource(R.drawable.stdudy_master_bg);
        StudyKnow.setBackgroundResource(R.drawable.study_add_bg);
        StudyUnKnow.setBackgroundResource(R.drawable.study_unknow_bg);
        //设置 三个按钮为不可点击
        StudyMaster.setEnabled(false);
        StudyKnow.setEnabled(false);
        StudyUnKnow.setEnabled(false);
        //"下一个"为可见
        nextWord.setVisibility(View.VISIBLE);
    }

    //"已掌握","认识","不认识"三个按钮初始的状态
    private void setOriginalText(){
        //设置 三个按钮的 背景颜色
        StudyMaster.setBackgroundResource(R.drawable.stdudy_master_bg);
        StudyKnow.setBackgroundResource(R.drawable.study_add_bg);
        StudyUnKnow.setBackgroundResource(R.drawable.study_unknow_bg);
        //设置 三个按钮为不可点击
        StudyMaster.setEnabled(true);
        StudyKnow.setEnabled(true);
        StudyUnKnow.setEnabled(true);
        //"下一个"为不可见(保留控件空间)
        nextWord.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onInit(int status) {
        speak.setLanguage(Locale.US);// 初始化TTS组件，设置语言为US英语
    }
}
