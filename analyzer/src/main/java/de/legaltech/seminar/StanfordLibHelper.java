package de.legaltech.seminar;

import de.legaltech.seminar.constants.AnalyserConstant;
import de.legaltech.seminar.entities.ClassificationResult;
import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.NamedEntity;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StanfordLibHelper {

    public static final String STANDARD_CLASSIFIER_ENGLISH = AnalyserConstant.classifierRootPath + "english.all.3class.distsim.crf.ser.gz";
    public static final String STANDARD_CLASSIFIER_GERMAN = "";
    public static final String CUSTOM_CLASSIFIER_GERMAN = AnalyserConstant.classifierRootPath + "15german.all.3class.distsim.crf.ser.gz";

    public static ClassificationResult classifyOriginal(String classifier, LegalFile legalFile){
        String taggedContent = tagContent(classifier, legalFile.getContent());
        legalFile.setTaggedContent(taggedContent);
        ClassificationResult classificationResult = buildClassificationResult(taggedContent);
        classificationResult.setFileName(legalFile.getFilename());
        return classificationResult;
    }

    public static ClassificationResult classifyTranslation(String classifier, LegalFile legalFile) {
        String taggedContent = tagContent(classifier, legalFile.getTranslatedContent());
        legalFile.setTranslatedTaggedContent(taggedContent);
        ClassificationResult classificationResult = buildClassificationResult(taggedContent);
        classificationResult.setFileName(legalFile.getFileOnlyNameTranslatedEN());
        return classificationResult;
    }

    public static String tagContent(String classifier, String content){
        CRFClassifier<CoreMap> nerClassifier = null;
        try {
            nerClassifier = CRFClassifier.getClassifier(new File(classifier));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String taggedContent = extract(content, nerClassifier);
        return taggedContent;
    }

    public static ClassificationResult buildClassificationResult(String taggedContent) {
        taggedContent = taggedContent.replace("LOCATION>", "Location>")
                .replace("ORGANIZATION>", "Organisation>").replace("PERSON>", "Person>");
        ClassificationResult result = new ClassificationResult();
        result.setYear((new Date()).getYear());
        ArrayList<Pattern> patternList = new ArrayList<Pattern>();
        patternList.add(Pattern.compile("<Location>[a-z, A-Z, 0-9]*</Location>"));
        patternList.add(Pattern.compile("<Organisation>[a-z, A-Z, 0-9]*</Organisation>"));
        patternList.add(Pattern.compile("<Person>[a-z, A-Z, 0-9]*</Person>"));
        for(int i=0; i<patternList.size(); i++){
            Matcher m = patternList.get(i).matcher(taggedContent);
            while(m.find()){
                int start = m.start();
                int end = m.end();
                NamedEntity nE = extractEntity(taggedContent.substring(start, end));
                result.addEntity(nE);
                result.addTag(nE.getTag());
            }
        }
        return result;
    }

    private static NamedEntity extractEntity(String regexMatch){
        NamedEntity nE = new NamedEntity();
        String value = "";
        String tag = "";
        if(regexMatch.startsWith("<Location>")){
            value = regexMatch.substring(10, regexMatch.length()-11);
            tag = "Location";
        } else if(regexMatch.startsWith("<Organisation>")){
            value = regexMatch.substring(14, regexMatch.length()-15);
            tag = "Organisation";
        }else if(regexMatch.startsWith("<Person>")){
            value = regexMatch.substring(8, regexMatch.length()-9);
            tag = "Person";
        }
        nE.setValue(value);
        nE.setTag(tag);
        return nE;
    }

    public static String openFile(File file) throws IOException, BadLocationException {
        JEditorPane p = new JEditorPane();
        p.setContentType("text/rtf");
        EditorKit rtfKit = p.getEditorKitForContentType("text/rtf");
        rtfKit.read(new FileReader(file), p.getDocument(), 0);
        rtfKit = null;
        EditorKit txtKit = p.getEditorKitForContentType("text/plain");
        Writer writer = new StringWriter();
        txtKit.write(writer, p.getDocument(), 0, p.getDocument().getLength());
        String documentText = writer.toString();
        return documentText;
    }

    private static void loadTsvFile(){

    }

    private static String extract(String untaggedContents,  CRFClassifier<CoreMap> classifier){
        String labeledText;
        if (untaggedContents == null) {
            untaggedContents = "";
        }
        String taggedContents = classifier.classifyWithInlineXML(untaggedContents);
        Set<String> tags = classifier.labels();
        labeledText = classifier.backgroundSymbol();
        StringBuilder tagPattern = new StringBuilder();
        Iterator var21 = tags.iterator();

        while(var21.hasNext()) {
            String tag = (String)var21.next();
            if (!labeledText.equals(tag)) {
                if (tagPattern.length() > 0) {
                    tagPattern.append('|');
                }

                tagPattern.append(tag);
            }
        }
        return taggedContents;
    }

    public static void trainAndWrite(String modelOutPath, String prop, String trainingFilepath) {
        Properties props = StringUtils.propFileToProperties(prop);
        props.setProperty("serializeTo", modelOutPath);
        if (trainingFilepath != null) {
            props.setProperty("trainFile", trainingFilepath);
        }
        SeqClassifierFlags flags = new SeqClassifierFlags(props);
        CRFClassifier<CoreLabel> crf = new CRFClassifier<CoreLabel>(flags);
        crf.train();
        crf.serializeClassifier(modelOutPath);
    }
}
