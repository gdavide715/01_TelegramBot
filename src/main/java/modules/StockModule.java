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
public class StockModule extends BotModule{

    public StockModule() {
        super("/stock");
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
            try {
            // Create URL
            URL url = new URL("https://finnhub.io/api/v1/quote?symbol=" + s.trim() + "&token=cnf4r8pr01qi6ftoagagcnf4r8pr01qi6ftoagb0");

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Remove curly braces and format JSON
            String formattedResponse = response.toString()
                    .replaceAll("[{}]", "") // Remove curly braces
                    .replaceAll(",", ",\n"); // Add newline after commas
            
            // Print formatted response
            String guida = "(c: prezzo attuale, d: differenza, dp: percentuale differenza, h: prezzo più alto della giornata, l: prezzo più basso della giornata, o: prezzo di apertura)\n\n" + s + "\n\n";
            String t = guida + formattedResponse;
            m.setText(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        }else{
            
            m.setText("Inserisci stock interessato (esempi: https://stockanalysis.com/stocks/)");
            //System.out.println(m.getText());
            super.activate();
        }
        
        return m;
    }
    
}
