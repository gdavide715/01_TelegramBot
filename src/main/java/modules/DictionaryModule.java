/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class DictionaryModule extends BotModule{

    public DictionaryModule() {
        super("/dictionary");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        String y = "";
        
        if(super.isActive()){
            s = update.getMessage().getText();
            String mess[] = s.split(",");
            String lingua = mess[0].trim();
            String parola = mess[1].trim();
            
            if("en".equals(lingua)){
            } else {
                String responseString = "";
                try {
                    // Construct the URL with parameters
                    String urlString = "https://655.mtis.workers.dev/translate?text=" + parola + "&source_lang=" + lingua + "&target_lang=en";
                    URL url = new URL(urlString);

                    // Open a connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Set the request method
                    connection.setRequestMethod("GET");

                    // Get the response code
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) { // If response is successful
                        // Read the response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Print the response
                        System.out.println("API Response: " + response.toString());
                        responseString = response.toString();
                    } else {
                        System.out.println("Error: HTTP " + responseCode);
                    }

                    JsonObject jsonResponse = new Gson().fromJson(responseString, JsonObject.class);
                    String translatedText = jsonResponse.getAsJsonObject("response").get("translated_text").getAsString();
                    
                    String[] words = translatedText.split(" ");
                    if(words.length>1){
                        String lastWord = words[words.length - 1];
                        parola = lastWord;
                    }else{
                        parola = translatedText;
                    }
                    connection.disconnect();
                } catch (IOException e) {
                }
            } //Fine if lingua != en

            try {
                // Encode the search term
                String encodedParola = URLEncoder.encode(parola, StandardCharsets.UTF_8);

                // Create URL object
                URL url = new URL("https://api.urbandictionary.com/v0/define?term=" + encodedParola);

                // Create connection object
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method
                connection.setRequestMethod("GET");

                // Set timeout
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // Send GET request
                int responseCode = connection.getResponseCode();

                // Check if the response code is successful
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response body
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // Close BufferedReader
                    in.close();

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray list = jsonObject.getJSONArray("list");

                    // Extract first two definitions and store them in a list
                    List<String> definitions = new ArrayList<>();
                    for (int i = 0; i < 2 && i < list.length(); i++) {
                        JSONObject definitionObject = list.getJSONObject(i);
                        String definition = definitionObject.getString("definition");
                        definitions.add(definition);
                    }

                    // Print the definitions
                    for (String definition : definitions) {
                        y += definition + " ";
                    }
                } else {
                    // If request is unsuccessful, print the response code
                    System.out.println("GET request not successful. Response code: " + responseCode);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            
            
            if(!"en".equals(lingua)){
                try {
                    // Construct the URL with parameters
                    String urlString = "https://655.mtis.workers.dev/translate?text=" + URLEncoder.encode(y.trim(), StandardCharsets.UTF_8) + "&source_lang=en&target_lang=" + URLEncoder.encode(lingua, StandardCharsets.UTF_8);
                    URL url = new URL(urlString);

                    // Open a connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Set the request method
                    connection.setRequestMethod("GET");

                    // Get the response code
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) { // If response is successful
                        // Read the response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Print the response
                        System.out.println("API Response: " + response.toString());
                        String responseString = response.toString();

                        // Parse JSON response
                        JsonObject jsonResponse = new Gson().fromJson(responseString, JsonObject.class);
                        String translatedText = jsonResponse.getAsJsonObject("response").get("translated_text").getAsString();

                        y = translatedText;
                    } else {
                        // If request is unsuccessful, print the response code
                        System.out.println("Error: HTTP " + responseCode);
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
}

                
            }
            
            m.setText(y);
            
            
        }else if(s.equalsIgnoreCase("/close")){
            super.deactivate();
        }
        else{
            m.setText("Inserisci: lingua, parola");
            super.activate();
        }
        
        return m;
    }
    
}
