package org.example.pretask.exception;

public class AppointmentAlreadyCancelledException extends RuntimeException {
    public AppointmentAlreadyCancelledException(String cause) {
        super(cause);
    }
}
