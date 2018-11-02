package de.legaltech.seminar.classifier;

import de.legaltech.seminar.IClassifier;
import de.legaltech.seminar.Standalone;
import de.legaltech.seminar.entities.LegalFile;

import javax.swing.text.BadLocationException;
import java.io.IOException;

public class DoubleTranslationClassifier implements IClassifier {
    public void processFile(LegalFile file) {

    }

    private boolean translateFile(LegalFile legalFile){
        String filename = legalFile.getFilename();
        String translatedFilename = filename + "_en";
        callTranslationService(filename, translatedFilename);
        try {
            LegalFile legalFileTranslated = Standalone.loadFile(translatedFilename);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (BadLocationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void callTranslationService(String filename, String translatedFilename) {
    }
}
