package modules;

import okhttp3.Response;

import interfaces.BotModule;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class WeatherModule extends BotModule{

    private static final String OPENWEATHERMAP_API_KEY = "25ee1e4024a847e1889efc82cc5aad67";
    
    public WeatherModule() {
        super("/weather");
        super.activate();
    }
    

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        
        SendMessage m = new SendMessage();
        if(super.isActive()){
            
            
            m.setChatId(update.getMessage().getChatId());
            
            String location = update.getMessage().getText().trim();
            if(!location.equals("/weather")){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                    .url("http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + OPENWEATHERMAP_API_KEY)
                    .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException ex) {
                    Logger.getLogger(WeatherModule.class.getName()).log(Level.SEVERE, null, ex);
                }
                String responseData = null;
                try {
                    responseData = response.body().string();
                } catch (IOException ex) {
                    Logger.getLogger(WeatherModule.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject json = new JSONObject(responseData);
                if (json.has("main") && json.has("weather")) {
                    JSONObject main = json.getJSONObject("main");
                    JSONArray weatherArray = json.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    double temperature = main.getDouble("temp") - 273.15; // Converti da Kelvin a Celsius
                    String description = weather.getString("description");

                    m.setText("Attualmente a " + location + ":\nTemperatura: " + temperature + "°C\nDescrizione: " + description);
                } else {
                    m.setText("Non è stato possibile ottenere le informazioni sul meteo per " + location + ".");
                }
                super.deactivate();
            } else {
                m.setText("Inserisci città");
            }
        }
        return m;
        
    }
    
}
