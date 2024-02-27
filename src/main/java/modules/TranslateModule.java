package modules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Response;

import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.ws.rs.sse.SseEventSource.target;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TranslateModule extends BotModule{

    
    public TranslateModule() {
        super("/traduci");
        //super.activate();
    }
    

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        System.out.println(s);
            
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
        
        }else if(this.isActive()){
                
                AsyncHttpClient client = new DefaultAsyncHttpClient();
                String responseText = null;
                String target[] = update.getMessage().getText().trim().split(",");
                String from = target[0].trim();
                String to = target[1].trim();
                String testo = target[2].trim();
        
        
                String responseString = "";
        try {
            // Construct the URL with parameters
            String urlString = "https://655.mtis.workers.dev/translate?text=" + testo + "&source_lang=" + from + "&target_lang=" + to;
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
            
            // Parse the JSON response
            JsonObject jsonResponse = new Gson().fromJson(responseString, JsonObject.class);

            // Extract the translated_text value
            String translatedText = jsonResponse.getAsJsonObject("response").get("translated_text").getAsString();

            // Print the translated text
            System.out.println("Translated Text: " + translatedText);
            m.setText(translatedText);
            // Close the connection
            connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            super.activate();
            m.setText("Inserisci: from, to, testo");
        }
        System.out.println(m.getText());
        
        return m;
    }
    
}
