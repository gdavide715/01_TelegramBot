import interfaces.BotModule;
import modules.HelloModule;
import modules.HelpModule;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import modules.CircolariModule;
import modules.CocktailModule;
import modules.CurrencyModule;
import modules.DavidGogginsModule;
import modules.DictionaryModule;
import modules.ITQuestionModule;
import modules.ImageModule;
import modules.IndiceLanguageModule;
import modules.IndiceMusicModule;
import modules.IndiceQuoteModule;
import modules.JokeModule;
import modules.LyricsModule;
import modules.Mp3Module;
import modules.NewsModule;
import modules.QuoteModule;
import modules.QuoteTrumpModule;
import modules.RecipeModule;
import modules.ShortLinkModule;
import modules.SimilarArtistModule;
import modules.StockModule;
import modules.TempMailModule;
import modules.TranslateModule;
import modules.WeatherModule;
import modules.WorkoutModule;

public class Main {



    public static void main(String[] args) throws TelegramApiException {

        TelegramBotsApi bot = new TelegramBotsApi(DefaultBotSession.class);

        ArrayList<BotModule> modules = new ArrayList<>(Arrays.asList(
                // TODO: registrare tutti i moduli presenti
                new HelloModule(),
                new HelpModule(),
                new JokeModule(),
                new WeatherModule(),
                new TranslateModule(),
                new QuoteModule(),
                new CocktailModule(),
                new DictionaryModule(),
                new LyricsModule(),
                new QuoteTrumpModule(),
                new ImageModule(),
                new RecipeModule(),
                new WorkoutModule(),
                new CurrencyModule(),
                new Mp3Module(),
                new DavidGogginsModule(),
                new CircolariModule(),
                new NewsModule(),
                new ShortLinkModule(),
                new StockModule(),
                new IndiceQuoteModule(),
                new IndiceLanguageModule(),
                new IndiceMusicModule(),
                new SimilarArtistModule(),
                new TempMailModule(),
                new ITQuestionModule()
        ));

        bot.registerBot(new MultiApiTelegramBot(modules));

    }
}

