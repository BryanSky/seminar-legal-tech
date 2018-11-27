package de.legaltech.seminar.classifier;

import de.legaltech.seminar.IClassifier;
import de.legaltech.seminar.entities.LegalFile;

public abstract class AbstractClassifier implements IClassifier {

    public void saveTaggedFile(LegalFile legalFile, String filename){
        //build content from LegalFile
    }

    public void compareTaggedWithManuallyTagged(String filename){

    }
}
