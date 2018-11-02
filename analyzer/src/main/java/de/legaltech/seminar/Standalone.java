package de.legaltech.seminar;

import de.legaltech.seminar.classifier.DoubleTranslationClassifier;
import de.legaltech.seminar.classifier.HeuristicClassifier;
import de.legaltech.seminar.classifier.StanfordCustomClassifier;
import de.legaltech.seminar.entities.LegalFile;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Standalone {

    private static ArrayList<IClassifier> classifiers = new ArrayList<IClassifier>();

    //args[1] is a comma separated list of filenames to process or a folder
    public static void main(String[] args){
        initialize(args);
    }

    public static void initialize(String[] args){
        //Default classifiers
        if(args.length==3){
            setupClassifiers(args[2]);
        }
        classifiers.add(new HeuristicClassifier());
        classifiers.add(new DoubleTranslationClassifier());
        classifiers.add(new StanfordCustomClassifier());
        if((new File(args[1])).isDirectory()){
            processFilesInDirectory(args[1]);
        }else{
            String[] allFiles = args[1].split(";");
            processAllFiles(allFiles);
        }
    }

    private static void setupClassifiers(String arg) {
        String[] cfs = arg.split(";");
        //load classifiers and add them to list
    }

    private static void processAllFiles(String[] allFiles) {
        for (String file : allFiles) {
            try {
                LegalFile legalFile = loadFile(file);
                classifyFile(legalFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private static void processFilesInDirectory(String arg) {
        File[] allFiles = (new File(arg)).listFiles();
        String[] allFileNames = new String[allFiles.length];
        for(int i=0; i<allFileNames.length; i++){
            allFileNames[i] = allFiles[i].getAbsolutePath();
        }
        processAllFiles(allFileNames);
    }

    public static LegalFile loadFile(String filename) throws IOException, BadLocationException {
        JEditorPane p = new JEditorPane();
        p.setContentType("text/rtf");
        EditorKit rtfKit = p.getEditorKitForContentType("text/rtf");
        rtfKit.read(new FileReader(filename), p.getDocument(), 0);
        String content = p.getText();
        return new LegalFile(content);
    }

    //optional if downloader triggers start of analyser
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
