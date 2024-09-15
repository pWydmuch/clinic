package org.example.pretask.exception;

import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class PatientExceptionHandler {

    @ExceptionHandler({AppointmentAlreadyCancelledException.class,
            NoSuchElementException.class,
            UserAlreadyExistException.class,
            AuthenticationException.class})
    public ResponseEntity<?> handleBadRequests(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValid(MethodArgumentNotValidException e) {
        ConstraintViolation<?> err = e.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
        return new ResponseEntity<>(new ErrorResponse(err.getPropertyPath() + " " + err.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
