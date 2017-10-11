package org.tyaa.ticketbookeremulator.exception;

/**
 * Created by yurii on 11.10.17.
 */

public class CityNotFoundException extends Exception {

    private String mCityName;

    public CityNotFoundException(String _cityName) {

        mCityName = _cityName;
    }

    @Override
    public String getMessage() {

        return "City not found";
    }

    public String getCityName() {

        return mCityName;
    }
}
