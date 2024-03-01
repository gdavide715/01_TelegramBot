/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class SportQuizModule extends BotModule{
    String question = "";
    String correctAnswer = "";
    public SportQuizModule() {
        super("/SportQuiz");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        
        
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
        
        }else if(this.isActive()){
            if(s.equalsIgnoreCase(correctAnswer)){
                m.setText("Bravo, esatto");
                super.deactivate();
            }else if(s != correctAnswer){
                m.setText("Sbagliato");
                super.deactivate();
            }
            
           }
        else{
            try {
                // Create URL object with the API endpoint
                URL url = new URL("https://opentdb.com/api.php?amount=1&category=21&difficulty=medium&type=boolean");

                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method
                connection.setRequestMethod("GET");

                // Get the response code
                int responseCode = connection.getResponseCode();

                // If the response code is successful (200)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Create BufferedReader to read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    // Read response line by line
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse JSON response
                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();

                    // Extract question and correct_answer
                    question = jsonObject.getAsJsonArray("results")
                                                  .get(0).getAsJsonObject()
                                                  .get("question").getAsString();
                    correctAnswer = jsonObject.getAsJsonArray("results")
                                                        .get(0).getAsJsonObject()
                                                        .get("correct_answer").getAsString();

                    // Print the extracted values
                    //System.out.println("Question: " + question);
                    //System.out.println("Correct Answer: " + correctAnswer);
                } else {
                    // If response code is not 200, print the error
                    //System.out.println("API call failed with response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            m.setText("Rispondi True o False:\n" + question);
            //System.out.println(m.getText());
            super.activate();
        }
        return m;
    }
    
}
