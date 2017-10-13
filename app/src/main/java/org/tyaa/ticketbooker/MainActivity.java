package org.tyaa.ticketbooker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.tyaa.ticketbookeremulator.exception.CityNotFoundException;
import org.tyaa.ticketbookeremulator.exception.FailJSONFetchException;
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
                    TicketBooker.getInstance().bookTicket(
                            mSelf
                            , "Москва"
                            , "Санкт-Петербург"
                            , date
                            , "020У"
                            , "СВ" //Купе СВ
                            , "12 вагон / 2К"
                            , 4);
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
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (data == null) {return;}
        Toast.makeText(mSelf, String.valueOf(TicketBooker.isBooked()), Toast.LENGTH_SHORT).show();
    }

}
