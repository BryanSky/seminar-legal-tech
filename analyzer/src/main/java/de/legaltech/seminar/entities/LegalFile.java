package de.legaltech.seminar.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class LegalFile {
    private String filename;
    private ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
    private boolean isProcessed;

    private String content;

    public LegalFile(String filename, String content) {
        try {
            setFilename(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.content = content;
        String[] splittedParagraphs = content.split("\n");
        for (String s : splittedParagraphs) {
            Paragraph p = new Paragraph(s);
            paragraphs.add(p);
        }
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) throws FileNotFoundException {
        if((new File(filename)).exists()){
            this.filename = filename;
        }else{
            throw new FileNotFoundException();
        }
    }

    public ArrayList<Paragraph> getParagraphs() {
        return paragraphs;
    }
}
