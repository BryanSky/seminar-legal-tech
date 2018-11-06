package de.legaltech.seminar;

import de.legaltech.seminar.entities.LegalFile;

public class DbManager {

    protected static DbManager db;

    private DbManager(){

    }

    public static DbManager Instance(){
        if(db == null){
            db = new DbManager();
        }
        return db;
    }

    public void saveResult(LegalFile legalFile, ClassificationResult classificationResult) {

    }
}
