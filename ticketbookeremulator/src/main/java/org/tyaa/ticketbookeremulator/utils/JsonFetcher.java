package org.tyaa.ticketbookeremulator.utils;

import android.os.AsyncTask;

import org.tyaa.ticketbookeremulator.exception.FailJSONFetchException;
import org.unbescape.html.HtmlEscape;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by yurii on 11.10.17.
 */

public class JsonFetcher {

    private static String mJsonString;

    public static String fetchByUrl(String _urlString) throws FailJSONFetchException, ExecutionException, InterruptedException {


        return new AsyncRequest().execute(_urlString).get();
        /*HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = "";

        try {

            URL url = new URL(_urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("charset", "UTF-8");

            urlConnection.connect();

            int tryCounter = 0;
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
            }

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
            throw new FailJSONFetchException();
        }
        if (jsonString == null || jsonString.equals("")) {

            throw new FailJSONFetchException();
        }
        return jsonString;*/
    }

    private static class AsyncRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = "";

            try {

                URL url = new URL(arg[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("charset", "UTF-8");

                urlConnection.connect();

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
                //throw new FailJSONFetchException();
            }
            if (jsonString == null || jsonString.equals("")) {

                //throw new FailJSONFetchException();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String _jsonString) {
            super.onPostExecute(_jsonString);
            //mJsonString = _jsonString;
        }
    }
}
