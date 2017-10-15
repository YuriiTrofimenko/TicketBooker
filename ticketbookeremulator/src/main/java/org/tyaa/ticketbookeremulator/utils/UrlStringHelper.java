package org.tyaa.ticketbookeremulator.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yurii on 15.10.17.
 *
 * Обработка адресных строк
 */

public class UrlStringHelper {

    /**
     * Извлечение карты параметров из адресной строки
     * */
    public static Map<String, String> splitQuery(String _urlString) throws UnsupportedEncodingException {

        Map<String, String> query_pairs = new LinkedHashMap<>();
        String[] pairs = _urlString.split("\\?");
        pairs = pairs[1].split("&");
        for (String pair : pairs) {

            //Log.i("pair", pair);
            int idx = pair.indexOf("=");
            query_pairs.put(
                URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                , URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            );
        }
        return query_pairs;
    }
}
