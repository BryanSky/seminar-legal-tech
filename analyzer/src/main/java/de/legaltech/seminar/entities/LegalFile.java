package de.legaltech.seminar.entities;

import java.util.ArrayList;

public class LegalFile {
    private String filename;
    private ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();

    public LegalFile(String content) {

    }

    public LegalFile(){

    }

    public ArrayList<NamedEntity> getNamedentities(){
        return new ArrayList<NamedEntity>();
    }
}
