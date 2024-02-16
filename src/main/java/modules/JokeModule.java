package modules;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import interfaces.BotModule;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class JokeModule extends BotModule{

    String s = "";
    public JokeModule() {
        super("/joke");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        // TODO: stampare manuale / gestire errore ecc..
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        
        
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        client.prepare("GET", "https://world-of-jokes1.p.rapidapi.com/v1/jokes/random-joke")
            .setHeader("X-RapidAPI-Key", "354a3b0fc8mshd40cc9e85c8306bp164d92jsnd095b9f54ac9")
            .setHeader("X-RapidAPI-Host", "world-of-jokes1.p.rapidapi.com")
            .execute()
            .toCompletableFuture()
            .thenAccept(response -> {
                //JsonParser parser = new JsonParser();
                String jsonString = response.getResponseBody();
                JsonObject jsonObject = JsonParser.parseString​(jsonString).getAsJsonObject();
                String body = jsonObject.get("body").getAsString();
                String title = jsonObject.get("title").getAsString();
                //System.out.println("Body: " + body);
                s = title + body;
                
                
            })
            .join();
        
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(JokeModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        m.setText(s);

        return m;
    }
    
}
