package de.legaltech.seminar;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.*;
import de.legaltech.seminar.exceptions.ItemTooLargeException;

public class DocumentTranslatorLibHelper {
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_DE = "de";

    static String subscriptionKey = "ENTER KEY HERE";

    static String host = "https://api.cognitive.microsofttranslator.com";
    static String path = "/translate?api-version=3.0";

    static String paramsToEN = "&from=de&to=en";
    static String paramsToDE = "&from=en&to=de";
    static String params = "";


    public static String translate(String content, String sourceLanguage, String targetLanguage){
        if(sourceLanguage.length()==2 && targetLanguage.length() == 2){
            params = "&from=" + sourceLanguage + "&to=" + targetLanguage;
        }else{
            params = paramsToEN;
        }
        String response = "";
        try {
            String[] paragraphs = content.split("\n");
            for (String paragraph : paragraphs) {
                try{
                    response += translate(paragraph) + "\n";
                }catch(ItemTooLargeException itle){
                    response += translateSingleSentences(paragraph);
                }
            }
        }
        catch (Exception e) {
            System.out.println (e);
        }
        return response;
    }

    private static String translateSingleSentences(String paragraph){
        String[] sentences = paragraph.split(Pattern.quote("."));
        String response = "";
        for (String sentence : sentences) {
            try{
                response += translate(sentence) + ".";
            }catch(ItemTooLargeException itle){
                response += sentence + ".";
            } catch (Exception e) {
                e.printStackTrace();
                response += sentence + ".";
            }
        }
        return response;
    }

    private static String readFile(String filename) throws IOException, BadLocationException {
        File file = new File(filename);
        return StanfordLibHelper.openFile(file);
    }

    public static class RequestBody {
        String Text;

        public RequestBody(String text) {
            this.Text = text;
        }
    }

    public static String Post (URL url, String content) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", content.length() + "");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
        connection.setRequestProperty("X-ClientTraceId", java.util.UUID.randomUUID().toString());
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        byte[] encoded_content = content.getBytes("UTF-8");
        wr.write(encoded_content, 0, encoded_content.length);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();
        if(responseCode==401){
            System.out.println("401: Unauthorized");
            return content;
        }else if(responseCode==400){
            System.out.println("400: Bad request");
        } else if(responseCode == 200){
            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString();
        }
        System.out.println("An error occured while translation");
        return content;
    }

    public static List<String> translateMultipleOutcomes(String text) throws Exception, ItemTooLargeException {
        URL url = new URL (host + path + paramsToEN);

        List<RequestBody> objList = new ArrayList<RequestBody>();
        objList.add(new RequestBody(text));
        String content = new Gson().toJson(objList);
        if(content.length()>5000){
            throw new ItemTooLargeException();
        }
        if(text != null && !text.equals("")){
            String response = Post(url, content);
            return extractArray(response);
        }
        return new ArrayList<>();
    }

    public static String translate(String text) throws Exception, ItemTooLargeException {
        URL url = new URL (host + path + params);

        List<RequestBody> objList = new ArrayList<RequestBody>();
        objList.add(new RequestBody(text));
        String content = new Gson().toJson(objList);
        if(content.length()>5000){
            throw new ItemTooLargeException();
        }
        if(text != null && !text.equals("")){
            String response = Post(url, content);
            return extractTranslation(response);
        }
        return text;
    }

    private static String extractTranslation(String response) {
        response = response.replace("[", "").replace("]","");
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(response);
        JsonElement translations = json.getAsJsonObject().get("translations");
        JsonElement text = translations.getAsJsonObject().get("text");
        return text.getAsString();
    }

    private static List<String> extractArray(String response) {
        List<String> responseList = new ArrayList<>();
        response = response.substring(1, response.length()-1);
        response = response.replace(",\"to\":\"en\"", "");
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(response);
        JsonArray translations = json.getAsJsonObject().getAsJsonArray("translations");
        for (int i=0; i<translations.size(); i++) {
            JsonElement element = translations.get(i);
            JsonElement text = element.getAsJsonObject().get("text");
            responseList.add(text.getAsString());
        }
        return responseList;
    }

    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }
}
