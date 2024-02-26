package modules;

import interfaces.BotModule;
import java.io.BufferedReader;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public class DavidGogginsModule extends BotModule {

    public DavidGogginsModule() {
        super("/goggins");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());

        String videoUrl = getRandomVideoUrlFromChannel("UCzr1IGDjo81uM5WaUW_br2A");
        m.setText("Ecco un video casuale: " + videoUrl);
        return m;
    }

    private String getRandomVideoUrlFromChannel(String channelId) {
        String apiKey = "AIzaSyAmnmiJsoc1fBooYN46f3cs6IF2svHqPyo";
        String apiUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&key=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray items = jsonResponse.getJSONArray("items");

                // Estrai un video casuale dall'elenco dei video
                Random random = new Random();
                int randomIndex = random.nextInt(items.length());
                JSONObject randomVideo = items.getJSONObject(randomIndex);
                JSONObject snippet = randomVideo.getJSONObject("snippet");
                String videoId = randomVideo.getJSONObject("id").getString("videoId");

                // Costruisci l'URL del video
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                return videoUrl;
            } else {
                System.out.println("Errore nella richiesta API a YouTube: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
