package org.tyaa.ticketbookeremulator;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.tyaa.ticketbookeremulator.exception.CarNumberNotFoundException;
import org.tyaa.ticketbookeremulator.exception.IncorrectPassengersNumberException;
import org.tyaa.ticketbookeremulator.impl.TicketBooker;
import org.tyaa.ticketbookeremulator.utils.UrlStringHelper;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class WebActivity extends AppCompatActivity {

    private WebView mWebView;
    //private android.support.constraint.ConstraintLayout mWebContainer;
    //private AppCompatActivity mSelf;
    //private Connection.Response mCurrentResp;
    //private Document mCurrentDoc;
    //private  boolean mClearHistory = false;
    /**
     * Текущее значение адресной строки - для определения направления переходов пользователя
     * по страницам внутри WebView
     * */
    private String mCurrentURLString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //mSelf = this;

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

        //mWebContainer = (ConstraintLayout) findViewById(R.id.webContainer);
        setContentView(R.layout.activity_web);
        mWebView = (WebView) findViewById (R.id.webView);

        //mWebView.clearCache(true);
        //mWebView.clearHistory();
        //mWebView.loadUrl("about:blank");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        //only for sdkVersion < 17
        //mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {

                /*mWebView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>')");*/

                /* Внедрение самозапускающейся функции javascript */
                //TODO show alert when any target element is not accessed
                mWebView.loadUrl("javascript:(function () {" +
                    //"window.addEventListener('load', function () {" +

                        //form._2kLMm submit
                        //orderManager
                        //createOrder

                        "var placesResponseDone = false;"+
                        "var placesResponseCounter = 0;"+
                        "var necessarySetNumber = false;"+
                        // Функция для эмуляции событий
                        "function eventFire(el, etype){" +

                            "if (el != null && el.fireEvent) {" +
                                "el.fireEvent('on' + etype);" +
                            "} else if(el != null) {" +
                                "var evObj = document.createEvent('Events');" +
                                "evObj.initEvent(etype, true, false);" +
                                "el.dispatchEvent(evObj);" +
                            "}" +
                            //"alert(el + ' ' + etype);" +
                            //"console.log(el + ' ' + etype + ' ' + el.className + ' ' + el.click);"+
                            //"console.log(seatTags[i].parentNode.className);"+
                        "}" +
                        "(function setAjaxHandler () {"+
                            "var origOpen = XMLHttpRequest.prototype.open;"+

                            "XMLHttpRequest.prototype.open = function() {"+
                                "console.log('request started!');"+
                                "console.log('load = ' + this.load);"+
                                "if(this != null){"+
                                    "this.addEventListener('load', function() {"+
                                        "console.log('request completed!');"+
                                        "console.log(this.readyState);"+
                                        "console.log(this.responseText);"+
                                        "if(this.responseText.indexOf('places') !== -1){"+
                                            "placesResponseDone = true;"+
                                            "placesResponseCounter++;"+
                                        "}"+
                                    "});"+
                                    "origOpen.apply(this, arguments);"+
                                "}"+
                            "};"+
                        "})();"+

                        "window.scrollTo(0,document.body.scrollHeight);"+
                        "window.scrollTo(0,0);"+

                        "var containerTag;" +

                        // начать повторы с интервалом 0.01 сек
                        "var timerId = setInterval(function() {"+
                            "var containerTagTest = document.querySelector('div._29lBY');" +
                            // Определяем, является ли заданный тип вагона типом по умолчанию на странице
                            "var defaultCarTypeTag = document.querySelector('span._8zy8y');" +
                            // Определяем, является ли заданный номер вагона номером по умолчанию на странице
                            "var defaultCarNumberTag =" +
                            "document.querySelector(" +
                            "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1) > span:nth-child(1) > span:nth-child(2)');" +
                            "if (containerTagTest != null && defaultCarTypeTag != null && defaultCarTypeTag != null) {"+
                                "clearInterval(timerId);"+
                                "containerTag = containerTagTest;"+
                                "doWork();"+
                            "}"+
                        "}, 100);"+

                        //"var observer = new MutationObserver(function(mutations) {"+
                            //"mutations.forEach(function(mutation) {"+
                                //"if (!mutation.addedNodes) {return;}"+
                                //"for (var i = 0; i < mutation.addedNodes.length; i++) {"+
                                    //className
                                    // do things to your newly added nodes here
                                    //"var node = mutation.addedNodes[i];"+
                                    //"if(node.className.indexOf('_29lBY') !== -1){"+
                                        //"console.log('node.className = ' + node.className);"+
                                    //"}"+

                                //"}"+
                            //"});"+
                        //"});"+

                        //"observer.observe(document.body, {"+
                            //"childList: true"+
                            //", subtree: true"+
                            //", attributes: false"+
                            //", characterData: false"+
                        //"});"+

                        //"var containerTag = document.querySelector('div._29lBY');" +
                        //"if (containerTag != null) {"+
                            //"containerTag.addEventListener('DOMSubtreeModified', contentChanged, false);"+
                        //"}"+

                        //Установка обработчика события "изменение содержимого в контейнере"
                        //"var containerTag = document.querySelector('div._29lBY');" +
                        //"console.log('containerTag = ' + containerTag);"+
                        //"console.log('containerTag.DOMSubtreeModified = ' + containerTag.DOMSubtreeModified);"+
                        //"containerTag.addEventListener('DOMSubtreeModified', contentChanged, false);"+

                        "function doWork(){"+

                            //Функция выбора и фиксации заданного места.
                            //Вызывать только на странице в установившемся режиме
                            "function setSeat(){"+
                                "var seatTags = document.querySelectorAll('div._3jQmm > div:nth-child(1)');" +
                                "var searchSeatText = '" + seatNumberString + "';" +
                                "var seatFound = false;"+
                                "for (var i = 0; i < seatTags.length; i++) {" +
                                    //Место не существует или уже забронировано/в процессе бронирования
                                    "console.log('seatTags[i].parentNode = ' + seatTags[i].parentNode);"+
                                    "if ((seatTags[i].textContent == searchSeatText) " +
                                        "&& (seatTags[i].parentNode.className.indexOf('_13esh') == -1)) {" +
                                        "seatFound = true;"+
                                        "eventFire(seatTags[i].parentNode, 'click');" +
                                    "}" +
                                    "if(seatTags[i] != null && seatTags[i].parentNode != null){"+
                                        "var old_seatTagParent = seatTags[i].parentNode;" +
                                        "var new_seatTagParent = seatTags[i].parentNode.cloneNode(true);" +
                                        "old_seatTagParent.parentNode.replaceChild(new_seatTagParent, old_seatTagParent);" +
                                    "}"+
                                    //"seatOk = true;"+
                                "}" +
                                //Если места на странице выведены, но среди них нет заданного,
                                //или оно уже забронировано - показываем сообщение об этом
                                //и отправляем запрос-сигнал для завершения работы
                                "if(seatTags.length > 0 && !seatFound){"+
                                    "alert('Место не найдено, забронировано или в процессе бронирования');"+
                                    "setTimeout(function() {"+
                                        "var xhr = new XMLHttpRequest();"+
                                        "xhr.open('GET', 'seat_not_found');"+
                                        "xhr.send(null);"+
                                    "}, 3000);"+
                                "}"+
                            "}"+

                            // Определяем, является ли заданный тип вагона типом по умолчанию на странице
                            "var defaultCarTypeTag = document.querySelector('span._8zy8y');" +
                            // Определяем, является ли заданный номер вагона номером по умолчанию на странице
                            "var defaultCarNumberTag =" +
                                "document.querySelector(" +
                                    "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1) > span:nth-child(1) > span:nth-child(2)');" +
                            //"console.log('defaultCarNumberTag.textContent = ' + defaultCarNumberTag.textContent);"+
                            //Если обе указанные строки установлены по умолчанию - фиксируем их,
                            // выбираем заданное место и его также фиксируем
                            "console.log('defaultCarTypeTag: ' + defaultCarTypeTag.textContent);"+
                            "console.log('defaultCarNumberTag: ' + defaultCarNumberTag.textContent);"+
                            "if((defaultCarTypeTag.textContent === '" + carTypeString + "') " +
                                "&& (defaultCarNumberTag.textContent === '" + carNumberString + "')){"+

                                //Фиксируется кнопка выбора типа вагона
                                "var typeListTag = document.querySelector('button._2fHmX._1W9qx._3D1rc');" +
                                "if(typeListTag != null){"+
                                    "var old_typeListTag = typeListTag;" +
                                    "var new_typeListTag = typeListTag.cloneNode(true);" +
                                    "old_typeListTag.parentNode.replaceChild(new_typeListTag, old_typeListTag);" +
                                "}"+

                                //Фиксируется кнопка выбора номера вагона
                                "var numberListTag = " +
                                    "document.querySelector(" +
                                        "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)');" +
                                "if(numberListTag != null){"+
                                    "var old_numberListTag = numberListTag;" +
                                    "var new_numberListTag = numberListTag.cloneNode(true);" +
                                    "old_numberListTag.parentNode.replaceChild(new_numberListTag, old_numberListTag);" +
                                "}"+

                                //Место в вагоне
                                //"if(!seatOk){"+
                                    "setSeat();"+
                                //"}"+
                            "} "+
                            //Иначе, если тип вагона соответствует заданному,
                            // а номер нет - открываем список номеров, выбираем нужный, фиксируем его,
                            // дожидаемся рендеринга данных о местах, ?
                            // выбираем заданное место и его также фиксируем
                            "else if((defaultCarTypeTag.textContent == '" + carTypeString + "') " +
                                "&& (defaultCarNumberTag.textContent != '" + carNumberString + "')){"+

                                //Фиксируется кнопка выбора типа вагона
                                "var typeListTag = document.querySelector('button._2fHmX._1W9qx._3D1rc');" +
                                "if(typeListTag != null){"+
                                    "var old_typeListTag = typeListTag;" +
                                    "var new_typeListTag = typeListTag.cloneNode(true);" +
                                    "old_typeListTag.parentNode.replaceChild(new_typeListTag, old_typeListTag);" +
                                "}"+

                                "setNumber();"+

                                //Фиксируется кнопка выбора номера вагона
                                "var numberListTag = " +
                                    "document.querySelector(" +
                                        "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)');" +
                                //"console.log('old_numberListTag: ' + old_numberListTag);"+
                                "if(numberListTag != null){"+
                                    "var old_numberListTag = numberListTag;" +
                                    "var new_numberListTag = numberListTag.cloneNode(true);" +
                                    "old_numberListTag.parentNode.replaceChild(new_numberListTag, old_numberListTag);" +
                                "}"+

                                //Место в вагоне
                                //"if(!seatOk){"+
                                    "setSeat();"+
                                //"}"+
                            "}"+
                            //Иначе, если тип вагона не соответствует заданному,
                            //и номер вагона не соответствует заданному,
                            // - открываем список типов, выбираем нужный, фиксируем его,
                            // дожидаемся рендеринга данных о номерах, ?
                            //открываем список номеров, выбираем нужный, фиксируем его,
                            // дожидаемся получения и рендеринга данных о местах, делаем паузу,
                            // выбираем заданное место и его также фиксируем
                            "else {"+

                                // Клик по кнопке списка типов вагонов
                                "var typeListTag = document.querySelector('button._2fHmX._1W9qx._3D1rc');" +
                                "eventFire(typeListTag, 'touchstart');" +
                                "eventFire(typeListTag, 'touchend');" +

                                "containerTag.addEventListener('DOMSubtreeModified', contentChanged, false);"+

                                // Выбор нужного типа
                                "var typeTags = document.querySelectorAll('div._3dsoa');" +
                                "var searchTypeText = '" + carTypeString + "';" +
                                "var typeFound = false;"+
                                "for (var i = 0; i < typeTags.length; i++) {" +
                                    "if (typeTags[i].textContent == searchTypeText) {" +
                                        "typeFound = true;"+
                                        "necessarySetNumber = true;"+
                                        "eventFire(typeTags[i].parentNode.parentNode, 'touchstart');" +
                                        "eventFire(typeTags[i].parentNode.parentNode, 'touchend');" +
                                        "break;" +
                                    "}" +
                                "}" +
                                "if(typeTags.length > 0 && !typeFound){"+
                                    "alert('Заданный тип вагона не найден на странице');"+
                                    "setTimeout(function() {"+
                                        "var xhr = new XMLHttpRequest();"+
                                        "xhr.open('GET', 'type_not_found');"+
                                        "xhr.send(null);"+
                                    "}, 3000);"+
                                "}"+
                            "}"+

                            //При любом варианте - блокирование кнопки перехода на страницу рейсов
                            "var backBtnTag = document.querySelector('button._3SgIS');" +
                            "var backBtnHeadings = document.evaluate(\"//span[contains(., 'Другие варианты')]\", document, null, XPathResult.ANY_TYPE, null );" +
                            //"console.log('backBtnHeadings: ' + backBtnHeadings);"+
                            "var old_backBtnTagChild = backBtnHeadings.iterateNext();" +
                            //className = Ji8LT
                            //"console.log('old_backBtnTagChild: ' + old_backBtnTagChild.className);"+
                            "if(old_backBtnTagChild != null && old_backBtnTagChild.parentNode != null){"+
                                "old_backBtnTagChild.parentNode.removeChild(old_backBtnTagChild);"+
                            "}"+
                            //Выдача оповещения об изменении информации о пассажирах
                            "function peopleChanged(){" +
                                "var peopleCurrentTags = document.querySelectorAll('div._2EZjJ');" +
                                "if ((peopleCurrentTags != null) && (peopleCurrentTags.length == 3)) {" +
                                    //"console.log('peopleCurrentTags: ' + peopleCurrentTags[0].textContent);"+
                                    //"console.log('peopleCurrentTags: ' + peopleCurrentTags[1].textContent);"+
                                    //"console.log('peopleCurrentTags: ' + peopleCurrentTags[2].textContent);"+
                                    "var ad = peopleCurrentTags[0].textContent;"+
                                    "var ch = peopleCurrentTags[1].textContent;"+
                                    "var yc = peopleCurrentTags[2].textContent;"+
                                    "var xhr = new XMLHttpRequest();"+
                                    "xhr.open('GET', 'people_changed?adult=' + ad + '&children=' + ch + '&young_children=' + yc);"+
                                    "xhr.send(null);"+
                                "}" +
                            "}" +
                            //"console.log('1');"+
                            "var peopleTags = document.querySelectorAll('div._2EZjJ');" +
                            //"console.log('2');"+
                            "if (peopleTags != null) {" +
                                "for (var i = 0; i < peopleTags.length; i++) {" +
                                    "peopleTags[i].addEventListener('DOMSubtreeModified', peopleChanged, false);"+
                                "}" +
                            "}" +

                            //Если было обновление формы после установки типа вагона -
                            //находим заданное место, и если заданный номер вагона не был номером по умолчанию -
                            //запускаем его установку
                            "function contentChanged(){" +
                                "if(placesResponseDone && (placesResponseCounter >= 2) && (document.querySelector('._17fX-') != null)){"+

                                    "placesResponseDone = false;"+
                                    "placesResponseCounter = 0;"+

                                    "var seatTags = document.querySelectorAll('div._3jQmm > div:nth-child(1)');" +
                                    "var searchSeatText = '" + seatNumberString + "';" +
                                    "var searchSeatTag;" +
                                    "var seatFound = false;"+
                                    "var seatsNoZero = false;"+
                                    "if (seatTags.length > 0) {" +
                                        "seatsNoZero = true;"+
                                    "}"+
                                    "for (var i = 0; i < seatTags.length; i++) {" +
                                        "if (seatTags[i].textContent == searchSeatText) {" +
                                            "seatFound = true;"+
                                            "if(necessarySetNumber) {" +
                                                "setNumber();" +
                                                "necessarySetNumber = false;" +
                                            "}"+
                                            "searchSeatTag = seatTags[i].parentNode;"+

                                        "}" +
                                    "}" +
                                    "function fireCallback () {" +

                                        //Фиксируется кнопка выбора типа вагона
                                        "var typeListTag = document.querySelector('button._2fHmX._1W9qx._3D1rc');" +
                                        "if(typeListTag != null){"+
                                            "var old_typeListTag = typeListTag;" +
                                            "var new_typeListTag = typeListTag.cloneNode(true);" +
                                            "old_typeListTag.parentNode.replaceChild(new_typeListTag, old_typeListTag);" +
                                        "}"+

                                        "setNumber();"+

                                        //Фиксируется кнопка выбора номера вагона
                                        "var numberListTag = " +
                                            "document.querySelector(" +
                                                "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)');" +
                                        //"console.log('old_numberListTag: ' + old_numberListTag);"+
                                        "if(numberListTag != null){"+
                                            "var old_numberListTag = numberListTag;" +
                                            "var new_numberListTag = numberListTag.cloneNode(true);" +
                                            "old_numberListTag.parentNode.replaceChild(new_numberListTag, old_numberListTag);" +
                                        "}"+
                                        //Если заданное место не было найдено,
                                        // или оно сейчас в процессе бронирования,
                                        //или уже забронировано
                                        "console.log('seatsNoZero: ' + seatsNoZero);"+
                                        "console.log('seatFound: ' + seatFound);"+
                                        "console.log('(searchSeatTag.className.indexOf(\"_13esh\") != -1): ' + (searchSeatTag != undefined && searchSeatTag.className.indexOf('_13esh') != -1));"+
                                        "if (seatsNoZero && (!seatFound " +
                                            "|| (searchSeatTag != undefined && searchSeatTag.className.indexOf('_13esh') != -1))) {" +
                                            "alert('Место не найдено, забронировано или в процессе бронирования');"+
                                            "setTimeout(function() {"+
                                                "var xhr = new XMLHttpRequest();"+
                                                "xhr.open('GET', 'seat_not_found');"+
                                                "xhr.send(null);"+
                                            "}, 3000);"+
                                        "}"+
                                        //Выбираем место в вагоне, затем фиксируем этот выбор

                                        "eventFire(searchSeatTag, 'click');" +

                                        //"var timerId = setInterval(function() {"+
                                        //"setTimeout(function() {"+
                                            //"var seatTags = document.querySelectorAll('div._3jQmm > div:nth-child(1)');" +
                                            //"console.log('searchSeatTag.className.indexOf ' + (searchSeatTag.className.indexOf('_1JSli') == -1));"+
                                            //"if (searchSeatTag.className.indexOf('_1JSli') == -1) {"+
                                                //"clearInterval(timerId);"+

                                                "for (var i = 0; i < seatTags.length; i++) {" +
                                                    "if(seatTags[i] != null && seatTags[i].parentNode != null){"+
                                                        "var old_seatTagParent = seatTags[i].parentNode;" +
                                                        "var new_seatTagParent = seatTags[i].parentNode.cloneNode(true);" +
                                                        //"console.log('marker ' + old_seatTagParent.parentNode);"+
                                                        "old_seatTagParent.parentNode.replaceChild(new_seatTagParent, old_seatTagParent);" +
                                                    "}"+
                                                "}" +

                                            //"}"+
                                        //"}, 1000);"+
                                    "}"+
                                    "setTimeout(fireCallback, 500);"+
                                "}"+
                            "}" +
                            "function setNumber(){"+

                                // Определяем, является ли заданный номер вагона номером по умолчанию на странице
                                "var defaultCarNumberTag =" +
                                    "document.querySelector(" +
                                        "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1) > span:nth-child(1) > span:nth-child(2)');" +
                                //Если значение по умолчанию есть на странице, но не равно заданному - устанавливаем заданное
                                "if((defaultCarNumberTag != null) "+
                                    "&& (defaultCarNumberTag.textContent != '" + carNumberString + "')){"+
                                    // Клик по кнопке списка номеров вагонов
                                    "var numberListTag = " +
                                        "document.querySelector(" +
                                            "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)');" +
                                    "eventFire(numberListTag, 'touchstart');" +
                                    "eventFire(numberListTag, 'touchend');" +

                                    // Выбор нужного номера вагона
                                    "var numberTags = document.querySelectorAll('div.dAJ0Q:nth-child(2) li > div > div > span > span:nth-child(1)');" +
                                    "var searchNumberText = '" + carNumberString + "';" +
                                    "var numberFound = false;"+
                                    "for (var i = 0; i < numberTags.length; i++) {" +
                                        //"console.log('Number: ' + searchNumberText + ' = ' + numberTags[i].textContent);"+
                                        "if (numberTags[i].textContent === searchNumberText) {" +
                                            "numberFound = true;"+
                                            "eventFire(numberTags[i].parentNode.parentNode.parentNode, 'touchstart');" +
                                            "eventFire(numberTags[i].parentNode.parentNode.parentNode, 'touchend');" +
                                            "break;" +
                                        "}" +
                                    "}" +
                                    "if(numberTags.length > 0 && !numberFound){"+
                                        "alert('Заданный номер вагона не найден на странице');"+
                                        "setTimeout(function() {"+
                                            "var xhr = new XMLHttpRequest();"+
                                            "xhr.open('GET', 'number_not_found');"+
                                            "xhr.send(null);"+
                                        "}, 3000);"+
                                    "}"+
                                "}"+
                                //Если заданное значение номера вагона установлено по умолчанию -
                                //фиксируем номер вагона и выбираем место
                                "else if((defaultCarNumberTag != null) "+
                                    "&& (defaultCarNumberTag.textContent === '" + carNumberString + "')){"+
                                    //Фиксируется кнопка выбора номера вагона
                                    "var numberListTag = " +
                                        "document.querySelector(" +
                                            "'div.dAJ0Q:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)');" +
                                    "if(numberListTag != null){"+
                                        "var old_numberListTag = numberListTag;" +
                                        "var new_numberListTag = numberListTag.cloneNode(true);" +
                                        "old_numberListTag.parentNode.replaceChild(new_numberListTag, old_numberListTag);" +
                                    "}"+
                                    //Место в вагоне
                                    //"setSeat();"+
                                "}"+
                            "}"+
                        "}"+
                    //"});" +
                    "})();");

                /*if (mClearHistory)
                {
                    mClearHistory = false;
                    mWebView.clearHistory();
                    mWebView.clearCache(true);
                }*/
            }

            /**
             *
             * */
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (url.contains("number_not_found")) {

                    TicketBooker.setBooked(false);
                    TicketBooker.mCarNumberNotFound = true;
                    finish();
                }

                if (url.contains("type_not_found")) {

                    TicketBooker.setBooked(false);
                    TicketBooker.mCarTypeNotFound = true;
                    finish();
                }

                if (url.contains("seat_not_found")) {

                    TicketBooker.setBooked(false);
                    TicketBooker.mSeatNotFound = true;
                    finish();
                }

                //Log.i("booked", url + " " + url.contains("bookManager"));
                if (url.contains("people_changed")) {

                    try {
                        Map<String, String> params = UrlStringHelper.splitQuery(url);
                        //Log.i("people_changed 1", params.get("adult"));
                        //Log.i("people_changed 2", params.get("children"));
                        //Log.i("people_changed 3", params.get("young_children"));
                        TicketBooker.SeatDetail.setState(
                            Integer.parseInt(params.get("adult"))
                            , Integer.parseInt(params.get("children"))
                            , Integer.parseInt(params.get("young_children")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IncorrectPassengersNumberException e) {
                        //e.printStackTrace();
                        showErrorMsg(getString(R.string.incorrect_pass_num_msg));
                    }
                }

                //if (url.contains("bookManager")) {createOrder
                //Если отправлен запрос на оплату - устанавливаем результат "забронировано"
                if (url.contains("createOrder")) {

                    TicketBooker.setBooked(true);
                    //Если пользователь нажал кнопку возврата к настройке билета -
                    //возвращаем его из модуля бронирования с результатом "НЕ забронировано"
                } else if (url.contains("cancelBooking")) {

                    TicketBooker.setBooked(false);
                    finish();
                }
                mCurrentURLString = url;
                //Log.i("booked", url + " " + url.contains("bookManager") + " " + TicketBooker.isBooked());
                return super.shouldInterceptRequest(view, url);
            }
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
        //mWebView.loadUrl(BASE_URL + "ru/poezda/train/?fromName="+ URLEncoder.encode("Москва", "UTF-8")+"&toName="+URLEncoder.encode("Санкт-Петербург", "UTF-8")+"&train=020У&from=2006004&to=2004001&classes[0]=4&classes[1]=6&minCost=2181.6&metaTo=22871&metaFrom=22823&date=01112017");
        mWebView.loadUrl(trainLink);
    }

    private void showErrorMsg(String _message){
        Toast.makeText(
            this
            , _message
            , Toast.LENGTH_LONG
        ).show();
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
