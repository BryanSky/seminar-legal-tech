package de.legaltech.seminar.classifier;

import de.legaltech.seminar.IClassifier;
import de.legaltech.seminar.entities.LegalFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public abstract class AbstractClassifier implements IClassifier {

    public void saveTaggedFile(LegalFile legalFile, String filename){
        if(legalFile.getTaggedContent() != null){
            String classifierName = this.getClass().getName();
            String taggedPath = legalFile.getFilePath() + classifierName + legalFile.getFileOnlyNameTagged();
            if(!(new File(taggedPath)).exists()){
                saveTextToPath(legalFile.getTaggedContent(), taggedPath);
            }else{
                saveTextToPath(legalFile.getTaggedContent(), legalFile.getFilePath() + (new Random(9)).nextInt() + legalFile.getFileOnlyNameTagged());
            }
        }
    }

    private void saveTextToPath(String taggedContent, String taggedPath) {
        try (PrintWriter out = new PrintWriter(taggedPath)) {
            out.println(taggedContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void compareTaggedWithManuallyTagged(String filename){

    }
}
