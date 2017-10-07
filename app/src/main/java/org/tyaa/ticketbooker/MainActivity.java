package org.tyaa.ticketbooker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.tyaa.ticketbookeremulator.impl.TicketBooker;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    AppCompatActivity mSelf;
    Button mCallTicketBookerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelf = this;
        setContentView(R.layout.activity_main);
        mCallTicketBookerButton = (Button) findViewById(R.id.callTicketBookerButton);
        mCallTicketBookerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mSelf, String.valueOf(TicketBooker.isBooked()), Toast.LENGTH_SHORT).show();
                TicketBooker.getInstance().bookTicket(mSelf, "Moscow", "Peter", new Date(), 1, "", "", "", 1);
            }
        });
    }
}
