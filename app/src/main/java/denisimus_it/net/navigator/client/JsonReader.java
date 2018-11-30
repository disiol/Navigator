package denisimus_it.net.navigator.client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonReader {


    public static JSONObject read(final String url) throws IOException, JSONException {
        final InputStream is = new URL(url).openStream();
        try {
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = readAll(rd);
            final JSONObject json = new JSONObject(jsonText);
            is.close();
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(final Reader read) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        int copy;
        while ((copy = read.read()) != -1) {
            stringBuilder.append((char) copy);
        }
        return stringBuilder.toString();
    }


}