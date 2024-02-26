/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class QuoteModule extends BotModule{

    public QuoteModule() {
        super("/quote");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        try {
            //Public API:
            //https://www.metaweather.com/api/location/search/?query=<CITY>
            //https://www.metaweather.com/api/location/44418/

            URL url = new URL("https://zenquotes.io/api/random");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Check if connect is made
            int responseCode = conn.getResponseCode();

            // 200 OK
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                StringBuilder informationString = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                //Close the scanner
                scanner.close();

                //System.out.println(informationString);


                //JSON simple library Setup with Maven is used to convert strings to JSON
                JSONParser parse = new JSONParser();
                org.json.simple.JSONArray dataObject = (org.json.simple.JSONArray) parse.parse(String.valueOf(informationString));

                //Get the first JSON object in the JSON array
                //System.out.println(dataObject.get(0));

                org.json.simple.JSONObject quote = (org.json.simple.JSONObject) dataObject.get(0);

                String quoteValue = (String) quote.get("q");
                //System.out.println("Quote: " + quoteValue);
                m.setText(quoteValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.deactivate();
        return m;
    }
    
}
