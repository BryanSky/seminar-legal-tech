package de.legaltech.seminar.classifier;

import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.NamedEntity;
import de.legaltech.seminar.entities.Paragraph;
import de.legaltech.seminar.entities.Sentence;
import de.legaltech.seminar.constants.AnalyserConstant;
import de.legaltech.seminar.entities.ClassificationResult;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HeuristicClassifier extends AbstractClassifier {

    //Count absolute Häufigkeit der Entities!!!

    public static int vectorLength = 5;
    public static float threshold = 0.575f;
    public static float[] baseProb = new float[vectorLength];
    public static String[] targetWords = new String[]{"amt", "behörde", "gericht", "klag", "straße", "land", "BFH", "BGH"};
    private static List<String> tagList = null;

    private ClassificationResult result = new ClassificationResult();

    public ClassificationResult processFile(LegalFile file, boolean training, boolean test) {
        if(!training)loadBaseProb();
        for (Paragraph paragraph : file.getParagraphs()) {
            processParagraph(paragraph);
        }
        return result;
    }

    private void processParagraph(Paragraph paragraph) {
        for(Sentence sentence : paragraph.getSentences()){
            processSentence(sentence);
        }
    }

    private void processSentence(Sentence sentence) {
        for(int i=0; i<sentence.getLength(); i++){
            float[] prob = buildProbabilityVector(sentence, i);
            if(probabilityAboveThreshold(baseProb, prob, threshold)){
                NamedEntity nE = classifyEntity(sentence.get(i));
                result.addEntity(nE);
                result.addTag(nE.getTag());
            }
        }
    }

    private NamedEntity classifyEntity(String s) {
        NamedEntity nE = new NamedEntity();
        nE.setValue(s);
        nE.setTag("Generic");
        //TODO: classification based on freebaseDB
        return nE;
    }

    private void loadBaseProb(){
        String trainedVectorsFile = AnalyserConstant.HEURISTIC_TRAINED_VECTORS_FILE;
        Path path = Paths.get(trainedVectorsFile);
        Charset charset = Charset.forName("UTF-8");
        try {
            List<String> lines = Files.readAllLines(path, charset);
            String[] personVector = lines.get(0).split("\t");
            String[] organisationVector = lines.get(1).split("\t");
            String[] locationVector = lines.get(2).split("\t");
            float[] mergedProbabilities = new float[personVector.length];
            for(int i=0; i<mergedProbabilities.length; i++){
                mergedProbabilities[i] = Float.valueOf(personVector[i]) + Float.valueOf(organisationVector[i]) + Float.valueOf(locationVector[i]);
            }
            baseProb = mergedProbabilities;
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private boolean probabilityAboveThreshold(float[] baseVector, float[] classificationVector, float threshold){
        double euclDist = calculateEuclideanDistance(baseVector, classificationVector);
        return euclDist < threshold;
    }

    private double calculateEuclideanDistance(float[] baseVector, float[] classificationVector) {
        if(baseVector.length != classificationVector.length)return -1;
        double overallDist = 0;
        for (int i=0; i<baseVector.length; i++){
            overallDist += Math.pow(Math.abs(baseVector[i] - classificationVector[i]), 2);
        }
        return Math.sqrt(overallDist);
    }

    public static float[] buildProbabilityVector(Sentence sentence, int position){
        float[] probVec = new float[vectorLength];
        probVec[0] = calculateX1Prob(sentence, position);
        probVec[1] = calculateX2Prob(sentence, position);
        probVec[2] = calculateX3Prob(sentence, position);
        probVec[3] = calculateX4Prob(sentence, position);
        probVec[3] = calculateX5Prob(sentence, position);
        return probVec;
    }

    private static float calculateX1Prob(Sentence sentence, int position){
        if(position==0){
            return 0;
        }
        String wordBefore = sentence.get(position-1);
        if(wordBefore.equalsIgnoreCase("der") ||
                wordBefore.equalsIgnoreCase("die") ||
                wordBefore.equalsIgnoreCase("des") ||
                wordBefore.equalsIgnoreCase("in")){
            return 1;
        }
        return 0;
    }

    private static float calculateX2Prob(Sentence sentence, int position){
        String word = sentence.get(position);
        if(!word.equals("") && Character.isUpperCase(word.charAt(0))){
            return 1;
        }
        return 0;
    }

    private static float calculateX3Prob(Sentence sentence, int position){
        if(position==0){
            return 0;
        }
        String wordBefore = sentence.get(position-1);
        if(wordBefore.equalsIgnoreCase("Herr") ||
                wordBefore.equalsIgnoreCase("Frau")){
            return 1;
        }
        return 0;
    }

    private static float calculateX4Prob(Sentence sentence, int position){
        int count = 0;
        for (String s : targetWords) {
            if(sentence.get(position).contains(s)){
                return 1;
            }
        }
        return 0;
    }

    private static float calculateX5Prob(Sentence sentence, int position){
        if(position==0){
            return 0;
        }
        if(sentence.get(position - 1).contains("/") || sentence.get(position + 1).contains("/")){
            return 1;
        }
        return 0;
    }

    private static float calculateX6Prob(Sentence sentence, int position){
        List<String> tagList = getTagList();
        if(tagList.contains(sentence.get(position)))return 1;
        return 0;
    }

    private static List<String> getTagList(){
        if(tagList == null){
            tagList = new ArrayList<>();
            Path path = Paths.get(AnalyserConstant.CSC_TSV_FILE_MAX);
            Charset charset = Charset.forName("UTF-8");
            try {
                List<String> lines = Files.readAllLines(path, charset);
                for (String line : lines) {
                    String[] map = line.split("\t");
                    tagList.add(map[0]);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return tagList;
    }

    //use outcomes of different papers to classifyOriginal a file

    //this is only used in test phase, not in production
    private void evaluateClassifierPerformance(){

    }
}
