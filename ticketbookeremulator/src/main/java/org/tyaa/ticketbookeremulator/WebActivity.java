package org.tyaa.ticketbookeremulator;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tyaa.ticketbookeremulator.impl.TicketBooker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class WebActivity extends AppCompatActivity {

    private WebView mWebView;
    private AppCompatActivity mSelf;
    //private Connection.Response mCurrentResp;
    //private Document mCurrentDoc;
    /**
     * Текущее значение адресной строки - для определения направления переходов пользователя
     * по страницам внутри WebView
     * */
    private String mCurrentURLString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mSelf = this;

        //1. Получаем строки "откуда", "куда" и "когда", выполняем запрос JSON:
        //https://www.onetwotrip.com/_api/rzd/metaTimetable/?from=22823&to=22871&source=web&date=01112017

        //2. В ответе находим такой элемент массива, у которого есть заданный trainNumber,
        //берем из него значение deeplink

        //3. На основе значения deeplink выполняем запрос HTML (загружаем ответ в WebView ...:
        //https://www.onetwotrip.com/ru/poezda/train/?date=01112017&train=116%D0%A1&fromName=%D0%90%D0%B4%D0%BB%D0%B5%D1%80&toName=%D0%A1%D0%B0%D0%BD%D0%BA%D1%82-%D0%9F%D0%B5%D1%82%D0%B5%D1%80%D0%B1%D1%83%D1%80%D0%B3&metaTo=22871&metaFrom=22913&classes[0]=3&classes[1]=4&classes[2]=6&from=2000001&to=2004006

        //4. ..., выполняем внедренный JS - выбираем тип, номер вагона и место, блокируем их)
        // открытие списка типов - button._2fHmX _1W9qx _3D1rc
        //type item - div.oNY5p (content: div._3m4PA > div._3dsoa, selected: div.oNY5p _1SguN)
        //https://www.onetwotrip.com/ru/poezda/pay/b92c946a-23d1-4186-97b0-120c2d894396?metaFrom=22823&metaTo=22871&date=01112017&fromName=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&toName=%D0%A1%D0%B0%D0%BD%D0%BA%D1%82-%D0%9F%D0%B5%D1%82%D0%B5%D1%80%D0%B1%D1%83%D1%80%D0%B3
        //POST
        //https://www.onetwotrip.com/_api/rzd/bookManager
        //success	true        result	{…}        bookId	b92c946a-23d1-4186-97b0-120c2d894396

        //5. обрабатываем событие навигации WebView. Сравниваем прошлый URL с наступающим.
        // Если прошлый не содержал подстроку "poezda/pay", а новый содержит -
        // устанавливаем флаг "Забронировано", если наоборот - сбрасываем его

        //6. обрабатываем событие  закрытия веб-активности

        String trainLink;
        Integer seatNumber;
        String carType;
        String carNumber;

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();

            if(extras == null) {

                trainLink = null;
                seatNumber = null;
                carType = null;
                carNumber = null;
            } else {

                trainLink = extras.getString(TicketBooker.TRAIN_LINK);
                seatNumber = extras.getInt(TicketBooker.SEAT_NUMBER);
                carType = extras.getString(TicketBooker.CAR_TYPE);
                carNumber = extras.getString(TicketBooker.CAR_NUMBER);
            }
        } else {

            trainLink = (String) savedInstanceState.getSerializable(TicketBooker.TRAIN_LINK);
            seatNumber = (Integer) savedInstanceState.getSerializable(TicketBooker.SEAT_NUMBER);
            carType = (String) savedInstanceState.getSerializable(TicketBooker.CAR_TYPE);
            carNumber = (String) savedInstanceState.getSerializable(TicketBooker.CAR_NUMBER);
        }

        final String seatNumberString = seatNumber.toString();
        final String carTypeString = carType;
        final String carNumberString = carNumber;

        setContentView(R.layout.activity_web);
        mWebView = (WebView) findViewById (R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

        //TODO apply some compat method instead of this for sdkVersion < 17
        //mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {

                /*mWebView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>')");*/

                /* Внедрение самозапускающейся функции javascript */
                //TODO show alert when any target element is not accessed
                mWebView.loadUrl("javascript:(function () {" +
                        // Функция для эмуляции событий
                        "function eventFire(el, etype){" +
                            "if (el.fireEvent) {" +
                                "el.fireEvent('on' + etype);" +
                            "} else {" +
                                "var evObj = document.createEvent('Events');" +
                                "evObj.initEvent(etype, true, false);" +
                                "el.dispatchEvent(evObj);" +
                            "}" +
                            //"alert(el + ' ' + etype);" +
                        "}" +
                        "window.scrollTo(0,document.body.scrollHeight);"+
                        // Клик по кнопке списка типов вагонов
                        "var typeListTag = document.querySelector('button._2fHmX._1W9qx._3D1rc');" +
                        //"var typeListOnClick = document.querySelector('button._2fHmX._1W9qx._3D1rc').getAttribute('onclick');" +
                        //"alert(typeListTag.click);" +
                        "eventFire(typeListTag, 'touchstart');" +
                        "eventFire(typeListTag, 'touchend');" +
                        //"typeListTag.click();"+
                        //"typeListTag.toggleSelectVisibility();" +
                        //"eval(typeListOnClick);" +

                        "var typeTags = document.querySelectorAll('div._3dsoa');" +
                        //"var searchTypeText = '" + carTypeString + "';" +
                        "alert(typeTags.length);" +
                        //"for (var i = 0; i < typeTags.length; i++) {" +

                            //"if (typeTags[i].textContent == searchTypeText) {" +
                                //"eventFire(typeTags[i].parentNode.parentNode, 'click');" +
                                //"break;" +
                            //"}" +
                        //"}" +

                        "var seatTags = document.querySelectorAll('div._3jQmm > div:nth-child(1)');" +
                        "var searchSeatText = '" + seatNumberString + "';" +

                        "for (var i = 0; i < seatTags.length; i++) {" +
                            //"alert(seatTags[i].textContent + ' = ' + searchSeatText);" +
                            "if (seatTags[i].textContent == searchSeatText) {" +
                                "eventFire(seatTags[i].parentNode, 'click');" +
                            "}" +
                            "var old_seatTagParent = seatTags[i].parentNode;" +
                            "var new_seatTagParent = seatTags[i].parentNode.cloneNode(true);" +
                            "old_seatTagParent.parentNode.replaceChild(new_seatTagParent, old_seatTagParent);" +
                        "}" +
                    "})();");
            }

            //@TargetApi(15)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (!mCurrentURLString.contains("poezda/pay") && url.contains("poezda/pay")) {

                    TicketBooker.setBooked(true);
                } else if (mCurrentURLString.contains("poezda/pay") && !url.contains("poezda/pay")) {

                    TicketBooker.setBooked(false);
                }
                mCurrentURLString = url;
                return super.shouldOverrideUrlLoading(view, url);
            }

            /*@TargetApi(21)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                //if () {


                //}

                mCurrentURLString = request.getUrl().toString();

                return super.shouldOverrideUrlLoading(view, request);
            }*/
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //return super.onJsAlert(view, url, message, result);
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        WebActivity.this);
                builder.setMessage(message)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        }).show();
                result.cancel();
                return true;
            }
        });
        //mWebView.loadUrl(BASE_URL + "_api/rzd/trainSchemes?clientName=web&type=internal&train=116%D0%A1&from=2000001&to=2004006&date=01112017");
        //try {

            //mWebView.loadUrl(BASE_URL + "ru/poezda/train/?fromName="+ URLEncoder.encode("Москва", "UTF-8")+"&toName="+URLEncoder.encode("Санкт-Петербург", "UTF-8")+"&train=020У&from=2006004&to=2004001&classes[0]=4&classes[1]=6&minCost=2181.6&metaTo=22871&metaFrom=22823&date=01112017");
            mWebView.loadUrl(trainLink);
        //} catch (UnsupportedEncodingException e) {

        //    e.printStackTrace();
        //}

        //TicketBooker.setBooked(true);
        //Toast.makeText(this, String.valueOf(TicketBooker.isBooked()), Toast.LENGTH_LONG).show();
    }

    /*private class MyJavaScriptInterface {
        @JavascriptInterface
        public void handleHtml(String html) {
            // Use jsoup on this String here to search for your content.
            Document doc = Jsoup.parse(html);

            // Now you can, for example, retrieve a div with id="username" here
            //div._3jQmm > div:nth-child(1)
            //Element submitButton = doc.select("button[type=submit]").first();
            Element submitButton = doc.select("div._3jQmm > div:nth-child(1)").first();
            Toast.makeText(mSelf, submitButton.text(), Toast.LENGTH_LONG).show();
        }
    }*/
}
