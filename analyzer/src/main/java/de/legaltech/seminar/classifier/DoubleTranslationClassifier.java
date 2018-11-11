package de.legaltech.seminar.classifier;

import de.legaltech.seminar.*;
import de.legaltech.seminar.entities.LegalFile;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.sql.SQLException;

import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_DE;
import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_EN;

public class DoubleTranslationClassifier implements IClassifier {

    public void processFile(LegalFile file) {
        translateFile(file, LANGUAGE_DE, LANGUAGE_EN);
        try {
            LegalFile legalFileTranslated = Standalone.loadFile(file.getFilename().split(".")[0] + "_en.rtf");
            ClassificationResult classificationResult = StanfordLibHelper.classify(StanfordLibHelper.STANDARD_CLASSIFIER_ENGLISH, legalFileTranslated);
            saveResult(legalFileTranslated, classificationResult);
            translateResult(file, classificationResult);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void translateResult(LegalFile file, ClassificationResult classificationResult) {

        //saveResult();
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

    private boolean translateFile(LegalFile legalFile, String sourceLanguage, String targetLanguage){
        String filename = legalFile.getFilename();
        String translatedFilename = filename.split(".")[0] + "_en.rtf";
        return DocumentTranslatorLibHelper.translate(filename, translatedFilename, sourceLanguage, targetLanguage);
    }
}
