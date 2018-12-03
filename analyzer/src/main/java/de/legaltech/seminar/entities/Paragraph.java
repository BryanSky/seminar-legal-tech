package de.legaltech.seminar.entities;

import java.util.ArrayList;

public class Paragraph {

    private String content;
    private ArrayList<Sentence> sentences = new ArrayList<Sentence>();

    public Paragraph(String s) {
        this.content = s;
        sentences.add(new Sentence(s));
//        String[] splittedSentences = s.split(Pattern.quote("."));
//        for (String str : splittedSentences) {
//            Sentence sentence = new Sentence(str);
//            sentences.add(sentence);
//        }
    }

    public ArrayList<Sentence> getSentences() {
        return sentences;
    }
}
