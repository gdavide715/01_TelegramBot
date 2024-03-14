/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import static modules.ImageModule.link;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class NMAPModule extends BotModule{

    public NMAPModule() {
        super("/nmap");
    }

    @Override
    public PartialBotApiMethod<?> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId().toString());       
        String s = update.getMessage().getText();
        
        if(s.equalsIgnoreCase("/close")){
                m.setText("/nmap chiuso");
                super.deactivate();
                
            }
        else if(this.isActive()){
            
            String apiKey = "fb5fa6e542844e5dbc335fefc6476036d080d550";
            String host = s.trim();
            String outputType = "json";
            String res = "";
            
            try {
            // Constructing the URL
            String baseUrl = "https://api.viewdns.info/portscan/?";
            String queryParams = String.format("host=%s&apikey=%s&output=%s", host, apiKey, outputType);
            URL url = new URL(baseUrl + queryParams);
            
            // Creating HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Reading the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Printing the response
            System.out.println("Response: " + response.toString());
             String jsonResponse = "{\"query\" : {\"tool\" : \"portscan_PRO\",\"host\" : \"8.8.8.8\"},\"response\" : {\"port\" : [{\"number\" : \"21\", \"service\" : \"FTP\", \"status\" : \"closed\"},{\"number\" : \"22\", \"service\" : \"SSH\", \"status\" : \"closed\"},{\"number\" : \"23\", \"service\" : \"Telnet\", \"status\" : \"closed\"},{\"number\" : \"25\", \"service\" : \"SMTP\", \"status\" : \"closed\"},{\"number\" : \"53\", \"service\" : \"DNS\", \"status\" : \"open\"},{\"number\" : \"80\", \"service\" : \"HTTP\", \"status\" : \"closed\"},{\"number\" : \"110\", \"service\" : \"POP3\", \"status\" : \"closed\"},{\"number\" : \"139\", \"service\" : \"NETBIOS\", \"status\" : \"closed\"},{\"number\" : \"143\", \"service\" : \"IMAP\", \"status\" : \"closed\"},{\"number\" : \"443\", \"service\" : \"HTTPS\", \"status\" : \"open\"},{\"number\" : \"445\", \"service\" : \"SMB\", \"status\" : \"closed\"},{\"number\" : \"1433\", \"service\" : \"MSSQL\", \"status\" : \"closed\"},{\"number\" : \"1521\", \"service\" : \"ORACLE\", \"status\" : \"closed\"},{\"number\" : \"3306\", \"service\" : \"MySQL\", \"status\" : \"closed\"},{\"number\" : \"3389\", \"service\" : \"Remote Desktop\", \"status\" : \"closed\"}]}}";

            // Parse JSON response
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            JsonObject response2 = jsonObject.getAsJsonObject("response");
            JsonArray ports = response2.getAsJsonArray("port");

            // Iterate through ports and print service and status
            for (JsonElement port : ports) {
                JsonObject portObject = port.getAsJsonObject();
                String service = portObject.get("service").getAsString();
                String status = portObject.get("status").getAsString();
                res += "Service: " + service + ", Status: " + status + "\n";
            }
            
            //System.out.println(res);

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
            
            m.setText(res);
            
        }else{
            
            
            //System.out.println(m.getText());
            m.setText("Inserisci IP da scannare (/close -> per chiudere):");
            super.activate();
            
            
        }
        return m;
    }
    
}
