package org.tyaa.ticketbookeremulator.exception;

/**
 * Created by yurii on 11.10.17.
 */

public class IncorrectPassengersNumberException extends Exception {

    @Override
    public String getMessage() {

        return "Incorrect number of passengers  (total min = 1, total max = 4)";
    }
}
