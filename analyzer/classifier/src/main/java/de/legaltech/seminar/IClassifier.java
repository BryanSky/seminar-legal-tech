package de.legaltech.seminar;

import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;

public interface IClassifier {
    ClassificationResult processFile(LegalFile file, boolean training, boolean test);
    void saveTaggedFile(LegalFile legalFile, String filename);
    void compareTaggedWithManuallyTagged(String filename);
}
