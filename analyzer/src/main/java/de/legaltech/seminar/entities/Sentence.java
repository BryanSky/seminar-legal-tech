package de.legaltech.seminar.entities;

public class Sentence {

    private String content;
    private String[] words;
    int currentPosition = 0;

    public Sentence(String str) {
        setContent(str);
    }

    public boolean IsLast() {
        return currentPosition == words.length;
    }

    public String get(int position){
        if(position>=words.length)return "";
        return words[position];
    }

    public String getNext(){
        if(currentPosition>=words.length)return "";
        return words[currentPosition++];
    }

    public void moveToFirst(){
        currentPosition = 0;
    }

    public int getLength(){
        return words.length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        words = content.split(" ");
    }
}
