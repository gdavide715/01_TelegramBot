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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
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
        String s = update.getMessage().getText();
        System.out.println(s);
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
            }
        else if(this.isActive()){
            m.setChatId(update.getMessage().getChatId());
            try {
                // Create URL object
                URL url = new URL("https://api.urbandictionary.com/v0/define?term=" + s);

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
                        m.setText(definition);
                    }

                    // Print the definitions
                    for (String definition : definitions) {
                        System.out.println(definition);
                    }
                } else {
                    // If request is unsuccessful, print the response code
                    System.out.println("GET request not successful. Response code: " + responseCode);
                }

                // Disconnect the connection
                connection.disconnect();

            } catch (IOException ex) {
                Logger.getLogger(DictionaryModule.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            m.setText("Inserisci parola");
            super.activate();
        }
        
        return m;
    }
    
}
