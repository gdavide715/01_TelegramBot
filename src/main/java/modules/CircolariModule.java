package modules;

import interfaces.BotModule;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import org.telegram.telegrambots.meta.api.objects.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.jsoup.select.Elements;

public class CircolariModule extends BotModule {

    String url = "https://iislonato.edu.it/circolare";

    public CircolariModule() {
        super("/circolari");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {

        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String s = update.getMessage().getText();
        
        if(s.equalsIgnoreCase("/close")){
            super.deactivate();
        }else if(super.isActive()){
            int num = parseInt(update.getMessage().getText());

        try {
            Document doc = Jsoup.connect(url).get();
            Elements circolari = doc.select(".col-lg-7.offset-lg-1.pt84 .presentation-card-link"); // Seleziona tutti i link delle circolari all'interno della sezione specificata

            String response = "";
            int count = 0;

            for (Element circolare : circolari) {
                if (count >= num) { // Esce dopo aver trovato le prime tre circolari
                    break;
                }

                String nomeCircolare = circolare.select(".card-article-content .h3").text(); // Seleziona il testo all'interno dell'elemento h3
                String dataPubblicazione = circolare.select(".card-body .date .year").text() + "-"
                        + circolare.select(".card-body .date .month").text() + "-"
                        + circolare.select(".card-body .date .day").text();

                if (!nomeCircolare.equals("")) {

                    response += "Titolo: " + nomeCircolare + "\nData: " + dataPubblicazione;
                    count++;

                    String linkCircolare = circolare.attr("href");

                    Document d = Jsoup.connect(linkCircolare).get();

                    // Selezioniamo l'elemento contenente il testo della circolare
                    Element circolareElement = d.selectFirst(".wysiwig-text");

                    // Estraiamo il testo dalla classe .wysiwig-text
                    String circolareText = circolareElement.text();

                    response += "\nTesto della circolare: " + circolareText;

                    // Selezioniamo l'elemento che contiene il link al PDF
                    Element pdfLinkElement = d.selectFirst("div.card-body a[href$=.pdf]");
                    System.out.println("pdfLinkElement: " + pdfLinkElement);

                    if (pdfLinkElement != null) {

                        // Estraiamo l'URL dal link
                        String pdfUrl = pdfLinkElement.attr("href");

                        System.out.println("pdfUrl: " + pdfUrl);
                        response += "\nPDF: " + pdfUrl + "\n\n";

                    }else{
                        response += "\nPDF: non presente\n\n";
                    }

                }

            }
            m.setText("Elenco circolari: \n" + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        }else{
            super.activate();
            m.setText("Inserisci numero di circolari:");
        }
            
        
        

        return m;
    }

}
