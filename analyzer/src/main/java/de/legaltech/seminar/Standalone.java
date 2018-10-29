package de.legaltech.seminar;

import de.legaltech.seminar.classifier.DoubleTranslationClassifier;
import de.legaltech.seminar.classifier.HeuristicClassifier;
import de.legaltech.seminar.classifier.StanfordCustomClassifier;
import de.legaltech.seminar.entities.LegalFile;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Standalone {

    private static ArrayList<IClassifier> classifiers = new ArrayList<IClassifier>();

    public static void main(String[] args){
        initialize(args);
    }

    public static void initialize(String[] args){
        classifiers.add(new HeuristicClassifier());
        classifiers.add(new DoubleTranslationClassifier());
        classifiers.add(new StanfordCustomClassifier());
        //setup list of desired classifiers
        //load filenames
    }

    private static LegalFile loadFile(String filename) throws IOException, BadLocationException {
        JEditorPane p = new JEditorPane();
        p.setContentType("text/rtf");
        EditorKit rtfKit = p.getEditorKitForContentType("text/rtf");
        rtfKit.read(new FileReader(filename), p.getDocument(), 0);
        String content = p.getText();
        return new LegalFile(content);
    }

    private static LegalFile loadDbEntry(String filename){
        MongoDbManager db = MongoDbManager.Instance();

        return new LegalFile();
    }

    private static void classifyFile(LegalFile file){
        for (IClassifier classifier : classifiers) {
            classifier.processFile(file);
        }
    }
}
