import interfaces.BotModule;
import modules.HelloModule;
import modules.HelpModule;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import modules.JokeModule;
import modules.TranslateModule;
import modules.WeatherModule;

public class Main {



    public static void main(String[] args) throws TelegramApiException {

        TelegramBotsApi bot = new TelegramBotsApi(DefaultBotSession.class);

        ArrayList<BotModule> modules = new ArrayList<>(Arrays.asList(
                // TODO: registrare tutti i moduli presenti
                new HelloModule(),
                new HelpModule(),
                new JokeModule(),
                new WeatherModule(),
                new TranslateModule()
        ));

        bot.registerBot(new MultiApiTelegramBot(modules));

    }
}

