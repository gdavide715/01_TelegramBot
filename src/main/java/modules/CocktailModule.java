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
            // Create URL object
            URL url = new URL("https://www.thecocktaildb.com/api/json/v1/1/random.php");

            // Create connection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Set timeout
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Send GET request
            int responseCode = connection.getResponseCode();

            // Check if the response code is successful
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response body
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Close BufferedReader
                in.close();

                // Parse JSON response
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray drinksArray = jsonObject.getJSONArray("drinks");

                // Extract strDrink value from the first item in the array
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
                
                
                // Print the strDrink value
                m.setText("Random Cocktail: " + strDrink + "\nIngridients: " + ingredients + "\n\n" + img);
            } else {
                // If request is unsuccessful, print the response code
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
