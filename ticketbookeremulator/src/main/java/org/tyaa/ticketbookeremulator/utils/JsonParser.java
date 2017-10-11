package org.tyaa.ticketbookeremulator.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yurii on 11.10.17.
 */

public class JsonParser {

    public static Integer parseCity(String _cityJsonString){

        Integer cityIdInteger = null;
        JSONObject cityJSONObject = null;

        try {
            cityJSONObject = new JSONObject(_cityJsonString);
        } catch (JSONException ex) {
            //e.printStackTrace();
            System.err.println("There are some bad symbols in the JSON data");
        }
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
}
