/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
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

            m.setText("Instructions: " + strInstructions);

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
    
}
