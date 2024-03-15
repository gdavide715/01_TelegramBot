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
            // Crea oggetto url contenente l'API endpoint
            URL url = new URL("https://bible-api.com/?random=verse");

            // Apri connessione
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Prendi codice risposta
            int responseCode = connection.getResponseCode();

            // Controlla se va tutto bene (200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Buffered Reader per leggere la risposta
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                // aggiungo allo stringBuilder il testo trovato nel bufferedReader
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                // Chiudo
                reader.close();
                
                // Metto risposta in jsonString
                jsonString = response.toString();
            } else {
                System.out.println("Failed to fetch data. Response code: " + responseCode);
            }
            
            // Creo JSONObject
            JSONObject jsonObject = new JSONObject(jsonString);

            // Estraggo valori di "reference"
            String reference = jsonObject.getString("reference");

            // Estraggo il valore del testo
            List<String> texts = new ArrayList<>();
            JSONArray versesArray = jsonObject.getJSONArray("verses");
            for (int i = 0; i < versesArray.length(); i++) {
                JSONObject verseObject = versesArray.getJSONObject(i);
                String text = verseObject.getString("text").trim();
                texts.add(text);
            }

            // Metto assieme l'estrazione del campo reference e testo
            Map<String, List<String>> result = new HashMap<>();
            result.put(reference, texts);

            
            for (Map.Entry<String, List<String>> entry : result.entrySet()) {
                String ref = entry.getKey();
                List<String> verseTexts = entry.getValue();
                verse += "Reference: " + ref  + "\n\n";
                
                for (String text : verseTexts) {
                    verse += "- " + text;
                }
            }
            
            // Lo setto sul messaggio 
            m.setText(verse);
            // Disconnetto la connessione
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Ritorno
        return m;
    }
    
}
