package common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Created by chaojiewang on 11/25/17.
 */
public class Util {
    private static final Logger logger = LogManager.getLogger(Util.class);

    public static String exceptionStacks(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * see if host is reachable. might take a long time.
     * @param url
     * @return
     */
    public static boolean isReachable(String url) {
        try {
            URL urlObj = new URL(url);
            InetAddress inet = InetAddress.getByName(urlObj.getHost());
            return inet.isReachable(2000);
        } catch (Exception e) {
            logger.warn(e);
            return false;
        }
    }

    public static boolean pingHttpUrl(String httpUrl) {
        try (Socket socket = new Socket()) {
            URL url = new URL(httpUrl);
            socket.connect(new InetSocketAddress(url.getHost(), url.getPort()), 2000);
            return true;
        } catch (Exception e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    /**
     *
     * @param obj
     * @param field
     * @return null on error
     */
    public static String safeGetString(JSONObject obj, String field) {
        try {
            return obj.getString(field);
        } catch (JSONException e) {
//            logger.debug(e);
            return null;
        }
    }
}
