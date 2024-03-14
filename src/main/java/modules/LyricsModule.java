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
public class LyricsModule extends BotModule{

    public LyricsModule() {
        super("/lyrics");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId()); 
        String s = update.getMessage().getText();
        
        System.out.println(s);
        if(s.equalsIgnoreCase("/close")){
            m.setText("/lirycs chiuso");
                super.deactivate();
            }
        else if(this.isActive()){
            String target[] = update.getMessage().getText().trim().split(",");
            String artist = target[0].trim(); // Replace with your desired artist
            String title = target[1].trim(); // Replace with your desired song title

        try {
            URL url = new URL("https://api.lyrics.ovh/v1/" + artist + "/" + title);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonString = response.toString();
                
                JSONObject jsonObject = new JSONObject(jsonString);

                // Get the lyrics from the JSON object
                String lyrics = jsonObject.getString("lyrics");

                // Find the index of the substring "Dean, what's poppin'?"
                int startIndex = lyrics.indexOf("]");
                if (startIndex != -1) {
                    // Extract the substring from the index of the occurrence of "Dean, what's poppin'?" till the end of the lyrics
                    String relevantLyrics = lyrics.substring(startIndex+1);
                    //System.out.println(relevantLyrics);
                    m.setText(relevantLyrics);
                } else {
                    System.out.println("Substring not found in the lyrics.");
                }
                
               
                
            } else {
                System.out.println("Error: HTTP response code " + responseCode);
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
        }else{
            m.setText("Inserisci: l'artista, canzone");
            super.activate();
        }
        
        return m;
    }
    
}
