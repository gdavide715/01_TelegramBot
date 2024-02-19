import interfaces.BotModule;
import modules.HelloModule;
import modules.HelpModule;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

public class MultiApiTelegramBot extends TelegramLongPollingBot {

    private final String BOT_USERNAME = "@SacconeChallengeBot";
    private final ArrayList<BotModule> modules;

    public MultiApiTelegramBot(ArrayList<BotModule> modules) {
        super("6908885694:AAHkxpt2HC3jHPbE1XPcX64Hqh0ACiWxwUo");
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
