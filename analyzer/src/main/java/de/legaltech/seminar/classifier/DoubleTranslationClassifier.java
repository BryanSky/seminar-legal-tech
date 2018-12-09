package de.legaltech.seminar.classifier;

import de.legaltech.seminar.DocumentTranslatorLibHelper;
import de.legaltech.seminar.FileManager;
import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.constants.AnalyserConstant;
import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;

import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_DE;
import static de.legaltech.seminar.DocumentTranslatorLibHelper.LANGUAGE_EN;

public class DoubleTranslationClassifier extends AbstractClassifier {

    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        translateFile(file, LANGUAGE_DE, LANGUAGE_EN);
        FileManager.WriteToFile(file.getTranslatedContent(),AnalyserConstant.fileBlobProcessed +
                file.getFileOnlyNameTranslatedEN());
        ClassificationResult res1 = StanfordLibHelper.classifyTranslation(StanfordLibHelper.STANDARD_CLASSIFIER_ENGLISH, file);
        FileManager.WriteToFile(file.getTranslatedTaggedContent(),AnalyserConstant.fileBlobProcessed +
                file.getFileOnlyNameTagged().replace(".rtf", ".txt"));
        res1.setClassifier(this.getClass().getName());
        if(test){
            return res1;
        }
        translateTaggedFile(file, LANGUAGE_EN, LANGUAGE_DE);
        FileManager.WriteToFile(file.getTaggedContent(),file.getFilePath() + "translated/" + file.getFileOnlyNameBacktranslatedDE());
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
