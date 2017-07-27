package com.readboy.online.model.data;

/**
 * Created by liujiawei on 16-6-28.
 */
public class HotWord {

    private int wordId;
    private String text;

    public HotWord(int wordId, String text){
        this.wordId = wordId;
        this.text = text;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
