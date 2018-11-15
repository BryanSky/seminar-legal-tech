package de.legaltech.seminar;

import de.legaltech.seminar.entities.NamedEntity;
import de.legaltech.seminar.entities.Tag;

import java.util.ArrayList;

public class ClassificationResult {
    private String fileName;
    private int year;
    private ArrayList<NamedEntity> recognizedEntities = new ArrayList<NamedEntity>();
    private ArrayList<Tag> documentTags = new ArrayList<Tag>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addEntity(NamedEntity entity){
        recognizedEntities.add(entity);
    }

    public boolean hasEntity(NamedEntity entity){
        return recognizedEntities.contains(entity);
    }

    public void addTag(Tag tag){
        documentTags.add(tag);
    }

    public boolean hasTag(Tag tag){
        return documentTags.contains(tag);
    }
}
