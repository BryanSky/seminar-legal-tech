package de.legaltech.seminar.entities;

import java.util.ArrayList;

public class Paragraph {

    private String content;
    private ArrayList<Sentence> sentences = new ArrayList<Sentence>();

    public Paragraph(String s) {
        this.content = s;
        String[] splittedSentences = s.split(".");
        for (String str : splittedSentences) {
            Sentence sentence = new Sentence(str);
            sentences.add(sentence);
        }
    }

    public ArrayList<Sentence> getSentences() {
        return sentences;
    }
}
