package org.tyaa.ticketbookeremulator;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tyaa.ticketbookeremulator.impl.TicketBooker;

import java.io.IOException;
import java.util.Map;

public class WebActivity extends AppCompatActivity {

    private WebView mWebView;
    private final String BASE_URL = "https://www.onetwotrip.com/ru/poezda/";
    //private AppCompatActivity mSelf;
    private Connection.Response mCurrentResp;
    private Document mCurrentDoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //mSelf = this;
        setContentView(R.layout.activity_web);
        mWebView = (WebView) findViewById (R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

        Thread downloadThread = new Thread() {
            public void run() {

                try {
                    mCurrentResp =
                        Jsoup.connect(BASE_URL)
                                .method(Connection.Method.GET)
                                .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Map<String, String> cookies = res.cookies();
                if (mCurrentResp == null){
                    Log.e("error", "Http request error");
                }
                try {
                    mCurrentDoc = mCurrentResp.parse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mCurrentDoc == null) {
                    Log.e("error", "Wep page parsing error");
                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (Map.Entry entry: mCurrentResp.cookies().entrySet()) {
                                Log.d("cookie: ", entry.getKey() + " = " + entry.getValue());
                            }
                        }
                    });
                    /*final Document docCopy = doc;
                    // post a new Runnable from a Handler in order to run the WebView loading code from the UI thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //mWebView.loadData(element.html(), "text/html", "UTF-8");
                            mWebView.loadDataWithBaseURL(BASE_URL, docCopy.html(), "text/html", "UTF-8", null);
                        }
                    });*/
                }
            }
        };
        downloadThread.start();

        /*mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }
        });*/


        //TODO apply some compat method instead this for sdkVersion < 17
        /*mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
        mWebView.loadUrl(BASE_URL);

        TicketBooker.setBooked(true);*/
        //Toast.makeText(this, String.valueOf(TicketBooker.isBooked()), Toast.LENGTH_LONG).show();
    }

    /*private class MyJavaScriptInterface {
        @JavascriptInterface
        public void handleHtml(String html) {
            // Use jsoup on this String here to search for your content.
            Document doc = Jsoup.parse(html);

            // Now you can, for example, retrieve a div with id="username" here
            Element submitButton = doc.select("button[type=submit]").first();
            Toast.makeText(mSelf, submitButton.text(), Toast.LENGTH_LONG).show();
        }
    }*/
}
