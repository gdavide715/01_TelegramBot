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
import java.net.URLEncoder;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class CurrencyModule extends BotModule{

    public CurrencyModule() {
        super("/currency");
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
            String target[] = update.getMessage().getText().trim().split(",");
            String cu1 = target[0].trim().toUpperCase(); 
            String cu2 = target[1].trim().toUpperCase(); 

        
            try {
                // Construct URL
                String baseUrl = "https://api.freecurrencyapi.com/v1/latest";
                String apiKey = "fca_live_b3yEDdoV7EiCsIJuwM0pD0DcoKGnqlK9eVyYmilo";
                String baseCurrency = "EUR";
                String currencies = cu1 + ","  + cu2;

                String urlString = String.format("%s?apikey=%s&base_currency=%s&currencies=%s",
                                        baseUrl, URLEncoder.encode(apiKey, "UTF-8"),
                                        URLEncoder.encode(baseCurrency, "UTF-8"),
                                        URLEncoder.encode(currencies, "UTF-8"));

                URL url = new URL(urlString);

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method
                connection.setRequestMethod("GET");

                // Get response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Close resources
                in.close();
                connection.disconnect();
                String jsonString = response.toString();
                String result = jsonString.substring(1, jsonString.length() - 1);
                String result1 = result.replace(",", ",  ");
                
                // Print response
                m.setText(result1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            m.setText("Inserisci inserisci valute interessate (max 2)");
            super.activate();
        }
        return m;
    }
    
}
