package email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Created by chaojiewang on 11/18/17.
 */
public class GmailProxy implements IEmailProxy {
    private static GmailProxy ourInstance = new GmailProxy();
    private static Logger logger = LogManager.getLogger(GmailProxy.class);
    public static GmailProxy getInstance() {
        return ourInstance;
    }

    private static Session emailSession;
    private static String username;
    private GmailProxy() {
    }

    /**
     *
     * @param properties
     *             HOST: ,
     *             SSL_PORT: ,
     *             USERNAME: ,
     *             PASSWORD: ;
     *
     *
     * @throws Exception
     */
    public static void configure(Properties properties) throws Exception {
        Properties sessionProps = new Properties();
        sessionProps.setProperty("mail.smtp.host", properties.getProperty("HOST"));
        sessionProps.setProperty("mail.smtp.socketFactory.port", properties.getProperty("SSL_PORT"));
        sessionProps.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        sessionProps.setProperty("mail.smtp.auth", "true");
        sessionProps.setProperty("mail.smtp.port", "465");


        Session session = Session.getDefaultInstance(sessionProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("USERNAME").trim(), properties.getProperty("PASSWORD").trim());
            }
        });

        username = properties.getProperty("USERNAME").trim();
        emailSession = session;
    }

    @Override
    public void sendEmail(String to, String subject, String content) throws Exception {
        if (emailSession == null) {
            throw new IllegalStateException("GmailProxy needs configuration");
        }

        Message message = new MimeMessage(emailSession);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");
        Transport.send(message);
    }
}
