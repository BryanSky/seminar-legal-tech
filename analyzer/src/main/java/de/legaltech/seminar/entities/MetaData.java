package de.legaltech.seminar.entities;

public class MetaData {

    private int legalFileId;
    private String claimerRequest;
    private String defendantRequest;
    private String title;
    private String decision;
    private String matterOfFact;

    public int getLegalFileId() {
        return legalFileId;
    }

    public void setLegalFileId(int legalFileId) {
        this.legalFileId = legalFileId;
    }

    public String getClaimerRequest() {
        return claimerRequest;
    }

    public void setClaimerRequest(String claimerRequest) {
        this.claimerRequest = claimerRequest;
    }

    public String getDefendantRequest() {
        return defendantRequest;
    }

    public void setDefendantRequest(String defendantRequest) {
        this.defendantRequest = defendantRequest;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getMatterOfFact() {
        return matterOfFact;
    }

    public void setMatterOfFact(String matterOfFact) {
        this.matterOfFact = matterOfFact;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
