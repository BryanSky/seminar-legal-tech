package de.legaltech.seminar.classifier;

import de.legaltech.seminar.DocumentTranslatorLibHelper;
import de.legaltech.seminar.FileManager;
import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.constants.AnalyserConstant;
import de.legaltech.seminar.entities.ClassificationResult;

public class DoubleTranslationClassifier extends AbstractClassifier {

    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        translateFile(file, DocumentTranslatorLibHelper.LANGUAGE_DE, DocumentTranslatorLibHelper.LANGUAGE_EN);
        FileManager.WriteToFile(file.getTranslatedContent(),AnalyserConstant.fileBlobProcessed +
                file.getFileOnlyNameTranslatedEN());
        ClassificationResult res1 = StanfordLibHelper.classifyTranslation(StanfordLibHelper.STANDARD_CLASSIFIER_ENGLISH, file);
        FileManager.WriteToFile(file.getTranslatedTaggedContent(),AnalyserConstant.fileBlobProcessed +
                file.getFileOnlyNameTagged().replace(".rtf", ".txt"));
        res1.setClassifier(this.getClass().getName());
        if(test){
            return res1;
        }
        translateTaggedFile(file, DocumentTranslatorLibHelper.LANGUAGE_EN, DocumentTranslatorLibHelper.LANGUAGE_DE);
        FileManager.WriteToFile(file.getTaggedContent(),AnalyserConstant.fileBlobProcessed + file.getFileOnlyNameBacktranslatedDE());
        ClassificationResult res = StanfordLibHelper.buildClassificationResult(file.getTaggedContent());
        return res;
    }

    private void translateTaggedFile(LegalFile legalFile, String sourceLanguage, String targetLanguage) {
        legalFile.setTaggedContent(DocumentTranslatorLibHelper.translate(legalFile.getTranslatedTaggedContent(),
                sourceLanguage, targetLanguage));
    }

    private void translateFile(LegalFile legalFile, String sourceLanguage, String targetLanguage){
        legalFile.setTranslatedContent(DocumentTranslatorLibHelper.translate(legalFile.getContent(),
                sourceLanguage, targetLanguage));
    }
}
