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
public class ITQuestionModule extends BotModule{
    String question = "";
    String correctAnswer = "";
    public ITQuestionModule() {
        super("/ITQuiz");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        
        
        if(s.equalsIgnoreCase("/close")){
            m.setText("/ITQuiz chiuso");
            super.deactivate();
        
        }else if(this.isActive()){
            if(s.equalsIgnoreCase(correctAnswer)){
                m.setText("Bravo, esatto!\n/ITQuiz -> per riprovare\n/close -> per chiudere");
                super.deactivate();
            }else if(s != correctAnswer){
                m.setText("Sbagliato!\n/ITQuiz -> per riprovare\n/close -> per chiudere");
                super.deactivate();
            }
            
           }
        else{
            try {
                // Crea oggetto url contenente l'API endpoint
                URL url = new URL("https://opentdb.com/api.php?amount=1&category=18&type=boolean");

                // Apri connessione
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

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
                    in.close();

                    // Creo JsonObject
                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();

                    // Estraggo valori di "results", "question", "correct_answer"
                    question = jsonObject.getAsJsonArray("results")
                                                  .get(0).getAsJsonObject()
                                                  .get("question").getAsString();
                    correctAnswer = jsonObject.getAsJsonArray("results")
                                                        .get(0).getAsJsonObject()
                                                        .get("correct_answer").getAsString();

                    
                    //System.out.println("Question: " + question);
                    //System.out.println("Correct Answer: " + correctAnswer);
                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Imposto testo
            m.setText("Rispondi True o False:\n" + question);
            //System.out.println(m.getText());
            super.activate();
        }
        return m;
    }
    
}
