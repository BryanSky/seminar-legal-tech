package de.legaltech.seminar.entities;

import java.util.ArrayList;

public class ClassificationResult {
    private int id;
    private String fileName;
    private int year;
    private ArrayList<NamedEntity> recognizedEntities = new ArrayList<NamedEntity>();
    private ArrayList<Tag> documentTags = new ArrayList<Tag>();
    private String classifier;

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

    public ArrayList<NamedEntity> getNamedEntities(){
        return recognizedEntities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTags() {
        String tags = "";
        for (Tag t : documentTags) {
            tags += t + ";";
        }
        return tags;
    }

    public String getClassifier() {
        return this.classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }
}
