import com.fasterxml.jackson.core.JsonProcessingException;
import interfaces.BotModule;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import modules.HelloModule;
import modules.HelpModule;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javassist.CtMethod.ConstParameter.string;
import modules.ImageModule;
import modules.Mp3Module;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class MultiApiTelegramBot extends TelegramLongPollingBot {

    private final String BOT_USERNAME = "@ChallengeSacconeBot";
    private final ArrayList<BotModule> modules;

    public MultiApiTelegramBot(ArrayList<BotModule> modules) {
        super("6640826991:AAF4T3HNgD6YRqbNiIa3GqKjk68pInoRxwI");
        this.modules = modules;
    }

    @Override
    public void onUpdateReceived(Update update) {

        BotApiMethod<Message> response;

        BotModule module = this.modules.parallelStream()
                .filter(
                        (m) -> m.isFired(update.getMessage().getText()) || m.isActive()
                )
                .findFirst()
                .orElseGet(() -> {
                    BotModule m = new HelpModule();
                    return m;
                });
        
        response = module.handleCommand(update);
        System.out.println();
        if(update.getMessage().getText().startsWith("/img")){
            
            try {
                SendPhoto sendPhoto = new SendPhoto();
                // Set the chat id where you want to send the photo
                sendPhoto.setChatId(update.getMessage().getChatId().toString());
                // Set the photo you want to send
                String target[] = update.getMessage().getText().trim().split(",");
                String comando = target[0].trim(); // Replace with your desired artist
                String cerca = target[1].trim(); // Replace with your desired song title
                String link = (String) ImageModule.link(cerca);
                sendPhoto.setPhoto(new InputFile(link));
                
                try {
                    // Execute the SendPhoto method
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } catch (JsonProcessingException ex) {
                Logger.getLogger(MultiApiTelegramBot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MultiApiTelegramBot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(update.getMessage().getText().startsWith("/shortLink")){
            SendAudio sendAudio = new SendAudio();
            sendAudio.setChatId(update.getMessage().getChatId().toString());
            String t = "";
            String target[] = update.getMessage().getText().trim().split(",");
            String comando = target[0].trim(); // Replace with your desired artist
            String cerca = target[1].trim(); // Replace with your desired song title
            try {
                t = (String) Mp3Module.link(cerca);
            } catch (JsonProcessingException ex) {
                Logger.getLogger(MultiApiTelegramBot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MultiApiTelegramBot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MultiApiTelegramBot.class.getName()).log(Level.SEVERE, null, ex);
            }
            sendAudio.setAudio(new InputFile(t));
            try {
                execute(sendAudio);
            } catch (TelegramApiException ex) {
                Logger.getLogger(MultiApiTelegramBot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        try {
            execute(response);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return this.BOT_USERNAME;
    }
}
