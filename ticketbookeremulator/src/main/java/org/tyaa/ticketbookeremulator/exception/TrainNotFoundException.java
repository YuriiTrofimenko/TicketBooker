package org.tyaa.ticketbookeremulator.exception;

/**
 * Created by yurii on 11.10.17.
 */

public class TrainNotFoundException extends Exception {

    @Override
    public String getMessage() {

        return "Train not found";
    }
}
