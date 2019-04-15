package com.example.lying.myword;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lying.myword.util.CreateModuleTxtUtil;
import com.example.lying.myword.util.File_read_write;
import com.example.lying.myword.util.getAssetsFileUtil;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    TextView home_WordsBook,home_StudySurvey,home_currentStudySurvey;
    Button home_WordsBookNum,home_UnlearnedWordsTop,home_ReviewWordsTop,home_MasterWordsTop;
    Button home_UnlearnedWordsBelow,home_ReviewWordsBelow,home_MasterWordsBelow;
    Button home_StudyWords,home_ReviewWords,home_TestWords;

    LinearLayout home_WordsBookAndNum;

    //Context对象
    Context context;

    //数据库
    MyDBHelper mydatabasehelper;
    SQLiteDatabase mydatabase;

    //读取文件辅助对象
    File_read_write file_read_write ;

    //单词本中数据
    int WordsBookID = -1;
    String WordsBookName = "未选择词本";
    String AddTime = "1970-1-1";
    int WordsNumber = 0;
    int ModuleNumber = 0;

    //未学习、复习中、已掌握 的 显示数据
    String currentModule = "";
    String currentStudySurvey = "当前模块学习概况";
    String unlearned = "0", needReview = "0",master = "0";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        context = getContext();
        //获取数据库数据
        mydatabasehelper = new MyDBHelper(getContext());
        mydatabase = mydatabasehelper.getWritableDatabase();
    }

    @Override
    public void onStart(){
        super.onStart();
        initData();
        setData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View HomeView=inflater.inflate(R.layout.home,container,false);
        home_WordsBook=(TextView)HomeView.findViewById(R.id.home_WordsBook);//当前词本Textview
        home_WordsBookNum=(Button)HomeView.findViewById(R.id.home_WordsBookNum);
        home_StudySurvey = (TextView)HomeView.findViewById(R.id.home_StudySurvey);//学习概况
        home_currentStudySurvey = (TextView)HomeView.findViewById(R.id.home_currentStudySurvey);//当前模块学习概况
        home_UnlearnedWordsTop=(Button)HomeView.findViewById(R.id.home_UnlearnedWordsTop);
        home_ReviewWordsTop=(Button)HomeView.findViewById(R.id.home_ReviewWordsTop);
        home_MasterWordsTop=(Button)HomeView.findViewById(R.id.home_MasterWordsTop);
        home_UnlearnedWordsBelow=(Button)HomeView.findViewById(R.id.home_UnlearnedWordsBelow);
        home_ReviewWordsBelow=(Button)HomeView.findViewById(R.id.home_ReviewWordsBelow);
        home_MasterWordsBelow=(Button)HomeView.findViewById(R.id.home_MasterWordsBelow);
        home_StudyWords=(Button)HomeView.findViewById(R.id.home_StudyWords);
        home_ReviewWords=(Button)HomeView.findViewById(R.id.home_ReviewWords);
        home_TestWords=(Button)HomeView.findViewById(R.id.home_TestWords);

        home_WordsBookAndNum=(LinearLayout)HomeView.findViewById(R.id.home_WordsBookAndNum);//词本和单词数量的父布局

//        //初始化要显示的数据(放到onCreat中了，便于回退时刷新)
//        initData();
//
//        //设置显示数据(放到onCreat中了，便于回退时刷新)
//        setData();

        //控件相应事件

        //直接用父布局设置点击事件
//        home_WordsBookNum.setOnClickListener(new MyonClickListener());
//        home_WordsBook.setOnClickListener(new MyonClickListener());
        //跳转到词本
        home_WordsBookAndNum.setOnClickListener(new MyonClickListener());
        //跳转到学习单词
        home_StudyWords.setOnClickListener(new MyonClickListener());
        //跳转到复习单词
        home_ReviewWords.setOnClickListener(new MyonClickListener());
        //跳转到单词测试
        home_TestWords.setOnClickListener(new MyonClickListener());
        //跳转到学习概况
        home_StudySurvey.setOnClickListener(new MyonClickListener());
        //跳转到当前模块的学习概况
        home_UnlearnedWordsTop.setOnClickListener(new MyonClickListener());
        home_UnlearnedWordsBelow.setOnClickListener(new MyonClickListener());
        home_ReviewWordsTop.setOnClickListener(new MyonClickListener());
        home_ReviewWordsBelow.setOnClickListener(new MyonClickListener());
        home_MasterWordsTop.setOnClickListener(new MyonClickListener());
        home_MasterWordsBelow.setOnClickListener(new MyonClickListener());

        return HomeView;
    }

    //初始化数据
    private void initData(){
        //初始化词本信息
        Cursor cursor_wordsbook= mydatabase.rawQuery("select * from " + MyDBHelper.Table_WordsBook_NANE, null);
        int cursor_wordsbook_count=cursor_wordsbook.getCount();
        //设置当前的词本和单词数量
        if(cursor_wordsbook_count>0){
            while(cursor_wordsbook.moveToNext()){
                WordsBookID = cursor_wordsbook.getInt(0);
                WordsBookName = cursor_wordsbook.getString(1);
                AddTime = cursor_wordsbook.getString(2);
                WordsNumber = cursor_wordsbook.getInt(3);
                ModuleNumber =cursor_wordsbook.getInt(4);
            }
        }
        cursor_wordsbook.close();

        //初始化 未学习、复习中、已掌握
        //查询当前模块，不查其他模块（在学习概况中体现其他模块）
        currentModule = "";
        Cursor cursor_learning = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningState=? limit 1 ",new String[]{"0"});
        if(cursor_learning.getCount() == 1){
            while (cursor_learning.moveToNext()){
                currentModule = cursor_learning.getString(1);
            }
        }
        cursor_learning.close();
        if(null != currentModule && !"".equals(currentModule)){
            file_read_write = new File_read_write(context,currentModule+".txt");
            String[] state = file_read_write.currentModuleStudyState();
            if(null != state && state.length == 3){
                currentStudySurvey = "'"+currentModule+"'"+"模块学习概况";
                unlearned = state[0];
                needReview = state[1];
                master = state[2];
            }
        }else{//未开始学习或者学习完成
            currentStudySurvey = "暂无模块学习概况";
            unlearned = "0";
            needReview = "0";
            master = "0";
        }
    }

    //设置显示数据
    private void setData(){
        //当前词本显示数据
        home_WordsBook.setText(WordsBookName);
        home_WordsBook.setTypeface(getAssetsFileUtil.getAssetsTtfFile(getContext()));
        home_WordsBookNum.setText("单词数："+WordsNumber);
        //未学习、复习中、已掌握 显示数据
        home_currentStudySurvey.setText(currentStudySurvey);
        home_UnlearnedWordsTop.setText(unlearned);
        home_ReviewWordsTop.setText(needReview);
        home_MasterWordsTop.setText(master);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mydatabase.close();
        mydatabasehelper.close();
    }

    private class MyonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.home_WordsBookAndNum://词本
                    Intent intent =new Intent(getActivity(),WordsModuleListActivity.class);
                    intent.putExtra("wordsbook",WordsBookName);
                    startActivity(intent);
                    break;
                case R.id.home_UnlearnedWordsTop://未学习概况
                case R.id.home_UnlearnedWordsBelow://未学习概况
                    if(currentModule != null && !currentModule.equals("")){
                        File_read_write fileReadWrite1 = new File_read_write(context,currentModule+".txt");
                        Survey survey1 = fileReadWrite1.getSurvey();
                        List<Word> unlearning = survey1.getUnLearning();
                        //发送事件
                        EventBus.getDefault().postSticky(new StudySurveyEvent(currentModule,unlearning,1));
                        Intent unStudyIntent = new Intent(context,StudySurveyOfSpecificActivity.class);
                        startActivity(unStudyIntent);
                    }

                    break;
                case R.id.home_ReviewWordsTop://需复习
                case R.id.home_ReviewWordsBelow://需复习
                    if(currentModule != null && !currentModule.equals("")){
                        File_read_write fileReadWrite2 = new File_read_write(context,currentModule+".txt");
                        Survey survey2 = fileReadWrite2.getSurvey();
                        List<Word> reviewing = survey2.getReviewing();
                        //发送事件
                        EventBus.getDefault().postSticky(new StudySurveyEvent(currentModule,reviewing,2));
                        Intent needReviewIntent = new Intent(context,StudySurveyOfSpecificActivity.class);
                        startActivity(needReviewIntent);
                    }
                    break;
                case R.id.home_MasterWordsTop://已掌握
                case R.id.home_MasterWordsBelow://已掌握
                    if(currentModule != null && !currentModule.equals("")){
                        File_read_write fileReadWrite3 = new File_read_write(context,currentModule+".txt");
                        Survey survey3 = fileReadWrite3.getSurvey();
                        List<Word> mastering = survey3.getMaster();
                        //发送事件
                        EventBus.getDefault().postSticky(new StudySurveyEvent(currentModule,mastering,3));
                        Intent masterIntent = new Intent(context,StudySurveyOfSpecificActivity.class);
                        startActivity(masterIntent);
                    }
                    break;
                case R.id.home_StudyWords://学习单词
                    String ModuleName="";
                    int StudyPosition=-2;
                    //查询学习中的模块，若没有查询未学习的模块，若还是没有则表示该词本学习完成
                    Cursor cursor_learning = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningState=? limit 1 ",new String[]{"0"});
                    if(cursor_learning.getCount() >0){
                        while (cursor_learning.moveToNext()){
                            ModuleName = cursor_learning.getString(1);
                            StudyPosition = cursor_learning.getInt(3);
                        }
                        cursor_learning.close();
                    }else{
                        cursor_learning.close();
                        Cursor cursor_unlearning = mydatabase.rawQuery("select * from "+MyDBHelper.Table_LearningPlan_NANE+" where LearningState=? limit 1 ",new String[]{"-1"});
                        if (cursor_unlearning.getCount() > 0){
                            while(cursor_unlearning.moveToNext()){
                                ModuleName = cursor_unlearning.getString(1);
                                StudyPosition = cursor_unlearning.getInt(3);
                            }
                        }
                        cursor_unlearning.close();
                    }
                    if(StudyPosition == -1 && !isFileExist(ModuleName,1)){
                        CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(getContext());
                        createModuleTxtUtil.createTxtOfModuleName(ModuleName);
                    }
                    Intent intent1 = new Intent(getActivity(),StudyActivity.class);
                    //传递要学习的模块和指针位置
                    intent1.putExtra("ModuleName",ModuleName);
                    intent1.putExtra("StudyPosition",StudyPosition);
                    startActivity(intent1);
                    break;
                case R.id.home_ReviewWords://测试单词
                    //查询学习计划表,获取当前需要复习的模块名
                    String currentReviewModuleName = "";
                    //先查询 学习状态为0(正在学习)且复习状态为0(正在复习)的模块
                        Cursor cursor_studytable00 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                                                                    " where LearningState=? and ReviewingState=? limit 1",new String[]{"0","0"});
                        if(cursor_studytable00.getCount() == 1){
                            while(cursor_studytable00.moveToNext()){
                                currentReviewModuleName = cursor_studytable00.getString(0);
                            }
                        //先删除文件（查找到了就删除）
                        deleteFile(currentReviewModuleName,2);
                        //重新创建文件
                        CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(getContext());
                        createModuleTxtUtil.createReviewTxtOfModuleName(currentReviewModuleName);
                    }
                    cursor_studytable00.close();
                    //再查询 学习状态为0(正在学习)且复习状态为-1(未复习)的模块
                    if(null == currentReviewModuleName || currentReviewModuleName.equals("") || !isFileExist(currentReviewModuleName,2)){
                        Cursor cursor_studytable0_1 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                                " where LearningState=? and ReviewingState=? limit 1",new String[]{"0","-1"});
                        if(cursor_studytable0_1.getCount() == 1){
                            while(cursor_studytable0_1.moveToNext()){
                                currentReviewModuleName = cursor_studytable0_1.getString(0);
                            }
                            //先删除文件（查找到了就删除）
                            deleteFile(currentReviewModuleName,2);
                            //重新创建文件
                            CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(getContext());
                            createModuleTxtUtil.createReviewTxtOfModuleName(currentReviewModuleName);
                        }
                        cursor_studytable0_1.close();
                    }
                    //再查询 学习状态为1(完成学习)且复习状态为0(正在复习)的模块
                    if(null == currentReviewModuleName || currentReviewModuleName.equals("") || !isFileExist(currentReviewModuleName,2)){
                        Cursor cursor_studytable10 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                                " where LearningState=? and ReviewingState=? limit 1",new String[]{"1","0"});
                        if(cursor_studytable10.getCount() == 1){
                            while(cursor_studytable10.moveToNext()){
                                currentReviewModuleName = cursor_studytable10.getString(0);
                            }
                            //先删除文件（查找到了就删除）
                            deleteFile(currentReviewModuleName,2);
                            //重新创建文件
                            CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(getContext());
                            createModuleTxtUtil.createReviewTxtOfModuleName(currentReviewModuleName);
                        }
                        cursor_studytable10.close();
                    }
                    //最后查询 学习状态为1(完成学习)且复习状态为-1(未复习)的模块
                    if(null == currentReviewModuleName || currentReviewModuleName.equals("") || !isFileExist(currentReviewModuleName,2)){
                        Cursor cursor_studytable1_1 = mydatabase.rawQuery("select LearningModule from "+MyDBHelper.Table_LearningPlan_NANE+
                                " where LearningState=? and ReviewingState=? limit 1",new String[]{"1","-1"});
                        if(cursor_studytable1_1.getCount() == 1){
                            while(cursor_studytable1_1.moveToNext()){
                                currentReviewModuleName = cursor_studytable1_1.getString(0);
                            }
                            //先删除文件（查找到了就删除）
                            deleteFile(currentReviewModuleName,2);
                            //重新创建文件
                            CreateModuleTxtUtil createModuleTxtUtil = new CreateModuleTxtUtil(getContext());
                            createModuleTxtUtil.createReviewTxtOfModuleName(currentReviewModuleName);
                        }
                        cursor_studytable1_1.close();
                    }
                    Intent intent2 = new Intent(getActivity(),ReviewActivity.class);
                    //传递要复习的模块
                    //有文件的时候才传递
                    if(isFileExist(currentReviewModuleName,2)){
                        intent2.putExtra("ModuleName",currentReviewModuleName);
                    }
                    startActivity(intent2);
                    break;
                case R.id.home_StudySurvey://学习概况
                    //点击“学习概况”查看所有学习txt文件的学习概况
                    Intent intent3 = new Intent(getActivity(),StudySurveyActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.home_TestWords://单词测试
                    Intent intent4 = new Intent(getActivity(),TestWordsActivity.class);
                    startActivity(intent4);
                    break;
            }
        }
    }
    //根据文件名判断 data目录 下 files目录 中 文件 是否存在
    private boolean isFileExist(String ModuleName,int Type){//Type = 1 表示创建学习txt  Type = 2 表示创建复习txt
        String FileName = "";
        if(Type == 1){
            FileName = ModuleName + ".txt";
        }else if(Type == 2){
            FileName = "review"+ModuleName + ".txt";
        }
        //file目录
        File directory = getContext().getFilesDir();
        if ("" != ModuleName && null != ModuleName && directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if(FileName.equals(item.getName())){
                    return true;
                }
            }
        }
        return false;
    }
    //根据文件名(若文件存在)删除 data目录 下 files目录 中 文件
    private void deleteFile(String ModuleName,int Type){//Type = 1 表示创建学习txt  Type = 2 表示创建复习txt
        String FileName = "";
        if(Type == 1){
            FileName = ModuleName + ".txt";
        }else if(Type == 2){
            FileName = "review"+ModuleName + ".txt";
        }
        //file目录
        File directory = getContext().getFilesDir();
        if ("" != ModuleName && null != ModuleName && directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if(FileName.equals(item.getName())){
                    item.delete();
                    return;
                }
            }
        }
    }
}