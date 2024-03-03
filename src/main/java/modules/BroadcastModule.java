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
import static java.lang.Long.parseLong;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static modules.StockModule.getTextFromFile;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class BroadcastModule extends BotModule{
    String filePath = "utenti.txt";
    File file = new File(filePath);
    static List<Long> utenti = new ArrayList<>();
    public BroadcastModule() {
        super("/broadcast");
    }

    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        System.out.println(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        System.out.println(s);
        
        if(s.equalsIgnoreCase("/close")){
            //Inizio if close
            m.setChatId(update.getMessage().getChatId());
            m.setText("/broadcast chiuso");
            super.deactivate();
            return m;
        }//Fine if close
        else if(this.isActive()){
            //Inizio if isActive
            if(s.equalsIgnoreCase("/iscriviti")){             
                m.setChatId(update.getMessage().getChatId());
                long id = update.getMessage().getChatId();
                if(utenti.contains(id)){
                    m.setText("Già iscritto");
                    
                }else{
                    //appendToFile(file, id);
                    utenti.add(id);
                    
                }
                return m;
            }else if(s.startsWith("/br")){
                String target[] = s.split(",");
                System.out.println(target[1]);
                for (Long chatId : utenti) {
                    // Setting the text
                    m.setText(target[1].trim());

                    // Setting the chat ID from the current chatId
                    m.setChatId(chatId.toString());

                    // Returning the modified object m
                }
                return m;
            }else{
                m.setText("Scegli comando valido");
                return m;
            }
            
        }//Fine if isActive
        else{
            //Inizio else
            // Specify the file path
            
            m.setChatId(update.getMessage().getChatId());
                try {
                    // Check if the file already exists
                    if (!file.exists()) {
                        // Create the new file if it doesn't exist
                        file.createNewFile();
                        System.out.println("File created successfully.");
                    }else{
                        String g[] = getTextFromFile(file).split(",");
                        for(int i=0; i<g.length; i++){
                            if(utenti.contains(g[i])){
                                
                            }else{
                                utenti.add(parseLong(g[i]));
                            }
                            
                        }
                        
                        System.out.println(utenti);
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            m.setText("Comandi:\n/iscriviti -> per iscriverti al broadcast\n/br -> per mandare messaggi in broadcast");
            super.activate();
            return m;
        }//Fine else
        
        
    }
    
    
    
    public static void writeToFile(File file, long content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write((int) content);
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
        
        utenti.remove(stringToRemove);

        // Write the updated content back to the file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(fileContent.toString());
            System.out.println("String '" + stringToRemove + "' removed from the file.");
        }
    }
    
     // Method to add a string to the file
    public static void appendToFile(File file, long stringToAdd) throws IOException {
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
