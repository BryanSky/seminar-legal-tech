package de.legaltech.seminar.entities;

import de.legaltech.seminar.IClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LegalFile {

    private int id;
    private String filename;
    private String filePath;
    private String fullFileName;
    private String fileOnlyNameBase;
    private String fileOnlyNameTagged;
    private String fileOnlyNameTranslated;
    private ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
    private boolean isProcessed;
    private boolean isSaved = false;

    private String content;
    private String taggedContent;
    private String translatedContent;

    private MetaData metaData;

    public Map<IClassifier, ClassificationResult> classificationResultMap = new HashMap<>();

    public LegalFile(String content, String filename) {
        setFileNames(filename);
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

    private void setFileNames(String filename){
        setFilePath(filename.substring(0, filename.lastIndexOf("\\")+1));
        setFileOnlyNameBase(filename.substring(filename.lastIndexOf("\\")+1));
        setFileOnlyNameTagged(getFileOnlyNameBase().replace(".rtf", "_TAGGED.rtf"));
        setFileOnlyNameTranslated(getFileOnlyNameBase().replace(".rtf", "_EN.rtf"));
    }

    public ArrayList<NamedEntity> getNamedEntities(){
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFullFileName() {
        return fullFileName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public String getFileOnlyNameBase() {
        return fileOnlyNameBase;
    }

    public void setFileOnlyNameBase(String fileOnlyNameBase) {
        this.fileOnlyNameBase = fileOnlyNameBase;
    }

    public String getFileOnlyNameTagged() {
        return fileOnlyNameTagged;
    }

    public void setFileOnlyNameTagged(String fileOnlyNameTagged) {
        this.fileOnlyNameTagged = fileOnlyNameTagged;
    }

    public String getFileOnlyNameTranslated() {
        return fileOnlyNameTranslated;
    }

    public void setFileOnlyNameTranslated(String fileOnlyNameTranslated) {
        this.fileOnlyNameTranslated = fileOnlyNameTranslated;
    }

    public String getTaggedContent() {
        return taggedContent;
    }

    public void setTaggedContent(String taggedContent) {
        this.taggedContent = taggedContent;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        if(this.metaData != null){
            mergeMetaData(metaData);
        }else{
            this.metaData = metaData;
        }
    }

    //only needed in case that more than one preprocessor in used
    private void mergeMetaData(MetaData metaData) {
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getTranslatedContent() {
        return translatedContent;
    }

    public void setTranslatedContent(String translatedContent) {
        this.translatedContent = translatedContent;
    }
}
