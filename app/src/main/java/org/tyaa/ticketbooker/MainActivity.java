package org.tyaa.ticketbooker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.tyaa.ticketbookeremulator.exception.CityNotFoundException;
import org.tyaa.ticketbookeremulator.exception.FailJSONFetchException;
import org.tyaa.ticketbookeremulator.exception.IncorrectPassengersNumberException;
import org.tyaa.ticketbookeremulator.exception.TrainNotFoundException;
import org.tyaa.ticketbookeremulator.exception.TrainsNotFoundException;
import org.tyaa.ticketbookeremulator.impl.TicketBooker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    AppCompatActivity mSelf;
    private Button mCallTicketBookerButton;
    private Boolean mBooked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mSelf = this;
        setContentView(R.layout.activity_main);
        mCallTicketBookerButton = (Button) findViewById(R.id.callTicketBookerButton);

        mCallTicketBookerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mSelf, String.valueOf(TicketBooker.isBooked()), Toast.LENGTH_SHORT).show();
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                String dateInString = "01112017";
                Date date = new Date();
                try {
                    date = sdf.parse(dateInString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    //Комбинации параметров для тестирования:
                    //1. Купе - 12 вагон / 2К - 4
                    //1-1. Купе - 10 вагон / 2К - 4
                    //2. Купе - 2 вагон / 2К - 6
                    //3. СВ - 4 вагон / 1У
                    //4. СВ - 6 вагон / 1Б
                    TicketBooker.getInstance().bookTicket(
                            mSelf
                            , "Москва"
                            , "Санкт-Петербург"
                            , date
                            , "020У" //020У; 016А
                            , "Купе" //Купе; СВ; Плацкарт (016А - 10 вагон / 3Э)
                            , "10 вагон / 2К" //Купе - 12 вагон / 2К; 2 вагон / 2К; СВ - 4 вагон / 1У, 6 вагон / 1Б
                            , 2);
                    //Вывод значений количества пассажиров по умолчанию
                    Log.i("adult", (String.valueOf(TicketBooker.SeatDetail.getAdultCount())));
                    Log.i("children", (String.valueOf(TicketBooker.SeatDetail.getChildrenCount())));
                    Log.i("young_children", (String.valueOf(TicketBooker.SeatDetail.getYoungChildrenCount())));
                } catch (FailJSONFetchException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TrainsNotFoundException e) {
                    e.printStackTrace();
                } catch (TrainNotFoundException e) {
                    e.printStackTrace();
                } catch (CityNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (IncorrectPassengersNumberException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (data == null) {return;}
        //Вывод результата работы модуля - забронирован ли билет
        Toast.makeText(
            mSelf
            , String.valueOf(TicketBooker.isBooked())
            , Toast.LENGTH_SHORT).show();

        //Вывод значений количества пассажиров после бронирования места или отмены
        Log.i("adult", (String.valueOf(TicketBooker.SeatDetail.getAdultCount())));
        Log.i("children", (String.valueOf(TicketBooker.SeatDetail.getChildrenCount())));
        Log.i("young_children", (String.valueOf(TicketBooker.SeatDetail.getYoungChildrenCount())));

        //Вывод уведомлений о том, что модуль завершил работу автоматически
        //из-за получения некорректных или неактуальных данных
        //о типе вагона / номере вагона / номере места
        if(TicketBooker.mCarTypeNotFound == true)
                Log.i("NotFound", "CarTypeNotFound");
        if(TicketBooker.mCarNumberNotFound == true)
            Log.i("NotFound", "CarNumberNotFound");
        if(TicketBooker.mSeatNotFound == true)
            Log.i("NotFound", "SeatNotFound");
    }

}
