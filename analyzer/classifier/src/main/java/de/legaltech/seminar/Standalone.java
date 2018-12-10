package de.legaltech.seminar;

import de.legaltech.seminar.classifier.DoubleTranslationClassifier;
import de.legaltech.seminar.classifier.HeuristicClassifier;
import de.legaltech.seminar.classifier.StanfordCustomClassifier;
import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.MetaData;
import de.legaltech.seminar.constants.AnalyserConstant;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Standalone {

    private static ArrayList<IClassifier> classifiers = new ArrayList<IClassifier>();
    private static ArrayList<IPreprocessor> preprocessors = new ArrayList<IPreprocessor>();
    private static boolean isTrainingCase = true;
    private static boolean isTestingCase = false;

    //args[1] is a comma separated list of filenames to process or a folder
    public static void main(String[] args){
        initialize(args);
    }

    public static void initialize(String[] args){
        if(args.length==0){
            args = new String[]{AnalyserConstant.fileBlobUnprocessed};
        }
        if(args.length==2){
            setupClassifiers(args[1]);
        }
        classifiers.add(new HeuristicClassifier());
        classifiers.add(new DoubleTranslationClassifier());
        classifiers.add(new StanfordCustomClassifier());
        preprocessors.add(new DefaultPreprocessor());
        if((new File(args[0])).isDirectory()){
            processFilesInDirectory(args[0]);
        }else{
            String[] allFiles = args[0].split(";");
            processAllFiles(allFiles);
        }
    }

    private static void setupClassifiers(String arg) {
        String[] cfs = arg.split(";");

    }

    private static void processAllFiles(String[] allFiles) {
        for (String file : allFiles) {
            try {
                LegalFile legalFile = loadFile(file);
                preprocess(legalFile);
                classifyFile(legalFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private static void preprocess(LegalFile legalFile) {
        for (IPreprocessor processor : preprocessors) {
            MetaData meta = processor.extractMetaData(legalFile.getContent(), legalFile.getId());
            legalFile.setMetaData(meta);
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
        rtfKit.read(new FileReader(filename), p.getDocument(), 1);
        String content = p.getDocument().getText(0, p.getDocument().getLength());
        return new LegalFile(content, filename);
    }

    private static void classifyFile(LegalFile file){
        for (IClassifier classifier : classifiers) {
            ClassificationResult result = classifier.processFile(file, isTrainingCase, isTestingCase);
            result.setClassifier(classifier.getClass().getName());
            file.classificationResultMap.put(classifier, result);
            FileManager.WriteToFile(file.getTaggedContent(), AnalyserConstant.fileBlobProcessed + file.getFileOnlyNameTagged()
                    .replace(".txt", "_" +
                            classifier.getClass().getSimpleName() + ".txt"));
            if(isTrainingCase || isTestingCase){
                classifier.compareTaggedWithManuallyTagged(file.getFilename());
            }
        }
        //DbManager.Instance().saveClassifiedFile(file);
    }
}
