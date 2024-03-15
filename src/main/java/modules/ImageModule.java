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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
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
    public PartialBotApiMethod<?> handleCommand(Update update) {
        SendPhoto sendAudio = new SendPhoto();
        sendAudio.setChatId(update.getMessage().getChatId().toString());
        String downloadUrl = "";
        String s = update.getMessage().getText();
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId().toString());
        
        if(s.equalsIgnoreCase("/close")){
                m.setText("/img chiuso");
                super.deactivate();
                return m;
            }
        else if(this.isActive()){
            
            try {
                sendAudio.setPhoto(new InputFile(link(s)));
            } catch (JsonProcessingException ex) {
                Logger.getLogger(ImageModule.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ImageModule.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return sendAudio;
            
        }else{
            
            
            //System.out.println(m.getText());
            m.setText("Inserisci nome immagine (/close -> per chiudere):");
            super.activate();
            return m;
            
            
        }
    }
    
    public static String link (String t) throws MalformedURLException, JsonProcessingException, IOException{
        String url2 = null;
    
        
            URL url = new URL("https://serpapi.com/search.json?q=" + t +"&engine=google_images&ijn=0&api_key=0d188adbc4cfc253bee588fa54e662753babf5bae1edaba11dbb3f22087cb8ee");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

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
                
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.toString());

                JsonNode imagesResults = jsonNode.get("images_results");
                if (imagesResults != null && imagesResults.isArray()) {
                    JsonNode firstImageResult = imagesResults.get(0);
                    if (firstImageResult != null) {
                        String originalUrl = firstImageResult.get("original").asText();
                        url2 = firstImageResult.get("original").asText();
                        System.out.println("Original URL: " + originalUrl);
       
                    }
                }
                
        
        return url2;
            }
    }
    
    
}
    
   
    

