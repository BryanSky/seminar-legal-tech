package de.legaltech.seminar.classifier;

import de.legaltech.seminar.DocumentTranslatorLibHelper;
import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;

import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_DE;
import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_EN;

public class DoubleTranslationClassifier extends AbstractClassifier {

    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        ClassificationResult res = null;
        translateFile(file, LANGUAGE_DE, LANGUAGE_EN);
        LegalFile legalFileTranslated = new LegalFile(file.getTranslatedContent(), file.getFilePath() + file.getFileOnlyNameTranslated());
        StanfordLibHelper.classify(StanfordLibHelper.STANDARD_CLASSIFIER_ENGLISH, legalFileTranslated);
        translateFile(legalFileTranslated, LANGUAGE_EN, LANGUAGE_DE);
        file.setTaggedContent(legalFileTranslated.getTranslatedContent());
        res = StanfordLibHelper.buildClassificationResult(file.getTaggedContent());
        return res;
    }

    private boolean translateFile(LegalFile legalFile, String sourceLanguage, String targetLanguage){
        return DocumentTranslatorLibHelper.translate(legalFile, sourceLanguage, targetLanguage);
    }
}
