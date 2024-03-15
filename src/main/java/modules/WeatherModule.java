package modules;

import okhttp3.Response;

import interfaces.BotModule;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class WeatherModule extends BotModule {

    private static final String OPENWEATHERMAP_API_KEY = "25ee1e4024a847e1889efc82cc5aad67";
    private boolean awaitingForecast = false;
    private boolean awaitingCity = false;
    private String lastLocation = "";

    public WeatherModule() {
        super("/weather");

    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        super.activate();
        SendMessage m = new SendMessage();
        if (super.isActive()) {

            m.setChatId(update.getMessage().getChatId());
            String messageText = update.getMessage().getText().trim();

            if (messageText.equals("/weather")) {
                awaitingForecast = false;
                awaitingCity = false;
                m.setText("/forecast --> per sapere il meteo dei prossimi 4 giorni \n /city --> per avere il meteo attuale di una città a tua scelta");
            } else if (messageText.equals("/forecast")) {
                awaitingForecast = true;
                awaitingCity = false;
                m.setText("Inserisci il nome della città per ottenere il meteo.");
            } else if (messageText.equals("/city")) {
                awaitingForecast = false;
                awaitingCity = true;
                m.setText("Inserisci il nome della città per ottenere il meteo attuale.");
            } 
            else {
                if (awaitingForecast) {
                    lastLocation = messageText;
                    String forecastResponse = getWeatherForecast(messageText);
                    m.setText(forecastResponse);
                    awaitingForecast = false;
                    super.deactivate();
                } else if (awaitingCity) {
                    // Logica per ottenere il meteo attuale della città
                    lastLocation = messageText;
                    String weatherResponse = getCurrentWeather(messageText);
                    m.setText(weatherResponse);
                    awaitingCity = false;
                    super.deactivate();
                } else {
                    m.setText("Comando non riconosciuto. \n /forecast --> per sapere il meteo dei prossimi 4 giorni \n /city --> per avere il meteo attuale di una città a tua scelta");
                }
            }
        }
        return m;
    }

    private String getCurrentWeather(String location) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + OPENWEATHERMAP_API_KEY + "&lang=it")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            JSONObject json = new JSONObject(responseData);
            if (json.has("main") && json.has("weather")) {
                JSONObject main = json.getJSONObject("main");
                JSONArray weatherArray = json.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                double temperature = main.getDouble("temp") - 273.15; // Converti da Kelvin a Celsius
                String description = weather.getString("description");

                return "Attualmente a " + location + ":\nTemperatura: " + temperature + "°C\nDescrizione: " + description;
            } else {
                return "Non è stato possibile ottenere le informazioni sul meteo per " + location + ".";
            }
        } catch (IOException ex) {
            Logger.getLogger(WeatherModule.class.getName()).log(Level.SEVERE, null, ex);
            return "Si è verificato un errore durante la richiesta del meteo per " + location + ".";
        }
    }

    private String getWeatherForecast(String location) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/forecast?q=" + location + "&appid=" + OPENWEATHERMAP_API_KEY + "&lang=it")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            JSONObject json = new JSONObject(responseData);
            JSONArray forecasts = json.getJSONArray("list");

            StringBuilder forecastText = new StringBuilder("Previsioni del tempo per i prossimi 4 giorni a " + location + ":\n");

            for (int i = 0; i < forecasts.length(); i++) {
                JSONObject forecast = forecasts.getJSONObject(i);
                long timestamp = forecast.getLong("dt");
                JSONObject main = forecast.getJSONObject("main");
                double temperature = main.getDouble("temp") - 273.15;
                JSONArray weatherArray = forecast.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String description = weather.getString("description");

                // Convert tempo in data leggibile
                String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(timestamp * 1000));

                forecastText.append("Data: ").append(dateString).append("\n");
                forecastText.append("Temperatura: ").append(temperature).append("°C\n");
                forecastText.append("Descrizione: ").append(description).append("\n\n");
            }

            return forecastText.toString();
        } catch (IOException | JSONException ex) {
            Logger.getLogger(WeatherModule.class.getName()).log(Level.SEVERE, null, ex);
            return "Si è verificato un errore durante la richiesta del meteo per " + location + ".";
        }
    }

}
