package dob;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import email.GmailProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


/**
 * Created by chaojiewang on 11/18/17.
 */
public class TestComplaintsMonitor {
    Logger logger = LogManager.getLogger(TestComplaintsMonitor.class);
    HikariDataSource source;
    ComplaintsMonitor monitor;
    ComplaintsSource complaintsSource;
    Set<String> emailRecipients;
    String complaintsViewUrl;
    boolean ready = false;

    @Before
    public void setup() throws Exception {
        Assume.assumeNotNull(System.getenv("CONF_FILE"));

        String conf = System.getenv("CONF_FILE");

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(conf)) {
            props.load(in);
        }

        HikariConfig config = new HikariConfig();
        config.setUsername(props.getProperty("DB_USERNAME"));
        config.setPassword(props.getProperty("DB_PASSWORD"));
        config.setJdbcUrl(props.getProperty("DB_URL"));
        config.setDriverClassName(props.getProperty("DB_DRIVER"));
        complaintsViewUrl = props.getProperty("COMPLAINTS_VIEW_URL");

        source = new HikariDataSource(config);
        source.getConnection();

        Assume.assumeNotNull(System.getenv("GMAIL_CONF"));
        Properties gmailProps = new Properties();
        try (FileInputStream in = new FileInputStream(System.getenv("GMAIL_CONF"))){
            gmailProps.load(in);
        }

        GmailProxy.configure(gmailProps);

        try {
            Properties properties = new Properties();
            try (FileInputStream in = new FileInputStream(new File(System.getenv("OPEN_NYC_CONF")))) {
                properties.load(in);
                complaintsSource = new ComplaintsSource(properties.getProperty("COMPLAINTS_API"), properties.getProperty("OPEN_NYC_TOKEN"));
                complaintsSource.getComplaintByBin("test");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        String recipients = gmailProps.getProperty("GMAIL_RECIPIENTS");
        emailRecipients = new HashSet<>();
        if (recipients != null) {
            for (String r : recipients.split(",")) {
                r = r.trim();
                emailRecipients.add(r);
            }
        }

        ready = true;
    }

    @Test
    public void test() throws  Exception {
        Assume.assumeTrue(ready);

        monitor = new ComplaintsMonitor(complaintsSource, source);
        monitor.setComplaintViewUrl(complaintsViewUrl);
        monitor.start();
        monitor.join();
    }
}
