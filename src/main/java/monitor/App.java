package monitor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dob.ComplaintsMonitor;
import dob.ComplaintsSource;
import email.GmailProxy;
import email.IEmailProxy;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Created by chaojiewang on 11/12/17.
 */
public class App {
    private static IEmailProxy emailProxy;
    private static App instance = new App();
    public static App getApp() {
        return instance;
    }
    private ComplaintsMonitor complaintsMonitor;
    private Set<String> emailRecipients;

    private App() {
        try {
            Properties properties;

            String conf = System.getenv("CONF_FILE");
            if (conf == null) {
                conf = "./conf/app.ini";
            }

            properties = new Properties();
            properties.load(new FileInputStream(new File(conf)));

            // email setting
            Properties gmailProperties = new Properties();
            gmailProperties.setProperty("HOST", properties.getProperty("GMAIL_HOST"));
            gmailProperties.setProperty("SSL_PORT", properties.getProperty("GMAIL_SSL_PORT"));
            gmailProperties.setProperty("USERNAME", properties.getProperty("GMAIL_USERNAME"));
            gmailProperties.setProperty("PASSWORD", properties.getProperty("GMAIL_PASSWORD"));
            gmailProperties.setProperty("RECIPIENTS", properties.getProperty("GMAIL_RECIPIENTS"));

            GmailProxy.configure(gmailProperties);
            emailProxy = GmailProxy.getInstance();

            String recipients = gmailProperties.getProperty("GMAIL_RECIPIENTS");
            emailRecipients = new HashSet<>();
            if (recipients != null) {
                for (String r : recipients.split(",")) {
                    r = r.trim();
                    if (!r.equals(""))
                        emailRecipients.add(r);
                }
            }

            // API
            ComplaintsSource source = new ComplaintsSource(properties.getProperty("COMPLAINTS_API"), properties.getProperty("OPEN_NYC_TOKEN"));

            HikariConfig config = new HikariConfig();
            config.setUsername(properties.getProperty("DB_USERNAME").trim());
            config.setPassword(properties.getProperty("DB_PASSWORD").trim());
            config.setJdbcUrl(properties.getProperty("DB_URL").trim());
            config.setDriverClassName(properties.getProperty("DB_DRIVER").trim());
            DataSource dob = new HikariDataSource(config);

            complaintsMonitor = new ComplaintsMonitor(source, dob);
            String heartbeat = properties.getProperty("MONITOR_HEARTBEAT");
            if (heartbeat != null)
                complaintsMonitor.setHeartbeat(Integer.parseInt(heartbeat));

            String complaintViewUrl = properties.getProperty("COMPLAINTS_VIEW_URL");
            if (complaintViewUrl != null)
                complaintsMonitor.setComplaintViewUrl(complaintViewUrl);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void run() throws Exception {
        complaintsMonitor.start();
    }

    public Set<String> getEmailRecipients() {
        return emailRecipients;
    }
    ////////////////////////
    public static void main(String args[]) throws Exception {
        try {
            getApp().run();
        } catch (Exception e) {
            for (String recipient : getApp().getEmailRecipients()) {
                emailProxy.sendEmail(recipient, "DOBMonitor Stopped Working", e.toString());
            }
        }
    }
}
