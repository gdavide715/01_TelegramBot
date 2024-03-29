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
        
        if(s.equalsIgnoreCase("/close")){
           m.setText("/dictionary chiuso");
           super.deactivate();                    
        }else if(this.isActive()){
            
            try {
                // Codifico il termine da cercare
                String encodedParola = URLEncoder.encode(s, StandardCharsets.UTF_8);

                // Crea oggetto url contenente l'API endpoint
                URL url = new URL("https://api.urbandictionary.com/v0/define?term=" + encodedParola);

                // Apri connessione
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Imposta timeout
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // Prendi codice risposta
                int responseCode = connection.getResponseCode();

                // Controlla se va tutto bene (200)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Buffered Reader per leggere la risposta
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    // aggiungo allo stringBuilder il testo trovato nel bufferedReader
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // Close BufferedReader
                    in.close();

                    // Creo JSONObject
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray list = jsonObject.getJSONArray("list");

                    // Salvo le prime 2 definizioni in una lista
                    List<String> definitions = new ArrayList<>();
                    for (int i = 0; i < 2 && i < list.length(); i++) {
                        JSONObject definitionObject = list.getJSONObject(i);
                        String definition = definitionObject.getString("definition");
                        definitions.add(definition);
                    }

                    // le aggiungo a una stringa
                    int i = 1;
                    for (String definition : definitions) {
                        y += i + "\t" + definition + "\n\n";
                        i++;
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
            
            // Rimpiazzio le [] con spazi
            m.setText(y.replace("[", " ").replace("]", " "));     
        }
        else{
            m.setText("Inserisci la parola in inglese (/close per chiusere)");
            super.activate();
        }
        
        return m;
    }
    
}
