package com.example.lying.myword;

public class Word {

    private String wordSpell;//单词
    private String phoneticSymbol;//音标
    private String chineseMean;//中文含义
    int isItemSelect = -1;//是否被选择（主要用于生词本中）
    String familiarity = "-1";//熟悉度
    String needReviewTime = "-1";//需要复习的次数
    String studyTime = "-1";//学习的时间
    String wordPosition = "-2";//单词所在txt文件中的行号
    int option = -1;//在那个选项（主要用于复习单词中）
    String wordReviewPosition = "-2";//单词所在复习txt文件中的行号
    int isTestRight = -1;//测试中，单词是否测试正确(0，不正确。1，正确。-1，未测试)
    String myInputWordSpell = "";//测试中，用户输入的单词拼写。

    public Word(String wordSpell,String phoneticSymbol,String chineseMean){
        this.wordSpell = wordSpell;
        this.phoneticSymbol = phoneticSymbol;
        this.chineseMean = chineseMean;
    }

    public Word(String wordPosition,String wordReviewPosition,String wordSpell,String chineseMean,String phoneticSymbol,String needReviewTime,String studyTime){
        this.wordSpell = wordSpell;
        this.phoneticSymbol = phoneticSymbol;
        this.chineseMean = chineseMean;
        this.needReviewTime = needReviewTime;
        this.studyTime = studyTime;
        this.wordPosition = wordPosition;
        this.wordReviewPosition = wordReviewPosition;
    }

    public void setWordSpell(String wordSpell) {
        this.wordSpell = wordSpell;
    }

    public void setPhoneticSymbol(String phoneticSymbol) {
        this.phoneticSymbol = phoneticSymbol;
    }

    public void setChineseMean(String chineseMean) {
        this.chineseMean = chineseMean;
    }

    public void setIsItemSelect(int isItemSelect) {
        this.isItemSelect = isItemSelect;
    }

    public void setFamiliarity(String familiarity) {
        this.familiarity = familiarity;
    }

    public void setNeedReviewTime(String needReviewTime) {
        this.needReviewTime = needReviewTime;
    }

    public void setStudyTime(String studyTime) {
        this.studyTime = studyTime;
    }

    public void setWordPosition(String wordPosition) {
        this.wordPosition = wordPosition;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public void setWordReviewPosition(String wordReviewPosition) {
        this.wordReviewPosition = wordReviewPosition;
    }

    public void setIsTestRight(int isTestRight) {
        this.isTestRight = isTestRight;
    }

    public void setMyInputWordSpell(String myInputWordSpell) {
        this.myInputWordSpell = myInputWordSpell;
    }

    public String getWordSpell() {
        return wordSpell;
    }

    public String getPhoneticSymbol() {
        return phoneticSymbol;
    }

    public String getChineseMean() {
        return chineseMean;
    }

    public int getIsItemSelect() {
        return isItemSelect;
    }

    public String getFamiliarity() {
        return familiarity;
    }

    public String getNeedReviewTime() {
        return needReviewTime;
    }

    public String getStudyTime() {
        return studyTime;
    }

    public String getWordPosition() {
        return wordPosition;
    }

    public int getOption() {
        return option;
    }

    public String getWordReviewPosition() {
        return wordReviewPosition;
    }

    public int getIsTestRight() {
        return isTestRight;
    }

    public String getMyInputWordSpell() {
        return myInputWordSpell;
    }
}
