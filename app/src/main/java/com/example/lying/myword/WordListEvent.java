package com.example.lying.myword;

import java.util.List;

public class WordListEvent {
    private List<Word> WordData;
    private String ModuleName;

    public WordListEvent(String ModuleName,List<Word> WordData){
        this.ModuleName = ModuleName;
        this.WordData = WordData;
    }

    public List<Word> getWordData() {
        return WordData;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setWordData(List<Word> wordData) {
        WordData = wordData;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }
}
