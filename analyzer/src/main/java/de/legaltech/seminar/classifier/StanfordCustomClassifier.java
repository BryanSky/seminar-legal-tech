package de.legaltech.seminar.classifier;

import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;

public class StanfordCustomClassifier extends AbstractClassifier {

    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        ClassificationResult classificationResult = StanfordLibHelper.classify(StanfordLibHelper.CUSTOM_CLASSIFIER_GERMAN, file);
        return classificationResult;
    }
}
