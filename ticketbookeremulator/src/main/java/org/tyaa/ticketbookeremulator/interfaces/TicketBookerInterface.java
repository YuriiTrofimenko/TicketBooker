package org.tyaa.ticketbookeremulator.interfaces;

import android.support.v7.app.AppCompatActivity;

import java.util.Date;

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
            , int pos
            , String trainNumber
            , String carType
            , String carNumber
            , int seatNumber
    );
}
