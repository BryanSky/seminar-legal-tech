package de.legaltech.seminar.training;

import com.google.gson.annotations.Since;
import de.legaltech.seminar.*;
import de.legaltech.seminar.classifier.DoubleTranslationClassifier;
import de.legaltech.seminar.classifier.HeuristicClassifier;
import de.legaltech.seminar.classifier.StanfordCustomClassifier;
import de.legaltech.seminar.constants.AnalyserConstant;
import de.legaltech.seminar.entities.*;
import de.legaltech.seminar.exceptions.ItemTooLargeException;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Since(value = 1.8)
public class ClassifierTrainer {

    private static String tsvDir = "C:/Users/bened/Desktop/TrainingFiles/tsv/";
    private static String propFilePath = "C:/Users/bened/Desktop/TrainingFiles/prop/german.all.3class.distsim.prop";
    private static String classifierPostfix = "german.all.3class.distsim.crf.ser.gz";

    private static String vectorResultFile = "C:/Users/bened/Desktop/TrainingFiles/result/heuristic.txt";

    public static void main(String args[]){
        testDoubleTranslationClassifier();
        //testHeuristicClassifier();
        //testCustomStanfordClassifier();
        //trainHeuristicClassifier("C:/Users/bened/Desktop/TrainingFiles/");
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
            for (int i=0; i<targetVectors.length; i++) {
                targetVectors[i] = targetVectors[i].replace("null", "");
            }
            try {
                Files.write(file, Arrays.asList(targetVectors), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void testHeuristicClassifier(){
        IClassifier classifier = new HeuristicClassifier();
        test(classifier);
    }

    private static void testCustomStanfordClassifier(){
        test(new StanfordCustomClassifier());
    }

    private static void testDoubleTranslationClassifier(){
        test(new DoubleTranslationClassifier());
    }


    private static void test(IClassifier classifier){
        String testDir = AnalyserConstant.docRootPath + "test/";
        File dir = new File(testDir);
        if(dir.exists() && dir.isDirectory()){
            List<String> allFiles = Arrays.asList(dir.list());
            allFiles.stream().forEach(file -> {
                if(!(file.contains("TAGGED") || file.contains(".tsv"))){
                    LegalFile lF = null;
                    try {
                        lF = Standalone.loadFile(dir.getAbsolutePath() + "/" + file);
                        ClassificationResult cR = classifier.processFile(lF, false, true);
                        int[] evaluation = evaluateClassificationResult(cR, lF.getFilename().replace(".rtf", ".tsv"));
                        ArrayList<String> preContent = new ArrayList<>();
                        preContent.add(classifier.getClass().getSimpleName().replace("/", "_"));
                        preContent.add(lF.getFileOnlyNameBase().replace("/", "_"));
                        preContent.add(String.valueOf(new Date().getTime()).replace("/", "_"));
                        FileManager.WriteIntArray(preContent, evaluation, AnalyserConstant.outputRootPath
                                .concat(preContent.get(1)).concat(preContent.get(2)).concat(".txt"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            });
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
            mean[i] /= vectorList.size();
        }
        return mean;
    }

    //returns: person: right | missing | wrong; organisation: right | missing | wrong; location: right | missing | wrong
    //ignore multiple occurances
    private static int[] evaluateClassificationResult(ClassificationResult classificationResult, String tsvMap){
        boolean isDTC = classificationResult.getClassifier().contains("DoubleTranslationClassifier");
        int misclassifiedIndex = 2;
        int[] evaluation = new int[9];
        Map<String, String> tagMap = new HashMap<>();
        Path path = Paths.get(tsvMap);
        Charset charset = Charset.forName("UTF-8");
        try {
            List<String> lines = Files.readAllLines(path, charset);
            for (String line : lines) {
                String[] map = line.split("\t");
                if(!tagMap.containsKey(map[0]) && map.length==2)
                tagMap.put(map[0], map[1]);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        if(isDTC)return evaluateDTC(classificationResult, tagMap);
        List<String> visited = new ArrayList<>();
        for (NamedEntity nE : classificationResult.getNamedEntities()) {
            visited.add(nE.getValue());
            String clazz = tagMap.get(nE.getValue());
            if(clazz == null){
                clazz = nE.getTag().getTagType();
            }
            if(clazz.equals("Person") || clazz.equals("PERSON")){
                if(tagMap.containsKey(nE.getValue())){
                    evaluation[0] += 1;
                }else{
                    evaluation[2] += 1;
                }
            }else if(clazz.equals("Organisation") || clazz.equals("ORGANIZATION")){
                if(tagMap.containsKey(nE.getValue())){
                    evaluation[3] += 1;
                }else{
                    evaluation[5] += 1;
                }
            }else if(clazz.equals("Location") || clazz.equals("LOCATION")){
                if(tagMap.containsKey(nE.getValue())){
                    evaluation[6] += 1;
                }else{
                    evaluation[8] += 1;
                }
            }else{
                evaluation[misclassifiedIndex] += 1;
                misclassifiedIndex = (misclassifiedIndex+3)%9;
            }
        }
        for (String key : tagMap.keySet()) {
            if(!visited.contains(key)){
                String clazz = tagMap.get(key);
                if(clazz.equals("PERSON") || clazz.equals("Person")){
                    evaluation[1] += 1;
                }else if(clazz.equals("ORGANISATION") || clazz.equals("Organisation") || clazz.equals("Organization")){
                    evaluation[4] += 1;
                }else if(clazz.equals("LOCATION") || clazz.equals("Location")){
                    evaluation[7] += 1;
                }
            }
        }
        return evaluation;
    }

    private static int[] evaluateDTC(ClassificationResult classificationResult, Map<String, String> tagMap){
        int[] evalResult = new int[9];
        int misclassifiedIndex = 2;
        Map<Integer, Map<String, String>> tagMapTranslations = null;
        try {
            tagMapTranslations = translateTagMap(tagMap);
            List<String> visited = new ArrayList<>();
            for (NamedEntity nE : classificationResult.getNamedEntities()) {
                visited.add(nE.getValue());
                String clazz = getClazz(tagMapTranslations, nE.getValue());
                if(clazz == null){
                    clazz = nE.getTag().getTagType();
                }
                if(clazz.equals("Person") || clazz.equals("PERSON")){
                    if(containsKey(tagMapTranslations, nE.getValue())){
                        evalResult[0] += 1;
                    }else{
                        evalResult[2] += 1;
                    }
                }else if(clazz.equals("Organisation") || clazz.equals("ORGANIZATION")){
                    if(containsKey(tagMapTranslations, nE.getValue())){
                        evalResult[3] += 1;
                    }else{
                        evalResult[5] += 1;
                    }
                }else if(clazz.equals("Location") || clazz.equals("LOCATION")){
                    if(containsKey(tagMapTranslations, nE.getValue())){
                        evalResult[6] += 1;
                    }else{
                        evalResult[8] += 1;
                    }
                }else{
                    evalResult[misclassifiedIndex] += 1;
                    misclassifiedIndex = (misclassifiedIndex+3)%9;
                }
            }
            for (int key : getUnvisited(tagMapTranslations, visited)) {
                String clazz = getClazz(tagMapTranslations, key);
                if(clazz.equals("PERSON") || clazz.equals("Person")){
                    evalResult[1] += 1;
                }else if(clazz.equals("ORGANISATION") || clazz.equals("Organization") || clazz.equals("Organisation")){
                    evalResult[4] += 1;
                }else if(clazz.equals("LOCATION") || clazz.equals("Location")){
                    evalResult[7] += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (ItemTooLargeException e) {
            e.printStackTrace();
        }
        return evalResult;
    }

    private static Map<Integer, Map<String, String>> translateTagMap(Map<String, String> tagMap) throws Exception, ItemTooLargeException {
        Map<Integer, Map<String, String>> translatedMap = new HashMap<>();
        int counter = 0;
        for (String tag : tagMap.keySet()) {
            List<String> translations = DocumentTranslatorLibHelper.translateMultipleOutcomes(tag);
            Map<String, String> translationMap = new HashMap<>();
            for (String translation : translations) {
                translationMap.put(translation, tagMap.get(tag));
            }
            translatedMap.put(counter++, translationMap);
        }
        return translatedMap;
    }

    private static List<Integer> getUnvisited(Map<Integer, Map<String, String>> tagMapTranslations, List<String> visited) {
        List<Integer> unvisited = new ArrayList<>();
        for (int i=0; i<tagMapTranslations.size(); i++) {
            Map<String, String> valueMap = tagMapTranslations.get(i);
            if(valueMap != null){
                boolean isVisited = false;
                for (String k : valueMap.keySet()) {
                    if(visited.contains(k)){
                        isVisited = true;
                    }
                }
                if(!isVisited){
                    unvisited.add(i);
                }
            }
        }
        return unvisited;
    }

    private static boolean containsKey(Map<Integer, Map<String, String>> tagMapTranslations, String value) {
        for (int i=0; i<tagMapTranslations.size(); i++) {
            Map<String, String> valueMap = tagMapTranslations.get(i);
            if(valueMap != null){
                if(valueMap.containsKey(value))return true;
            }
        }
        return false;
    }

    private static String getClazz(Map<Integer, Map<String, String>> tagMapTranslations, String value) {
        for (int i=0; i<tagMapTranslations.size(); i++) {
            Map<String, String> valueMap = tagMapTranslations.get(i);
            if(valueMap != null){
                if(valueMap.containsKey(value))return valueMap.get(value);
            }
        }
        return "null";
    }

    private static String getClazz(Map<Integer, Map<String, String>> tagMapTranslations, int value) {
        return tagMapTranslations.get(value).values().iterator().next();
    }
}
