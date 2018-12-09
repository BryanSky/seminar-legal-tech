package de.legaltech.seminar;

import de.legaltech.seminar.entities.MetaData;

public class DefaultPreprocessor implements IPreprocessor {

    public MetaData extractMetaData(String filecontent, int legalFileId) {
        MetaData meta = new MetaData();
        meta.setClaimerRequest(extractClaimerRequest(filecontent));
        meta.setTitle(extractTitle(filecontent));
        meta.setDecision(extractDecision(filecontent));
        meta.setDefendantRequest(extractDefendantRequest(filecontent));
        meta.setLegalFileId(legalFileId);
        meta.setMatterOfFact(extractMatterOfFact(filecontent));
        return meta;
    }

    private String extractMatterOfFact(String filecontent) {
        filecontent.indexOf("");
        return null;
    }

    private String extractDefendantRequest(String filecontent) {
        return null;
    }

    private String extractTitle(String filecontent){
        return null;
    }

    private String extractDecision(String filecontent) {
        return null;
    }

    private String extractClaimerRequest(String filecontent) {
        return null;
    }
}
