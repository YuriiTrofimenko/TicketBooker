package org.tyaa.ticketbookeremulator.impl;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.tyaa.ticketbookeremulator.WebActivity;
import org.tyaa.ticketbookeremulator.exception.CityNotFoundException;
import org.tyaa.ticketbookeremulator.exception.FailJSONFetchException;
import org.tyaa.ticketbookeremulator.exception.IncorrectPassengersNumberException;
import org.tyaa.ticketbookeremulator.exception.TrainNotFoundException;
import org.tyaa.ticketbookeremulator.exception.TrainsNotFoundException;
import org.tyaa.ticketbookeremulator.interfaces.TicketBookerInterface;
import org.tyaa.ticketbookeremulator.utils.JsonFetcher;
import org.tyaa.ticketbookeremulator.utils.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by yurii on 07.10.17.
 *
 * Запускает активность для эмуляции бронирования билета и
 * выдает результаты процесса бронирования
 */

public class TicketBooker implements TicketBookerInterface {

    private static boolean mBooked;
    private static TicketBooker mInstance;
    public static final String BASE_URL = "https://www.onetwotrip.com/";
    public static final String TRAIN_LINK = "org.tyaa.ticketbookeremulator.impl.TicketBooker.TrainLink";
    public static final String SEAT_NUMBER = "org.tyaa.ticketbookeremulator.impl.TicketBooker.SeatNumber";
    public static final String CAR_TYPE = "org.tyaa.ticketbookeremulator.impl.TicketBooker.CarType";
    public static final String CAR_NUMBER = "org.tyaa.ticketbookeremulator.impl.TicketBooker.CarNumber";

    //Проверять после завершения работы модуля - возможно, он завершился из-за получения
    //некорректных или неактуальных входных данных
    public static boolean mCarTypeNotFound = false;
    public static boolean mCarNumberNotFound = false;
    public static boolean mSeatNotFound = false;

    private TicketBooker() {

        mBooked = false;
    }

    public static TicketBooker getInstance(){

        if (mInstance == null) {mInstance = new TicketBooker();}

        return mInstance;
    }

    @Override
    public void bookTicket(
            AppCompatActivity _sender
            , @NonNull String _from
            , @NonNull String _to
            , @NonNull Date _date
            , @NonNull String _trainNumber
            , @NonNull String _carType
            , @NonNull String _carNumber
            , @NonNull Integer _seatNumber)
            throws FailJSONFetchException
            , JSONException
            , TrainsNotFoundException
            , TrainNotFoundException
            , CityNotFoundException, ExecutionException, InterruptedException, IncorrectPassengersNumberException {

        //Установка состояния по умолчанию
        mBooked = false;
        mCarTypeNotFound = false;
        mCarNumberNotFound = false;
        mSeatNotFound = false;
        //Число пассажиров по умолчанию - 1 взрослый, 0 детей от 5 лет, 0 детей до 5 лет
        SeatDetail.setState(1,0,0);

        Map<String, String> trainsMap = getTrainsMap(_from, _to, _date);

        if (trainsMap != null) {

            String trainLink = trainsMap.get(_trainNumber);

            if (trainLink != null){

                Intent intent = new Intent(_sender, WebActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(TRAIN_LINK, trainLink);
                //TODO check seat number
                intent.putExtra(SEAT_NUMBER, _seatNumber);
                intent.putExtra(CAR_TYPE, _carType);
                intent.putExtra(CAR_NUMBER, _carNumber);
                _sender.startActivityForResult(intent, 0);
            } else {

                throw new TrainNotFoundException();
            }
        } else {

            throw new TrainsNotFoundException();
        }
    }

    public static void setBooked(boolean _booked) {
        mBooked = _booked;
    }

    /**
     * Получение результата работы модуля - забронировал ли пользователь билет
     * */
    public static boolean isBooked() {
        boolean result = mBooked;
        //Не удалять. Версия со сбросом результата после первого его чтения
        //if (mBooked == true){mBooked = false;}
        return result;
    }

    @Nullable
    private Integer getCityIdByName(String _cityName)
            throws FailJSONFetchException, JSONException, ExecutionException, InterruptedException {

        String cityString = null;
        String jsonString = null;
        Integer cityInteger = null;

        try {

            cityString = URLEncoder.encode(_cityName, "utf-8");

            if (cityString != null) {
                jsonString =
                        JsonFetcher.fetchByUrl(
                                BASE_URL
                                + "_api/rzd/suggestStations?"
                                + "searchText="
                                + cityString
                                + "&type=station"
                        );

                if (jsonString != null) {

                    cityInteger = JsonParser.parseCity(jsonString);
                }

            }
        } catch (UnsupportedEncodingException ex) {}

        return cityInteger;
    }

    @Nullable
    private Map<String, String> getTrainsMap(String _from, String _to, Date _date)
            throws FailJSONFetchException
            , JSONException
            , CityNotFoundException
            , ExecutionException
            , InterruptedException, IncorrectPassengersNumberException {

        Map<String, String> trainsMap = null;
        Integer fromCityId = getCityIdByName(_from);
        if (fromCityId == null){throw new CityNotFoundException(_from);}
        Integer toCityId = getCityIdByName(_to);
        if (toCityId == null){throw new CityNotFoundException(_to);}
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String dateString = dateFormat.format(_date);

        String jsonString =
                JsonFetcher.fetchByUrl(
                        BASE_URL
                        + "_api/rzd/metaTimetable/?"
                                + "from="
                                + fromCityId
                                + "&to="
                                + toCityId
                                + "&source=web"
                                + "&date="
                                + dateString
                );

        if (jsonString != null) {

            trainsMap = JsonParser.parseTrains(jsonString);
        }

        return trainsMap;
    }

    /**
     * Класс-набор дополнительной информации о месте в вагоне
     * */
    public static class SeatDetail {

        private static int mAdultCount = 1;
        private static int mChildrenCount = 0;
        private static int mYoungChildrenCount = 0;

        public static void setState(
                int _adultCount
                , int _childrenCount
                , int _youngChildrenCount)
                throws IncorrectPassengersNumberException {

            int totalPeopleCount =
                _adultCount + _childrenCount + _youngChildrenCount;

            if ((totalPeopleCount > 4) || (totalPeopleCount < 1)) {

                throw new IncorrectPassengersNumberException();
            }
            SeatDetail.mAdultCount = _adultCount;
            SeatDetail.mChildrenCount = _childrenCount;
            SeatDetail.mYoungChildrenCount = _youngChildrenCount;
        }

        public static int getAdultCount() {
            return mAdultCount;
        }

        public static int getChildrenCount() {
            return mChildrenCount;
        }

        public static int getYoungChildrenCount() {
            return mYoungChildrenCount;
        }
    }
}
