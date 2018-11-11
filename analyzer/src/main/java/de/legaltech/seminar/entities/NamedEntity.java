package de.legaltech.seminar.entities;

public class NamedEntity {

    private Tag tag;
    private String value;
    private int startInText;
    private int endInText;

    public Tag getTag() {
        return tag;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTag(String tag) {
        this.tag = new Tag(tag);
    }

    public int getStartInText() {
        return startInText;
    }

    public void setStartInText(int startInText) {
        this.startInText = startInText;
    }

    public int getEndInText() {
        return endInText;
    }

    public void setEndInText(int endInText) {
        this.endInText = endInText;
    }

    public String getValue() {
        return value;
    }
}
