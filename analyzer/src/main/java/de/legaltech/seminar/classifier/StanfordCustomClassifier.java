package de.legaltech.seminar.classifier;

import de.legaltech.seminar.ClassificationResult;
import de.legaltech.seminar.DbManager;
import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.entities.LegalFile;

import java.io.IOException;
import java.sql.SQLException;

public class StanfordCustomClassifier extends AbstractClassifier {

    public void processFile(LegalFile file) {
        ClassificationResult classificationResult = StanfordLibHelper.classify(StanfordLibHelper.CUSTOM_CLASSIFIER_GERMAN, file);
        saveResult(file, classificationResult);
    }

    public void saveResult(LegalFile legalFile, ClassificationResult classificationResult) {
        try {
            DbManager.Instance().saveResult(legalFile, classificationResult);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
