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
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class Mp3Module extends BotModule{

    public Mp3Module() {
        super("/mp3");
    }

    @Override
    public PartialBotApiMethod<?> handleCommand(Update update) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(update.getMessage().getChatId().toString());
        String downloadUrl = "";
        String s = update.getMessage().getText();
        
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
            }
        else if(this.isActive()){
            try {
            // URL della richiesta
            String urlString = "https://yt-cw.fabdl.com/youtube/get?url=" + s + "&mp3_task=2";
            URL url = new URL(urlString);
            
            // Apertura della connessione
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // Lettura della risposta
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Parsare la risposta JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            
            // Ottenere l'oggetto "results" dalla risposta JSON
            JSONObject results = jsonResponse.getJSONObject("result");
            
            // Ottenere il valore di mp3_download_url dall'oggetto "results"
            String mp3DownloadUrl = results.getString("get_mp3_download_url");
            
            // Stampare il valore
            System.out.println("mp3_download_url: " + mp3DownloadUrl);
            
            // Chiusura della connessione
            URL mp3Url = new URL(mp3DownloadUrl);
            HttpURLConnection mp3Connection = (HttpURLConnection) mp3Url.openConnection();
            mp3Connection.setRequestMethod("GET");

            // Lettura della risposta
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(mp3Connection.getInputStream()));
            StringBuilder response2 = new StringBuilder();
            String line2;
            while ((line2 = reader2.readLine()) != null) {
                response2.append(line2);
            }
            reader.close();

            // Stampare la risposta
            /*
            System.out.println("Response from mp3DownloadUrl:");
            System.out.println(response2.toString());*/
            JSONObject jsonResponse2 = new JSONObject(response2.toString());
            
            // Ottenere l'oggetto "result" dalla risposta JSON
            JSONObject result2 = jsonResponse2.getJSONObject("result");
            
            // Ottenere il valore di "download_URL" dall'oggetto "result"
            downloadUrl = result2.getString("download_url");
            
            // Stampare il valore di "download_URL"
            System.out.println("download_URL: " + downloadUrl);
            
            // Chiusura della connessione
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
            sendAudio.setAudio(new InputFile(downloadUrl));
            
        }else{
            
            
            //System.out.println(m.getText());
            super.activate();
        }
        return sendAudio;
    }
    
}
