package de.legaltech.seminar.classifier;

import de.legaltech.seminar.ClassificationResult;
import de.legaltech.seminar.IClassifier;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.Paragraph;
import de.legaltech.seminar.entities.Sentence;

public class HeuristicClassifier implements IClassifier {

    public void processFile(LegalFile file) {
        for (Paragraph paragraph : file.getParagraphs()) {
            processParagraph(paragraph);
        }
    }

    private void processParagraph(Paragraph paragraph) {
        for(Sentence sentence : paragraph.getSentences()){
            processSentence(sentence);
        }
    }

    private void processSentence(Sentence sentence) {

    }

    public void saveResult(LegalFile legalFile, ClassificationResult classificationResult) {

    }

    //use outcomes of different papers to classify a file
}
