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
import java.util.Arrays;
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
                m.setText("/currency chiuso");
                super.deactivate();
            }
        else if(this.isActive()){
            String target[] = update.getMessage().getText().trim().split(",");
            System.out.println(Arrays.toString(target) + "\n\n" + target.length);
            
            if (target.length == 2) {
                String cu1 = target[0].trim().toUpperCase(); 
                String cu2 = target[1].trim().toUpperCase();
                try {
                    // Crea oggetto url contenente l'API endpoint
                    String baseUrl = "https://api.freecurrencyapi.com/v1/latest";
                    String apiKey = "fca_live_b3yEDdoV7EiCsIJuwM0pD0DcoKGnqlK9eVyYmilo";
                    String baseCurrency = "EUR";
                    String currencies = cu1 + ","  + cu2;

                    String urlString = String.format("%s?apikey=%s&base_currency=%s&currencies=%s",
                                            baseUrl, URLEncoder.encode(apiKey, "UTF-8"),
                                            URLEncoder.encode(baseCurrency, "UTF-8"),
                                            URLEncoder.encode(currencies, "UTF-8"));

                    URL url = new URL(urlString);

                    // Apri connessione
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Prendi codice risposta
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
                    String withoutQuotes = result1.replace("\"", "");

                    // Rimuovi parentesi
                    String withoutBraces = withoutQuotes.replace("{", " ").replace("}", " ");

                    // Imposta messaggio
                    m.setText(withoutBraces + ", EUR: 1");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }else{
                m.setText("Inserisci 2 valute!");
            }   
        }else{
            String currencyAbbreviations = "\n-EUR\n-USD\n-JPY\n-BGN\n-CZK\n-DKK\n-GBP\n-HUF\n-PLN\n-RON\n-SEK\n-CHF\n-ISK\n-NOK\n-HRK\n-RUB\n-TRY\n-AUD\n-BRL\n-CAD\n-CNY\n-HKD\n-IDR\n-ILS\n-INR\n-KRW\n-MXN\n-MYR\n-NZD\n-PHP\n-SGD\n-THB\n-ZAR\n";

            m.setText("Inserisci valute interessate (2)\n/close -> per chiudere:" + "\n\nesempi: " + currencyAbbreviations );
            super.activate();
        }
        return m;
    }
    
}
