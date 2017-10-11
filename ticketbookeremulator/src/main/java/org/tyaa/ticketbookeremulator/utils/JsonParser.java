package org.tyaa.ticketbookeremulator.utils;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yurii on 11.10.17.
 */

public class JsonParser {

    @Nullable
    public static Integer parseCity(String _cityJsonString) throws JSONException {

        Integer cityIdInteger = null;
        JSONObject cityJSONObject = null;

        cityJSONObject = new JSONObject(_cityJsonString);

        if (cityJSONObject != null
                && cityJSONObject.getBoolean("success") == true) {

            JSONArray result = cityJSONObject.getJSONArray("result");

            if (result.length() > 0) {

                JSONObject cityIdJSONObject = result.getJSONObject(0);
                cityIdInteger = cityIdJSONObject.getInt("id");
            }
        }

        return cityIdInteger;
    }

    @Nullable
    public static Map<String, String> parseTrains(String _trainsJsonString) throws JSONException {

        Map<String, String> trainsMap = new TreeMap<>();
        JSONObject trainsJSONObject = null;

        trainsJSONObject = new JSONObject(_trainsJsonString);

        if (trainsJSONObject != null
                && trainsJSONObject.getBoolean("success") == true) {

            JSONArray result = trainsJSONObject.getJSONArray("result");
            if (result.length() > 0) {

                for (int i=0; i < result.length(); i++) {

                    JSONObject trainJSONObject = result.getJSONObject(i);
                    trainsMap.put(
                            trainJSONObject.getString("trainNumber")
                            ,trainJSONObject.getString("deeplink"));
                }
            } else {

                trainsMap = null;
            }
        }

        return trainsMap;
    }
}
