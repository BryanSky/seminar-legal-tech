package de.legaltech.seminar.training;

import de.legaltech.seminar.Standalone;
import de.legaltech.seminar.StanfordLibHelper;
import de.legaltech.seminar.classifier.HeuristicClassifier;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.Paragraph;
import de.legaltech.seminar.entities.Sentence;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClassifierTrainer {

    private static String tsvDir = "C:/Users/bened/Desktop/TrainingFiles/tsv/";
    private static String propFilePath = "C:/Users/bened/Desktop/TrainingFiles/prop/german.all.3class.distsim.prop";
    private static String classifierPostfix = "german.all.3class.distsim.crf.ser.gz";

    private static String vectorResultFile = "C:/Users/bened/Desktop/TrainingFiles/result/heuristic.txt";

    public static void main(String args[]){
        trainHeuristicClassifier("C:/Users/bened/Desktop/TrainingFiles/");
    }

    private void trainCustomStanfordClassifier(){
        File f = new File(tsvDir);
        if(f.isDirectory()){
            String[] files = f.list();
            for (String file : files) {
                String fullFile = f + "/" + file;
                StanfordLibHelper.trainAndWrite(tsvDir + "../classifiers/" + file.substring(0, 2) + classifierPostfix, propFilePath, fullFile);
            }
        }
    }

    private static void trainHeuristicClassifier(String trainingFilesDir){
        File dir = new File(trainingFilesDir);
        if(dir.exists() && dir.isDirectory()){
            List<float[]> personsList = new ArrayList<>();
            List<float[]> organisationsList = new ArrayList<>();
            List<float[]> locationsList = new ArrayList<>();
            String[] allFiles = dir.list();
            for (String file : allFiles) {
                if(file.contains("TAGGED")){
                    try {
                        LegalFile lF = Standalone.loadFile(dir + "/" +file);
                        for (Paragraph p : lF.getParagraphs()) {
                            for (Sentence s : p.getSentences()) {
                                String sentence = s.getContent().replace("<PERSON>", "")
                                        .replace("</PERSON>", "")
                                        .replace("<ORGANISATION>", "")
                                        .replace("</ORGANISATION>", "")
                                        .replace("<LOCATION>", "")
                                        .replace("</LOCATION>", "");
                                Sentence cleanedSentence = new Sentence(sentence);
                                Map<Integer, String> positionMap = extractPositions(s);
                                for (int position : positionMap.keySet()) {
                                    float[] vector = HeuristicClassifier.buildProbabilityVector(cleanedSentence, position);
                                    if(positionMap.get(position).equals("PERSON")){
                                        personsList.add(vector);
                                    } else if(positionMap.get(position).equals("ORGANISATION")){
                                        organisationsList.add(vector);
                                    } else if(positionMap.get(position).equals("LOCATION")){
                                        locationsList.add(vector);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
            float[] meanPerson = calculateMean(personsList);
            float[] meanOrganisation = calculateMean(organisationsList);
            float[] meanLocation = calculateMean(locationsList);
            String[] targetVectors = new String[3];
            for (float f : meanPerson) {
                targetVectors[0] += String.valueOf(f) + "\t";
            }
            for (float f : meanOrganisation) {
                targetVectors[1] += String.valueOf(f) + "\t";
            }
            for (float f : meanLocation) {
                targetVectors[2] += String.valueOf(f) + "\t";
            }
            Path file = Paths.get(vectorResultFile);
            try {
                Files.write(file, Arrays.asList(targetVectors), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<Integer,String> extractPositions(Sentence s) {
        s.setContent(cleanString(s.getContent()));
        Map<Integer, String> positionMap = new HashMap<>();
        int cleanedCounter = 0;
        boolean inEntity = false;
        boolean updateCounter = true;
        String currentEntity = null;
        s.moveToFirst();
        while(!s.IsLast()){
            String value = s.getNext();
            if(value.startsWith("<PERSON>") && !inEntity){
                inEntity = true;
                currentEntity = "PERSON";
                if(!positionMap.containsKey(cleanedCounter))positionMap.put(cleanedCounter, currentEntity);
                updateCounter = false;
            }else if(value.startsWith("<ORGANISATION>") && !inEntity){
                inEntity = true;
                currentEntity = "ORGANISATION";
                if(!positionMap.containsKey(cleanedCounter))positionMap.put(cleanedCounter, currentEntity);
                updateCounter = false;
            }else if(value.startsWith("<LOCATION>") && !inEntity){
                inEntity = true;
                currentEntity = "LOCATION";
                if(!positionMap.containsKey(cleanedCounter))positionMap.put(cleanedCounter, currentEntity);
                updateCounter = false;
            }
            if(inEntity && currentEntity.equals("PERSON") && value.contains("</PERSON>")){
                inEntity = false;
                currentEntity = null;
                updateCounter = false;
            } else if(inEntity && currentEntity.equals("ORGANISATION") && value.contains("</ORGANISATION>")){
                inEntity = false;
                currentEntity = null;
                updateCounter = false;
            } else if(inEntity && currentEntity.equals("LOCATION") && value.contains("</LOCATION>")){
                inEntity = false;
                currentEntity = null;
                updateCounter = false;
            }
            if(updateCounter)cleanedCounter++;
            updateCounter = true;
        }
        return positionMap;
    }

    private static String cleanString(String value){
        return value.replace("<PERSON>", " <PERSON> ")
                .replace("</PERSON>", " </PERSON> ")
                .replace("<ORGANISATION>", " <ORGANISATION> ")
                .replace("</ORGANISATION>", " </ORGANISATION> ")
                .replace("<LOCATION>", " <LOCATION> ")
                .replace("</LOCATION>", " </LOCATION> ")
                .replaceAll(" +", " ");
    }

    private static float[] calculateMean(List<float[]> vectorList) {
        if(vectorList.size()==0)return new float[]{};
        int length = vectorList.get(0).length;
        float[] mean = new float[length];
        for(int i=0; i<length; i++){
            for (float[] item : vectorList) {
                mean[i] += item[i];
            }
            mean[i] /= length;
        }
        return mean;
    }
}
