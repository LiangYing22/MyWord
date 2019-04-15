package com.example.lying.myword;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.lying.myword.util.File_read_write;
import com.example.lying.myword.util.MyToast;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestWordsActivity extends AppCompatActivity implements View.OnClickListener{

    //控件
    private TextView WordTestTitle,WordTestEditText,WordTestConfirm;
    private TextView WordTestChineseMean,WordTestSeeResults,WordTestNext;

    //保存学习完成和正在学习的模块名
    private List<Survey> ModuleStudyList = new ArrayList<>();
    private List<String> ModuleNameList = new ArrayList<>();
    //当前测试的模块List中的位置
    private int position = -1;
    //当前测试的模块名
    private String ModuleName = "";
    //储存当前模块对应txt文件的所有数据对象
    private Survey CurrentSurvey ;
    //当前模块已学单词
    private List<Word> hadStudyWordList = new ArrayList<>();
    //已学单词List中的指针位置
    int hasStudyPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_words);

        //绑定控件
        WordTestTitle = (TextView)findViewById(R.id.WordTestTitle);//标题
        WordTestEditText = (TextView)findViewById(R.id.WordTestEditText);//输入
        WordTestConfirm = (TextView)findViewById(R.id.WordTestConfirm);//确认
        WordTestChineseMean = (TextView)findViewById(R.id.WordTestChineseMean);//中文含义
        WordTestSeeResults = (TextView)findViewById(R.id.WordTestSeeResults);//查看成绩
        WordTestNext = (TextView)findViewById(R.id.WordTestNext);//下一个

        //初始化数据（要测试的单词）
        initData();

        //响应事件
        WordTestTitle.setOnClickListener(this);
        WordTestConfirm.setOnClickListener(this);
        WordTestEditText.addTextChangedListener(new myTextChangedListener());
        WordTestNext.setOnClickListener(this);
        WordTestSeeResults.setOnClickListener(this);
    }

    //数据源（全部的学习txt文件）
    public void initData(){
        //file目录
        File directory = getFilesDir();
        if (directory != null && directory.exists() && directory.isDirectory()){
            for (File item : directory.listFiles()) {
                if(item.getName().contains(".txt") && !item.getName().contains("review")){
                    File_read_write file_read_write = new File_read_write(this,item.getName());
                    ModuleStudyList.add(file_read_write.getSurvey());
                    ModuleNameList.add(item.getName().substring(0,item.getName().length()-4));
                }
            }
        }
    }

    //根据Survey对象初始化当前模块已学单词
    private void initHadStudyWord(){
        if(hadStudyWordList != null && hadStudyWordList.size() > 0){
            hadStudyWordList.clear();
        }
        for(int i =0;i<CurrentSurvey.getMaster().size();i++){
            Word word = CurrentSurvey.getMaster().get(i);
            word.setMyInputWordSpell("");
            word.setIsTestRight(-1);
            hadStudyWordList.add(word);
        }
        for(int i = 0;i<CurrentSurvey.getReviewing().size();i++){
            Word word = CurrentSurvey.getReviewing().get(i);
            word.setMyInputWordSpell("");
            word.setIsTestRight(-1);
            hadStudyWordList.add(word);
        }
    }

    //更换测试模块后刷新显示
    private void flashData(){
        if(ModuleName != null && !ModuleName.equals("") && CurrentSurvey != null ){
            WordTestTitle.setText("'"+ModuleName+"'"+"模块测试");
            initHadStudyWord();
            if(hadStudyWordList != null && !hadStudyWordList.isEmpty()){
                hasStudyPosition = 0;
                WordTestChineseMean.setText(hadStudyWordList.get(hasStudyPosition).getChineseMean());
            }else{
                WordTestChineseMean.setText("该模块尚未开始学习");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.WordTestTitle://标题
                StudyModulePop studyModulePop = new StudyModulePop(this,ModuleNameList);
                studyModulePop.setListener(new StudyModulePop.PopItemClickListener() {
                    @Override
                    public void onItemClickListener(PopupWindow popupWindow, List<String> myItems, int position) {
                        TestWordsActivity.this.position = position;
                        TestWordsActivity.this.ModuleName = myItems.get(position);
                        if(ModuleStudyList != null && !ModuleStudyList.isEmpty()){
                            if(ModuleStudyList.get(position).getModuleName().equals(ModuleName)){
                                CurrentSurvey = ModuleStudyList.get(position);
                            }else {
                                for(int i =0;i<ModuleStudyList.size();i++){
                                    if(ModuleStudyList.get(i).getModuleName().equals(ModuleName)){
                                        CurrentSurvey = ModuleStudyList.get(i);
                                    }
                                }
                            }
                        }
                        flashData();
                        if(hadStudyWordList == null || hadStudyWordList.isEmpty()){
                            MyToast.getToast(TestWordsActivity.this,"该模块未开始学习");
                        }else{
                            MyToast.getToast(TestWordsActivity.this,"已选择"+myItems.get(position)+"模块");
                        }
                        popupWindow.dismiss();
                    }
                });
                studyModulePop.show(WordTestTitle);
                break;
            case R.id.WordTestConfirm://确定
                if(hadStudyWordList != null && hadStudyWordList.size() > 0){
                    String input = WordTestEditText.getText().toString().trim();
                    if(input.equals(hadStudyWordList.get(hasStudyPosition).getWordSpell())){
                        WordTestEditText.setTextColor(Color.rgb(138,214,246));
                    }else{
                        WordTestEditText.setTextColor(Color.rgb(247,82,110));
                    }
                }
                break;
            case R.id.WordTestNext://下一个
                //不为空时操作
                if(hadStudyWordList != null && !hadStudyWordList.isEmpty()){
                    int maxHadStudyPosition = hadStudyWordList.size()-1;
                    if(hasStudyPosition < maxHadStudyPosition){
                        String input = WordTestEditText.getText().toString().trim();
                        if(input.equals(hadStudyWordList.get(hasStudyPosition).getWordSpell())){
                            hadStudyWordList.get(hasStudyPosition).setIsTestRight(1);
                            hadStudyWordList.get(hasStudyPosition).setMyInputWordSpell(input);
                        }else{
                            hadStudyWordList.get(hasStudyPosition).setIsTestRight(0);
                            hadStudyWordList.get(hasStudyPosition).setMyInputWordSpell(input);
                        }
                        //清空输入
                        WordTestEditText.setText("");
                        hasStudyPosition++;
                        WordTestChineseMean.setText(hadStudyWordList.get(hasStudyPosition).getChineseMean());
                    }else if(hasStudyPosition == maxHadStudyPosition){
                        new AlertDialog.Builder(this)
                                .setTitle("提示").setMessage("这是最后一个单词了！快去查看成绩吧！")
                                .setNegativeButton("取消",null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String input = WordTestEditText.getText().toString().trim();
                                        if(input.equals(hadStudyWordList.get(hasStudyPosition).getWordSpell())){
                                            hadStudyWordList.get(hasStudyPosition).setIsTestRight(1);
                                            hadStudyWordList.get(hasStudyPosition).setMyInputWordSpell(input);
                                        }else{
                                            hadStudyWordList.get(hasStudyPosition).setIsTestRight(0);
                                            hadStudyWordList.get(hasStudyPosition).setMyInputWordSpell(input);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
            case R.id.WordTestSeeResults:
                //发送事件
                EventBus.getDefault().postSticky(new WordListEvent(ModuleName,hadStudyWordList));
                Intent intent = new Intent(TestWordsActivity.this,TestResultActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class myTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            WordTestEditText.setTextColor(Color.rgb(0,0,0));
        }
    }

}
