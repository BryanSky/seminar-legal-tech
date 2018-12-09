package de.legaltech.seminar;

import de.legaltech.seminar.entities.LegalFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static boolean WriteToFile(List<String> allLines, String filename){
        try {
            Files.write(Paths.get(filename), allLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean WriteToFile(String content, String filename){
        List<String> allLines = new ArrayList<>();
        allLines.add(content);
        return WriteToFile(allLines, filename);
    }

    public static void WriteIntArray(List<String> preContent, int[] content, String filename){
        String lastLine = "";
        for (int i : content) {
            lastLine += String.valueOf(i) + "\t";
        }
        preContent.add(lastLine);
        WriteToFile(preContent, filename);
    }

    public static String loadTextFile(String filename) {
        StringBuilder sb = new StringBuilder();
        Path path = Paths.get(filename);
        Charset charset = Charset.forName("UTF-8");
        try {
            List<String> lines = Files.readAllLines(path, charset);
            for(String s : lines){
                sb.append(s);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return sb.toString();
    }
}
