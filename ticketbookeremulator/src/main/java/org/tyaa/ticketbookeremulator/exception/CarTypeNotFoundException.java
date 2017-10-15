package org.tyaa.ticketbookeremulator.exception;

/**
 * Created by yurii on 11.10.17.
 */

public class CarTypeNotFoundException extends Exception {

    @Override
    public String getMessage() {

        return "Car type not found";
    }
}
