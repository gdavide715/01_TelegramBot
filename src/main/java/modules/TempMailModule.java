/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import interfaces.BotModule;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.mailslurp.apis.InboxControllerApi;
import com.mailslurp.apis.WaitForControllerApi;
import com.mailslurp.clients.ApiClient;
import com.mailslurp.clients.ApiException;
import com.mailslurp.clients.Configuration;
import com.mailslurp.models.Email;
import com.mailslurp.models.InboxDto;
import com.mailslurp.models.InboxPreview;
import com.mailslurp.models.PageInboxProjection;
import com.mailslurp.models.SendEmailOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author taluk
 */
public class TempMailModule extends BotModule{

    public TempMailModule( ) {
        super("/tempMail");
    }

    @Override
    public BotApiMethod<Message> handleCommand(Update update) {
        Object id = null;
        SendMessage m = new SendMessage();
        m.setChatId(update.getMessage().getChatId());
        String MAIL_KEY = "c0127c245309a49d9bace480973cbf7c4b3e51374d4ae21212daf4beb7252f3d";
        String s = update.getMessage().getText();
            if(s.equalsIgnoreCase("/close")){
                m.setText("/tempMail chiuso");
                super.deactivate();
            }
            else if(this.isActive()){
                try {
                // create a MailSlurp client with your API_KEY
                ApiClient defaultClient = Configuration.getDefaultApiClient();
                defaultClient.setApiKey(MAIL_KEY);
                InboxControllerApi inboxControllerApi = new InboxControllerApi(defaultClient);
                InboxDto inbox = null;
                if(s.equalsIgnoreCase("/create")){
                    inbox = inboxControllerApi.createInboxWithDefaults();
                    m.setText(inbox.getEmailAddress());
                    id = inbox.getId();
                }

                if(s.equalsIgnoreCase("/emails")){
                    
                    WaitForControllerApi waitForControllerApi = new WaitForControllerApi(defaultClient);
                    boolean UNREAD_ONLY;
                    long TIMEOUT_MILLIS = 3 * 1000;
                    Email email = waitForControllerApi.waitForLatestEmail((UUID) id, TIMEOUT_MILLIS, UNREAD_ONLY = false, null, null, null, null);

                    Pattern p = Pattern.compile("Your code is: ([0-9]{3})");
                    Matcher m1 = p.matcher(email.getBody());
                    m1.find();

                    String code = m1.group(1);
                    m.setText(code);
                    
                }
            

            } catch (ApiException ex) {
                Logger.getLogger(TempMailModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{

            m.setText("Inserisci comando:\n/create -> creare mail\n/emails -> get inbox\n/close -> per chiudere");
            //System.out.println(m.getText());
            super.activate();
        }
        return m;
    }
    
}
