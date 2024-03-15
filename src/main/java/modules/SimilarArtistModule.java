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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class SimilarArtistModule extends BotModule{

    public SimilarArtistModule() {
        super("/similarArtist");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        String ex = "";
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        System.out.println(s);
        if(s.equalsIgnoreCase("/close")){
            m.setText("/similiarArtist chiuso");
                super.deactivate();
            }
        else if(this.isActive()){
            
            String target[] = update.getMessage().getText().trim().split(",");
            String artist = target[0].trim(); 
            int num = parseInt(target[1].trim()); 
            String formattedArtist = artist.replace(" ", "+");
            
            String url = "https://www.music-map.com/map-search.php?f=" + formattedArtist;
            try {
                Document doc = Jsoup.connect(url).get();
                Element gnodMap = doc.getElementById("gnodMap");
                Elements links = gnodMap.select("a");

                // Metto tutti gli artisti eccetto il primo che era quello di input
                for (int i = 1; i < num+1; i++) {
                    Element link = links.get(i);
                    String text = link.text();
                    System.out.println("Text: " + text);
                    ex += i + ".\t" + text + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            m.setText(ex);
            
        
            
        }else{
            m.setText("Inserisci artista , numero");
            super.activate();
        }
        
        return m;
    }
    
}
