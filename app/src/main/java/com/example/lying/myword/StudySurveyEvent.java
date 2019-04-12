package com.example.lying.myword;

import java.util.List;

public class StudySurveyEvent {
    private String ModuleName;
    private List<Word> WordData;
    private int Type = 0;

    public StudySurveyEvent(String ModuleName,List<Word> WordData,int Type){
        this.ModuleName = ModuleName;
        this.WordData = WordData;
        this.Type = Type;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public List<Word> getWordData() {
        return WordData;
    }

    public int getType() {
        return Type;
    }
}
