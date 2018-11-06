package de.legaltech.seminar.classifier;

import de.legaltech.seminar.ClassificationResult;
import de.legaltech.seminar.DbManager;
import de.legaltech.seminar.IClassifier;
import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.entities.LegalFile;

public class StanfordCustomClassifier implements IClassifier {

    public void processFile(LegalFile file) {
        ClassificationResult classificationResult = StanfordLibHelper.classify(StanfordLibHelper.CUSTOM_CLASSIFIER_GERMAN, file);
        saveResult(file, classificationResult);
    }

    public void saveResult(LegalFile legalFile, ClassificationResult classificationResult) {
        DbManager.Instance().saveResult(legalFile, classificationResult);
    }


}