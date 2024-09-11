package org.example.pretask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class PatientExceptionHandler {
    @ExceptionHandler({AppointmentAlreadyCancelledException.class, NoSuchElementException.class})
    public ResponseEntity<?> handleBadRequests(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
