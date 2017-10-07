package org.tyaa.ticketbookeremulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import org.tyaa.ticketbookeremulator.impl.TicketBooker;

public class WebActivity extends AppCompatActivity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWebView = (WebView) findViewById (R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("https://www.onetwotrip.com/ru/poezda/");
        TicketBooker.setBooked(true);
        Toast.makeText(this, String.valueOf(TicketBooker.isBooked()), Toast.LENGTH_LONG).show();
    }
}
