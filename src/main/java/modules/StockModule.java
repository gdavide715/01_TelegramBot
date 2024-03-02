/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class StockModule extends BotModule{
    String filePath = "stock.txt";
    File file = new File(filePath);
    static List<String> stock = new ArrayList<String>();
    
    public StockModule() {
        super("/stock");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        System.out.println(s);
        if(s.equalsIgnoreCase("/close")){
                super.deactivate();
            }
        else if(this.isActive()){
            
                
            if(s.startsWith("/add")){
                String target[] = s.split(",");
                for(int i=1; i<target.length; i++){
                    try {
                        appendToFile(file, target[i].trim());
                        stock.add(target[i].trim());
                        
                    } catch (IOException ex) {
                        Logger.getLogger(StockModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                m.setText("Stock Aggiunto");
            }
            
            
            if(s.startsWith("/remove")){
                String target[] = s.split(",");
                for(int i=0; i<target.length; i++){
                    try {
                        removeFromFile(file, target[i].trim());
                        
                        
                    } catch (IOException ex) {
                        Logger.getLogger(StockModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                m.setText("Stock tolto");
            }
            
            if(s.equalsIgnoreCase("/info")){
                String info = "";
                for(int i=0; i<stock.size(); i++){
                    try {
                        // Create URL
                        URL url = new URL("https://finnhub.io/api/v1/quote?symbol=" + stock.get(i).trim() + "&token=cnf4r8pr01qi6ftoagagcnf4r8pr01qi6ftoagb0");
                        System.out.println(stock.get(i).trim());
                        // Create connection
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        // Get response
                        int responseCode = connection.getResponseCode();
                        System.out.println("Response Code: " + responseCode);

                        // Read response
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Remove curly braces and format JSON
                        String formattedResponse = response.toString()
                                .replaceAll("[{}]", "") // Remove curly braces
                                .replaceAll(",", ",\n"); // Add newline after commas

                        // Print formatted response
                        info += stock.get(i).trim() + "\n" + formattedResponse + "\n\n";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                }
                String guida = "(c: prezzo attuale, d: differenza, dp: percentuale differenza, h: prezzo più alto della giornata, l: prezzo più basso della giornata, o: prezzo di apertura)\n\n" + s + "\n\n";                      
                String t = guida + info;
                m.setText(t);
                
                
            }
        }else{
            // Specify the file path
                try {
                    // Check if the file already exists
                    if (!file.exists()) {
                        // Create the new file if it doesn't exist
                        file.createNewFile();
                        System.out.println("File created successfully.");
                    }else{
                        String g[] = getTextFromFile(file).split(" ");
                        for(int i=0; i<g.length; i++){
                            if(stock.contains(g[i])){
                                
                            }else{
                                stock.add(g[i]);
                            }
                            
                        }
                        
                        System.out.println(stock);
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            m.setText("Comandi:\n/add, nomeStock -> per aggiungere lo stock\n/remove, nomeStock -> per togliere lo stock\n/info -> per vedere info \n\n(esempi: https://stockanalysis.com/stocks/)");
            //System.out.println(m.getText());
            super.activate();
        }
        
        return m;
    }
    
    public static void writeToFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("Content has been written to the file.");
        }
    }
    
    public static void removeFromFile(File file, String stringToRemove) throws IOException {
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                // Remove only exact matches of the stringToRemove
                String[] words = line.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    if (!words[i].equals(stringToRemove)) {
                        fileContent.append(words[i]).append(" ");
                    }
                }
            }
            
        }
        
        stock.remove(stringToRemove);

        // Write the updated content back to the file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(fileContent.toString());
            System.out.println("String '" + stringToRemove + "' removed from the file.");
        }
    }
    
     // Method to add a string to the file
    public static void appendToFile(File file, String stringToAdd) throws IOException {
        try (FileWriter writer = new FileWriter(file, true)) {
            // Append the string with a comma and a space
            writer.write(" " + stringToAdd);
            
            System.out.println("String '" + stringToAdd + "' added to the file.");
        }
    }
    
    public static String getTextFromFile(File file) throws IOException {
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
        }

        return fileContent.toString().trim();
    }
    
}
