package dob;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chaojiewang on 10/28/17.
 */
public class OpenNycSource {
    protected String apiUrl;
    protected String appToken;

    protected OpenNycSource(String apiUrl, String appToken) {
        this.apiUrl = apiUrl;
        this.appToken = appToken;
    }

    protected InputStream sendGetRequest() throws IOException {
        return sendGetRequest(new HashMap<>());
    }

    /**
     *
     * @param params paramter name to value mapping
     * @return
     */
    protected InputStream sendGetRequest(Map<String,String> params) throws IOException {
        StringBuilder urlStr = new StringBuilder(apiUrl);

        // appending request parameters
        boolean first = true;
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (first) {
                urlStr.append(String.format("?%s=%s", param.getKey(), param.getValue()));
                first = false;
            } else {
                urlStr.append(String.format("&%s=%s", param.getKey(), param.getValue()));
            }
        }

        // getting connection
        return sendGetRequest(urlStr.toString());
    }

    /**
     *
     * @param conditions
     * @return
     */
    protected InputStream sendGetRequest(List<String> conditions) throws IOException {
        StringBuilder urlStr = new StringBuilder(apiUrl);
        boolean first = true;
        for (String cond : conditions) {
            if (first) {
                urlStr.append("?").append(cond);
                first = false;
            } else {
                urlStr.append("&").append(cond);
            }
        }

        // getting connection
        return sendGetRequest(urlStr.toString());
    }

    private InputStream sendGetRequest(String url) throws IOException {
        // getting connection
        URL urlObj = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
        conn.setRequestProperty("Accepts", "json");
        if (appToken != null)
            conn.setRequestProperty("X-dob.App-Token", appToken);

        return conn.getInputStream();
    }
}
