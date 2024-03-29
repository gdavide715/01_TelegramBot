package modules;

import interfaces.BotModule;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import static java.lang.Integer.parseInt;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsModule extends BotModule {

    private static final String NEWS_API_KEY = "9b73d4fe2aff4247b54c821de4d5776d";

    public NewsModule() {
        super("/news");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        
        if(s.equalsIgnoreCase("/close")){
            response.setText("/news chiuso");
            super.deactivate();
        }else if(super.isActive()){
            String target[] = s.split(",");
        
            String c = target[0].trim();
            int n = parseInt(target[1].trim());

            String news = getTopHeadlines(c, n);
            response.setText("Ecco le ultime notizie:\n\n " + news);
        }else{
            String categories = "-business\n-entertainment\n-general\n-health\n-sciences\n-sports\n-technology\n";

            response.setText("Inserisci: categoria , numero di articoli \n\nPer esempio:\n" + categories + "\n(/close -> per chiudere) ");
            super.activate();
        }
        
        

        
        return response;
    }

    private String getTopHeadlines(String c, int n) {
        String category = c; // Puoi cambiare la categoria a tuo piacimento
        int count = n; // Numero di notizie da ottenere

        try {
            URL url = new URL("https://newsapi.org/v2/top-headlines?country=it&category=" + category + "&pageSize=" + count + "&apiKey=" + NEWS_API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "Errore nella richiesta alle notizie.";
            }

            InputStream inputStream = conn.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Analizza la risposta JSON e ottieni le notizie
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray articles = jsonResponse.getJSONArray("articles");
            StringBuilder newsText = new StringBuilder();
            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);
                String title = article.getString("title");
                String description = article.isNull("description") ? "Nessuna descrizione disponibile" : article.getString("description");
                String url1 = article.getString("url");
                newsText.append((i + 1) + ". " + title + "\n" + description + "\n" + url1 + "\n\n");
            }
            return newsText.toString();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "Errore durante il recupero delle notizie.";
        }
    }

}
