package org.tyaa.ticketbookeremulator.exception;

/**
 * Created by yurii on 11.10.17.
 */

public class FailJSONFetchException extends Exception {

    @Override
    public String getMessage() {

        return "JSON fetching failed";
    }
}
