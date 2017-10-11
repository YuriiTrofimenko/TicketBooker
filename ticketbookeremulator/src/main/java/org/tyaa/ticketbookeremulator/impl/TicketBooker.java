package org.tyaa.ticketbookeremulator.impl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import org.tyaa.ticketbookeremulator.WebActivity;
import org.tyaa.ticketbookeremulator.exception.FailJsonFetchException;
import org.tyaa.ticketbookeremulator.interfaces.TicketBookerInterface;
import org.tyaa.ticketbookeremulator.utils.JsonFetcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by yurii on 07.10.17.
 */

public class TicketBooker implements TicketBookerInterface {

    private static boolean mBooked;
    private static TicketBooker mInstance;

    private TicketBooker() {
        mBooked = false;
    }

    public static TicketBooker getInstance(){
        if (mInstance == null) {mInstance = new TicketBooker();}
        return mInstance;
    }

    @Override
    public void bookTicket(AppCompatActivity sender, String from, String to, Date date, int pos, String trainNumber, String carType, String carNumber, int seatNumber) {

        Intent intent = new Intent(sender, WebActivity.class);
        sender.startActivity(intent);
    }

    public static void setBooked(boolean _booked) {
        mBooked = _booked;
    }

    public static boolean isBooked() {
        return mBooked;
    }

    public Integer getCityIdByName(String _cityName)
            throws FailJsonFetchException {

        String cityString = null;
        String jsonString = null;
        Integer cityInteger = null;

        try {
            cityString = URLEncoder.encode(_cityName, "utf-8");
            if (cityString != null) {
                jsonString =
                        JsonFetcher.fetchByUrl(
                                "https://www.onetwotrip.com/_api/rzd/suggestStations?searchText="
                                        + cityString
                                        + "&type=station");

                if (jsonString != null) {

                    cityInteger = JsonParser.parseCity(jsonString);
                }

            }
        } catch (UnsupportedEncodingException ex) {}
        return cityInteger;
    }
}
