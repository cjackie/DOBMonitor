package email;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by chaojiewang on 11/18/17.
 */
public class TestGmailProxy {

    @Before
    public void setup() throws Exception {
        Assume.assumeNotNull(System.getenv("CONF_FILE"));

        String confFile = System.getenv("CONF_FILE");
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(confFile)) {
            properties.load(in);
        }
        GmailProxy.configure(properties);
    }

    @Test
    public void sendEmail() throws Exception {
        GmailProxy.getInstance().sendEmail("chaojie.dev@gmail.com", "hi", "something");
    }
}
