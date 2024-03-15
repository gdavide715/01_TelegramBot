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
import java.util.ArrayList;
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
public class CocktailModule extends BotModule{

    public CocktailModule() {
        super("/cocktail");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        try {
            // Crea oggetto url contenente l'API endpoint
            URL url = new URL("https://www.thecocktaildb.com/api/json/v1/1/random.php");

            // Apri connessione
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Imposta timeout
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Prendi codice risposta
            int responseCode = connection.getResponseCode();

            // Controlla se va tutto bene (200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Buffered Reader per leggere la risposta
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // aggiungo allo stringBuilder il testo trovato nel bufferedReader
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Chiudo BufferedReader
                in.close();

                // Creo JSONObject
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray drinksArray = jsonObject.getJSONArray("drinks");

                // Estraggo valore di strDrink
                JSONObject firstDrink = drinksArray.getJSONObject(0);
                String strDrink = firstDrink.getString("strDrink");
                String img = firstDrink.getString("strDrinkThumb");
                int a = 1;
                ArrayList<String> ing = new ArrayList<>();
                for (int i = 0; i < 15; i++) {
                    String ingredient = firstDrink.optString("strIngredient" + a);
                    if (ingredient != null && !ingredient.isEmpty()) {
                        ing.add(ingredient);
                    }
                    a++;
                }
                String ingredients = ing.toString();
                ingredients = ingredients.replaceAll("^\\[|\\]$", "");
                
                
                // Setto al messaggio
                m.setText("Random Cocktail: " + strDrink + "\nIngridients: " + ingredients + "\n\n" + img);
            } else {
                // Se richiesta non è stata successfull allora stampa codice
                System.out.println("GET request not successful. Response code: " + responseCode);
            }

            // Disconnect the connection
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.deactivate();
        return m;
    }
    
}
