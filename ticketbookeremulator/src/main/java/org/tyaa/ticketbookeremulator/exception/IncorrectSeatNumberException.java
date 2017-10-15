package org.tyaa.ticketbookeremulator.exception;

/**
 * Created by yurii on 11.10.17.
 */

public class IncorrectSeatNumberException extends Exception {

    @Override
    public String getMessage() {

        return "Incorrect number of seat  (not a number or less than 1)";
    }
}
