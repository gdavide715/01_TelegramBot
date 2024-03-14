/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Integer.parseInt;
import java.net.HttpURLConnection;
import java.net.URL;
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
public class WorkoutModule extends BotModule{

    public WorkoutModule() {
        super("/workout");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        String ex = "";
        SendMessage m = new SendMessage();
        String s = update.getMessage().getText();
        m.setChatId(update.getMessage().getChatId());
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
            }
        else if(this.isActive()){
            String target[] = update.getMessage().getText().trim().split(",");
            String muscle = target[0].trim(); // Replace with your desired artist
            String num = target[1].trim(); // Replace with your desired song title

        try {
            // Construct URL
            String urlString = "https://api.api-ninjas.com/v1/exercises?muscle=" + muscle;


            URL url = new URL(urlString);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Add API key to request headers
            connection.setRequestProperty("x-api-key", "alM+ELEXdzq4WCbZ5LAPZw==MQQKok0UfvRTqWg8");

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

            // Parse JSON response
            JSONArray exercisesArray = new JSONArray(response.toString());
            int a = 1;
            for (int i = 0; i < parseInt(num); i++) {
                JSONObject exercise = exercisesArray.getJSONObject(i);
                String name = exercise.getString("name");
                String instructions = exercise.getString("instructions");
                ex += ("Exercise" + a +" Name: " + name + "\n\n");
                ex += ("Instructions: " + instructions + "\n\n");
                //System.out.println();
                a++;
            }
            m.setText(ex);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        
            
        }else{
            m.setText("Inserisci muscolo (in inglese) e numero di esercizi (max 5) (/close -> per chiudere): ");
            super.activate();
        }
        
        return m;
    
    }
    
}
