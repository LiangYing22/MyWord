package com.example.lying.myword;

import java.util.List;

public class Survey {

    private String ModuleName = "";//模块名
    private int unLearningNum = -1;//未学习数量
    private int reviewingNum = -1;//需复习数量
    private int masterNum = -1;//已掌握数量
    private List<Word> unLearning = null;//未学习的单词
    private List<Word> reviewing = null;//需复习的单词
    private List<Word> master = null;//已掌握单词

    public Survey(){}

    public Survey(String ModuleName){
        this.ModuleName = ModuleName;

    }

    public int getMasterNum() {
        return masterNum;
    }

    public int getReviewingNum() {
        return reviewingNum;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public int getUnLearningNum() {
        return unLearningNum;
    }

    public List<Word> getMaster() {
        return master;
    }

    public List<Word> getReviewing() {
        return reviewing;
    }

    public List<Word> getUnLearning() {
        return unLearning;
    }

    public void setMaster(List<Word> master) {
        this.master = master;
    }

    public void setMasterNum(int masterNum) {
        this.masterNum = masterNum;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }

    public void setReviewing(List<Word> reviewing) {
        this.reviewing = reviewing;
    }

    public void setReviewingNum(int reviewingNum) {
        this.reviewingNum = reviewingNum;
    }

    public void setUnLearning(List<Word> unLearning) {
        this.unLearning = unLearning;
    }

    public void setUnLearningNum(int unLearningNum) {
        this.unLearningNum = unLearningNum;
    }

}
