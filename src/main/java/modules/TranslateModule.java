package modules;

import okhttp3.Response;

import interfaces.BotModule;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TranslateModule extends BotModule{

    
    public TranslateModule() {
        super("/traduci");
        //super.activate();
    }
    

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        System.out.println(s);
            
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
        
        }else if(this.isActive()){
                
                AsyncHttpClient client = new DefaultAsyncHttpClient();
                String responseText = null;
                String target[] = update.getMessage().getText().trim().split(",");
                String lingua = target[0].trim();
                String testo = target[1].trim();
        
        
                try {
                    org.asynchttpclient.Response response = client.prepare("POST", "https://google-translate1.p.rapidapi.com/language/translate/v2")
                            .setHeader("content-type", "application/x-www-form-urlencoded")
                            .setHeader("Accept-Encoding", "application/gzip")
                            .setHeader("X-RapidAPI-Key", "354a3b0fc8mshd40cc9e85c8306bp164d92jsnd095b9f54ac9")
                            .setHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                            .setBody("source=it&target=" + lingua + "&q=" + testo)
                            .execute()
                            .toCompletableFuture()
                            .get(); // Blocking call to get the response

                    if (response.getStatusCode() == 200) {
                        responseText = response.getResponseBody();
                    } else {
                        System.out.println("Error: " + response.getStatusText());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                            try {
                                client.close();
                            } catch (IOException ex) {
                                Logger.getLogger(TranslateModule.class.getName()).log(Level.SEVERE, null, ex);
                            }
                }

                // Now you can work with the 'responseText' variable
                if (responseText != null) {
                    // Handle the response text
                    System.out.println("Response: " + responseText);
                } else {
                    // Handle the case when response is null
                    System.out.println("Response is null");
                }


                try {
                    // Parse the JSON response
                    JSONObject jsonObject = new JSONObject(responseText);

                    // Extract the "translations" array
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    org.json.JSONArray translationsArray = dataObject.getJSONArray("translations");

                    // Get the first item from the translations array
                    JSONObject translationObject = translationsArray.getJSONObject(0);

                    // Extract the "translatedText" value
                    String translatedText = translationObject.getString("translatedText");

                    // Print the translated text
                    System.out.println("Translated Text: " + translatedText);
                    m.setText(translatedText);
                } catch (JSONException e) {
                    // Handle any parsing errors
                    System.out.println("Error parsing JSON: " + e.getMessage());
                }
                
        }
        else{
            super.activate();
        }
        System.out.println(m.getText());
        return m;
        
    }
    
}
