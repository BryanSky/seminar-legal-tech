package de.legaltech.seminar.classifier;

import de.legaltech.seminar.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.NamedEntity;
import de.legaltech.seminar.entities.Paragraph;
import de.legaltech.seminar.entities.Sentence;

public class HeuristicClassifier extends AbstractClassifier {

    public static int vectorLength = 4;
    public static float threshold = 0;
    public static float[] baseProb = new float[vectorLength];
    public static String[] targetWords = new String[]{"amt", "behörde", "gericht", "klag", "straße", "land", "BFH", "BGH"};

    private ClassificationResult result = new ClassificationResult();

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
        return null;
    }

    private void loadBaseProb(){
        //load base probability distribution from database
        //calculate this beforehand an basis of the training data
    }

    public void saveResult(LegalFile legalFile, ClassificationResult classificationResult) {

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

    private float[] buildProbabilityVector(Sentence sentence, int position){
        float[] probVec = new float[vectorLength];
        probVec[0] = calculateX1Prob(sentence, position);
        probVec[1] = calculateX2Prob(sentence, position);
        probVec[2] = calculateX3Prob(sentence, position);
        probVec[3] = calculateX4Prob(sentence, position);
        return probVec;
    }

    private float calculateX1Prob(Sentence sentence, int position){
        if(position==0){
            return 0;
        }
        String wordBefore = sentence.get(position-1);
        if(wordBefore.equalsIgnoreCase("der") ||
                wordBefore.equalsIgnoreCase("die") ||
                wordBefore.equalsIgnoreCase("des")){
            return 1;
        }
        return 0;
    }

    private float calculateX2Prob(Sentence sentence, int position){
        String word = sentence.get(position);
        if(Character.isUpperCase(word.charAt(0))){
            return 1;
        }
        return 0;
    }

    private float calculateX3Prob(Sentence sentence, int position){
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

    private float calculateX4Prob(Sentence sentence, int position){
        int count = 0;
        for (String s : targetWords) {
            if(sentence.get(position).contains(s)){
                return 1;
            }
        }
        return 0;
    }

    //use outcomes of different papers to classify a file

    //this is only used in test phase, not in production
    private void evaluateClassifierPerformance(){

    }
}
