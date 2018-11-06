package de.legaltech.seminar;

import de.legaltech.seminar.entities.LegalFile;

public interface IClassifier {
    void processFile(LegalFile file);
    void saveResult(LegalFile legalFile, ClassificationResult classificationResult);
}
