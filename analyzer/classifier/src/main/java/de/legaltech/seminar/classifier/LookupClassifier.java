package de.legaltech.seminar.classifier;

import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.ClassificationResult;

public class LookupClassifier extends AbstractClassifier {

    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        return new ClassificationResult();
    }
}
