package de.legaltech.seminar.entities;

public class Tag {

    private String tagType;

    public Tag(String tag) {
        setTagType(tag);
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }
}
