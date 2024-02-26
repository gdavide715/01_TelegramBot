/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
/**
 *
 * @author taluk
 */
public class ImageModule extends BotModule{

    public ImageModule() {
        super("/img");
        
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m =  m =  new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        this.deactivate();
        m.setText("fatto");
        return m;
    }
    
    public static String link (String t) throws MalformedURLException, JsonProcessingException, IOException{
        String url2 = null;
    
        
            URL url = new URL("https://serpapi.com/search.json?q=" + t +"&engine=google_images&ijn=0&api_key=0d188adbc4cfc253bee588fa54e662753babf5bae1edaba11dbb3f22087cb8ee");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Check if connection is made
            int responseCode = conn.getResponseCode();

            // 200 OK
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                // Read response into a JSON string
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Convert JSON string to JSON object
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.toString());

                // Process the JSON object as needed
                JsonNode imagesResults = jsonNode.get("images_results");
                if (imagesResults != null && imagesResults.isArray()) {
                    JsonNode firstImageResult = imagesResults.get(0);
                    if (firstImageResult != null) {
                        String originalUrl = firstImageResult.get("original").asText();
                        url2 = firstImageResult.get("original").asText();
                        System.out.println("Original URL: " + originalUrl);
                        //Specify URL from which file will be downloaded.
       
                    }
                }
                
        
        return url2;
            }
    }
    
    
}
    
   
    

