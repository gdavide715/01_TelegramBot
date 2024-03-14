/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author taluk
 */
public class IndiceMusicModule extends BotModule{

    public IndiceMusicModule() {
        super("/music");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        m.setText("/mp3 -> inserire link per mp3 🎸\n/lyrics -> testo canzone 🎵\n/similarArtist -> artisti simili 🧑‍🎤\n/close ->per chiudere");
        this.deactivate();
        return m;
    }
    
}
