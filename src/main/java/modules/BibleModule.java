/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class BibleModule extends BotModule{

    public BibleModule() {
        super("/bible");
    }

    @Override
    public PartialBotApiMethod<?> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        String verse = "";
        m.setChatId(update.getMessage().getChatId());
        String jsonString = "";
        try {
            // Create the URL object with the API endpoint
            URL url = new URL("https://bible-api.com/?random=verse");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Check if the request was successful (response code 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Create a BufferedReader to read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                // Read the response line by line and append it to the StringBuilder
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                // Close the BufferedReader
                reader.close();
                
                // Print the response
                jsonString = response.toString();
            } else {
                System.out.println("Failed to fetch data. Response code: " + responseCode);
            }
            
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract the value of "reference"
            String reference = jsonObject.getString("reference");

            // Extract the values of "text"
            List<String> texts = new ArrayList<>();
            JSONArray versesArray = jsonObject.getJSONArray("verses");
            for (int i = 0; i < versesArray.length(); i++) {
                JSONObject verseObject = versesArray.getJSONObject(i);
                String text = verseObject.getString("text").trim();
                texts.add(text);
            }

            // Combine the reference and text values into a Map
            Map<String, List<String>> result = new HashMap<>();
            result.put(reference, texts);

            // Print the combined result
            for (Map.Entry<String, List<String>> entry : result.entrySet()) {
                String ref = entry.getKey();
                List<String> verseTexts = entry.getValue();
                verse += "Reference: " + ref  + "\n\n";
                
                for (String text : verseTexts) {
                    verse += "- " + text;
                }
            }
            m.setText(verse);
            // Disconnect the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
    
}
