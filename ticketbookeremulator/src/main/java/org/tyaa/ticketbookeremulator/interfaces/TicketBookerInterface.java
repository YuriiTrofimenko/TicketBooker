package org.tyaa.ticketbookeremulator.interfaces;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.tyaa.ticketbookeremulator.exception.CityNotFoundException;
import org.tyaa.ticketbookeremulator.exception.FailJSONFetchException;
import org.tyaa.ticketbookeremulator.exception.IncorrectPassengersNumberException;
import org.tyaa.ticketbookeremulator.exception.TrainNotFoundException;
import org.tyaa.ticketbookeremulator.exception.TrainsNotFoundException;

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by yurii on 07.10.17.
 */

public interface TicketBookerInterface {

    /**
     * Заказать билет на основании подготовленных данных. Вызывается из клиентского кода
     */
    void bookTicket(
            AppCompatActivity sender
            , String from
            , String to
            , Date date
            , String trainNumber
            , String carType
            , String carNumber
            , Integer seatNumber
    ) throws FailJSONFetchException, JSONException, TrainsNotFoundException, TrainNotFoundException, CityNotFoundException, ExecutionException, InterruptedException, IncorrectPassengersNumberException;
}
