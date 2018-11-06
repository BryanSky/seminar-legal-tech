package de.legaltech.seminar;

import de.legaltech.seminar.entities.LegalFile;
import de.legaltech.seminar.entities.NamedEntity;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StanfordLibHelper {

    public static final String STANDARD_CLASSIFIER_ENGLISH = "";
    public static final String STANDARD_CLASSIFIER_GERMAN = "";
    public static final String CUSTOM_CLASSIFIER_GERMAN = "";

    public static ClassificationResult classify(String classifier, LegalFile legalFile){
        File file = new File(legalFile.getFilename());
        try {
            String untaggedContent = openFile(file);
            CRFClassifier<CoreMap> nerClassifier = CRFClassifier.getClassifier(new File(classifier));
            String taggedContent = extract(untaggedContent, nerClassifier);
            ClassificationResult classificationResult = buildClassificationResult(taggedContent);
            classificationResult.setFileName(legalFile.getFilename());
            return classificationResult;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return new ClassificationResult();
    }

    private static ClassificationResult buildClassificationResult(String taggedContent) {
        ClassificationResult result = new ClassificationResult();
        result.setYear((new Date()).getYear());
        ArrayList<Pattern> patternList = new ArrayList<Pattern>();
        patternList.add(Pattern.compile("<LOCATION>[a-z, A-Z, 0-9]*</LOCATION>"));
        patternList.add(Pattern.compile("<ORGANISATION>[a-z, A-Z, 0-9]*</ORGANISATION>"));
        patternList.add(Pattern.compile("<PERSON>[a-z, A-Z, 0-9]*</PERSON>"));
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
        if(regexMatch.startsWith("<LOCATION>")){
            value = regexMatch.substring(10, regexMatch.length()-10);
            tag = "Location";
        } else if(regexMatch.startsWith("<ORGANISATION>")){
            value = regexMatch.substring(14, regexMatch.length()-14);
            tag = "Organisation";
        }else if(regexMatch.startsWith("<PERSON>")){
            value = regexMatch.substring(8, regexMatch.length()-8);
            tag = "Person";
        }
        nE.setValue(value);
        nE.setTag(tag);
        return nE;
    }

    private static String openFile(File file) throws IOException, BadLocationException {
        JEditorPane p = new JEditorPane();
        p.setContentType("text/rtf");
        EditorKit rtfKit = p.getEditorKitForContentType("text/rtf");
        rtfKit.read(new FileReader(file), p.getDocument(), 0);
        rtfKit = null;
        // convert to text
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
}
