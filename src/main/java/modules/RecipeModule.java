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
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class RecipeModule extends BotModule{
    String translation = "";
    public RecipeModule() {
        super("/recipe");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
      
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
            }
        else if(this.isActive()){
            String t = "";
            try {
            // API endpoint URL
            String apiUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + s;

            // Create URL object
            URL url = new URL(apiUrl);

            // Create connection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Create BufferedReader to read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // Read the response line by line
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // Close the BufferedReader
            in.close();

            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(response.toString());

            // Extract the meals array
            JSONArray meals = jsonObject.getJSONArray("meals");

            // Get the first object in the meals array
            JSONObject mealObject = meals.getJSONObject(0);

            // Extract the strInstructions field value
            String strInstructions = mealObject.getString("strInstructions");

            //Traduzione
            
            
            AsyncHttpClient client = new DefaultAsyncHttpClient();
            
            String testo = strInstructions;
            System.out.println(testo);
            
            
            
            //System.out.println(testo);
            m.setText(testo);
        
                // Define the URL
        
            
            

        } catch (IOException e) {
            e.printStackTrace();
        }   
        }else{
            
            m.setText("Inserisci piatto");
            //System.out.println(m.getText());
            super.activate();
        }
        
        return m;
    }
    
    public String traduci(String frase){
        String s = "";
        
        
        
        // Define the URL
        String url = "https://api.microsofttranslator.com/V2/Ajax.svc/Translate?appId=DB50E2E9FBE2E92B103E696DCF4E3E512A8826FB&oncomplete=?&text=" + frase.trim() + "&from=&to=it";

        // Create an HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Create an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        // Send the request asynchronously
        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TranslateModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        // Handle the response asynchronously
        CompletableFuture<String> translationResult = responseFuture.thenApply(response -> {
            // Extract and return the translated text
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Use regular expression to extract text inside quotes
                Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(responseBody);
                if (matcher.find()) {
                    return matcher.group(1); // Extract the text inside quotes
                } else {
                    return "Error: Translation not found in response";
                }
            } else {
                return "Error: " + response.statusCode(); // Handle error cases
            }
            });

            // Block and get the translation result
            s = translationResult.join();
            System.out.println(s);
            
        
        return s;
    }
    
}
