/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
public class ShortLinkModule extends BotModule{

    public ShortLinkModule() {
        super("/shortLink");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        System.out.println(s);
            
        if(s.equalsIgnoreCase("/close")){
                m.setText("/shortLink chiuso");
                super.deactivate();
        
        }else if(this.isActive()){
                
                AsyncHttpClient client = new DefaultAsyncHttpClient();
                String responseText = null;
                String target = update.getMessage().getText().trim();
        
        
                String jsonString = target;
        try {
            URL url = new URL("https://shrtlnk.dev/api/v2/link");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("api-key", "ABVjsjL2ezwvYG343nO08b5nb01x0qW1lgZGZeENv1nd4");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String requestBody = "{\"url\": \"%s\"}";
            String originalUrl = target;
            requestBody = String.format(requestBody, originalUrl);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int statusCode = conn.getResponseCode();
            if (statusCode == 201) { // Changed the condition to check for status code 201
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("API call successful");
                    System.out.println("Response:");
                    System.out.println(response.toString());
                    jsonString = response.toString();
                }
            } else {
                //System.out.println("API call failed with status code: " + statusCode);
                //System.out.println("Response:");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        System.out.println(responseLine.trim());
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        

        JSONObject jsonObject = new JSONObject(jsonString);

        String shrtlnk = jsonObject.getString("shrtlnk");

        //System.out.println("Value of shrtlnk: " + shrtlnk);
        
        m.setText(shrtlnk);
                
        }
        else{
            super.activate();
            m.setText("Inserisci link da accorciare (/close -> per chiudere):");
        }
        //System.out.println(m.getText());
        return m;
            
    }
    
}
