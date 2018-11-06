package de.legaltech.seminar.classifier;

import de.legaltech.seminar.*;
import de.legaltech.seminar.entities.LegalFile;

import javax.swing.text.BadLocationException;
import java.io.IOException;

import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_DE;
import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_EN;

public class DoubleTranslationClassifier implements IClassifier {

    public void processFile(LegalFile file) {
        translateFile(file, LANGUAGE_DE, LANGUAGE_EN);
        try {
            LegalFile legalFileTranslated = Standalone.loadFile("translatedFilename");
            ClassificationResult classificationResult = StanfordLibHelper.classify(StanfordLibHelper.STANDARD_CLASSIFIER_ENGLISH, legalFileTranslated);
            saveResult(legalFileTranslated, classificationResult);
            //backtranslate
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void saveResult(LegalFile legalFile, ClassificationResult classificationResult) {

    }

    private boolean translateFile(LegalFile legalFile, String sourceLanguage, String targetLanguage){
        String filename = legalFile.getFilename();
        String translatedFilename = filename + "_en";
        return DocumentTranslatorLibHelper.translate(filename, translatedFilename, sourceLanguage, targetLanguage);
    }
}
