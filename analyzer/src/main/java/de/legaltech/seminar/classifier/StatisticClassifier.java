package de.legaltech.seminar.classifier;

import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;

public class StatisticClassifier extends AbstractClassifier {
    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        return new ClassificationResult();
    }
}
