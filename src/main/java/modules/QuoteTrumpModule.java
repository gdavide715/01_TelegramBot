/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class QuoteTrumpModule extends BotModule{

    public QuoteTrumpModule() {
        super("/quoteTrump");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        try {
            URL url = new URL("https://www.tronalddump.io/random/quote");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder jsonStringBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                jsonStringBuilder.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject jsonObject = new JSONObject(new JSONTokener(jsonStringBuilder.toString()));

            String value = jsonObject.getString("value");
            
            
            String urlValue = jsonObject.getJSONObject("_embedded")
                                             .getJSONArray("source")
                                             .getJSONObject(0)
                                             .getString("url");
            
            String mes = value + "\n\n" + urlValue;
            m.setText(mes);
            // Close the connection
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.deactivate();
        return m;
    }
    
}
