package de.legaltech.seminar.entities;

import java.util.ArrayList;

public class LegalFile {
    private String filename;
    private ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
    private boolean isProcessed;

    public LegalFile(String content) {

    }

    public LegalFile(){

    }

    public ArrayList<NamedEntity> getNamedentities(){
        return new ArrayList<NamedEntity>();
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }
}
