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
                m.setText("/recipe chiuso");
                super.deactivate();
            }
        else if(this.isActive()){
            String t = "";
            try {
            String apiUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + s;

            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            JSONArray meals = jsonObject.getJSONArray("meals");

            JSONObject mealObject = meals.getJSONObject(0);

            String strInstructions = mealObject.getString("strInstructions");

            //Traduzione
            
            
            AsyncHttpClient client = new DefaultAsyncHttpClient();
            
            String testo = strInstructions;
            
            
            
            //System.out.println(testo);
            m.setText(testo);
        
        
            
            

        } catch (IOException e) {
            e.printStackTrace();
        }   
        }else{
            
            m.setText("Inserisci piatto (/close ->  per chiudere): ");
            //System.out.println(m.getText());
            super.activate();
        }
        
        return m;
    }
}