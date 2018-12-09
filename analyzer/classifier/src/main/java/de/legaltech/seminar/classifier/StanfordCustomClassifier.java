package de.legaltech.seminar.classifier;

import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.ClassificationResult;

public class StanfordCustomClassifier extends AbstractClassifier {

    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        ClassificationResult classificationResult = StanfordLibHelper.classifyOriginal(StanfordLibHelper.CUSTOM_CLASSIFIER_GERMAN, file);
        return classificationResult;
    }
}
