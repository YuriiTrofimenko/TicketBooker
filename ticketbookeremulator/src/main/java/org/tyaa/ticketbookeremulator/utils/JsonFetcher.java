package org.tyaa.ticketbookeremulator.utils;

import org.tyaa.ticketbookeremulator.exception.FailJsonFetchException;
import org.unbescape.html.HtmlEscape;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yurii on 11.10.17.
 */

public class JsonFetcher {

    public static String fetchByUrl(String _urlString) throws FailJsonFetchException{

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = "";

        try {

            URL url = new URL(_urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("charset", "UTF-8");


            /*int tryCounter = 0;
            while (tryCounter < 30) {

                try {

                    tryCounter++;
                    urlConnection.connect();
                    break;
                } catch (Exception ex) {

                    if (tryCounter > 29) {

                        out.println("Great fatal net error!");
                        throw ex;
                    }
                }
            }*/

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            jsonString = HtmlEscape.unescapeHtml(buffer.toString());
        } catch (Exception e) {

            e.printStackTrace();
            throw new FailJsonFetchException();
        }
        if (jsonString == null || jsonString.equals("")) {

            throw new FailJsonFetchException();
        }
        return jsonString;
    }
}
