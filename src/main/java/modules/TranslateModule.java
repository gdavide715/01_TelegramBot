package modules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Response;

import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.ws.rs.sse.SseEventSource.target;
import static kotlin.concurrent.ThreadsKt.thread;
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
    String translation = "";
    
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
                m.setText("/traduci chiuso");
                super.deactivate();
        
        }else if(this.isActive()){
                
                AsyncHttpClient client = new DefaultAsyncHttpClient();
                String responseText = null;
                String target[] = update.getMessage().getText().trim().split(",");
                String from = target[0].trim();
                String to = target[1].trim();
                String testo = target[2].trim();
        
        
        String url = "https://api.microsofttranslator.com/V2/Ajax.svc/Translate?appId=DB50E2E9FBE2E92B103E696DCF4E3E512A8826FB&oncomplete=?&text=" + testo.replace(" ", "+") + "&from=" + from + "&to=" + to;

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TranslateModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        CompletableFuture<String> translationResult = responseFuture.thenApply(response -> {
            
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                
                Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(responseBody);
                if (matcher.find()) {
                    return matcher.group(1); // Extract the text inside quotes
                } else {
                    return "Error: Translation not found in response";
                }
            } else {
                return "Error: " + response.statusCode(); // Handle error cases
            }
            });

            translation = translationResult.join();

            m.setText(translation);

            }
        else{
            super.activate();
            String list = "\n- Af\n" +
        "- Ar\n" +
        "- Bg\n" +
        "- Ca\n" +
        "- Zh-yue\n" +
        "- Zh\n" +
        "- Zh-TW\n" +
        "- Hr\n" +
        "- Cs\n" +
        "- Da\n" +
        "- Nl\n" +
        "- En\n" +
        "- Et\n" +
        "- Fil\n" +
        "- Fi\n" +
        "- Fr\n" +
        "- De\n" +
        "- El\n" +
        "- He\n" +
        "- Hi\n" +
        "- Hu\n" +
        "- Is\n" +
        "- It\n" +
        "- Ja\n" +
        "- Ko\n" +
        "- Lv\n" +
        "- Lt\n" +
        "- Ms\n" +
        "- Mt\n" +
        "- No\n" +
        "- Fa\n" +
        "- Pl\n" +
        "- Pt\n" +
        "- Ro\n" +
        "- Ru\n" +
        "- Sr\n" +
        "- Sk\n" +
        "- Sl\n" +
        "- Es\n" +
        "- Sw\n" +
        "- Sv\n" +
        "- Th\n" +
        "- Tr\n" +
        "- Uk\n" +
        "- Vi\n" +
        "- Cy\n";

            m.setText("Inserisci: lingua della frase da tradure, lingua frase tradotta, testo\nLingue disponibili: " + list);
        }
        System.out.println(m.getText());
        
        return m;
    }
    
}
