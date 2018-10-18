package de.legaltech.seminar;

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
}
