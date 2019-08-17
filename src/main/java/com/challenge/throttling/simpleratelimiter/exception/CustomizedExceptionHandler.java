package com.challenge.throttling.simpleratelimiter.exception;

import com.challenge.throttling.simpleratelimiter.models.ExceptionResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomizedExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity handleAllExceptions(Exception ex, WebRequest request){
        ExceptionResponseModel exception =
                new ExceptionResponseModel(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public final ResponseEntity handleHttpErrorExceptions(HttpClientErrorException ex, WebRequest request){
        String errorMessage = enhanceErrorMessage(ex.getMessage());
        ExceptionResponseModel exception =
                new ExceptionResponseModel(new Date(), errorMessage, request.getDescription(false));

        System.out.println(errorMessage);

        return new ResponseEntity(exception,ex.getStatusCode());
    }

    private String enhanceErrorMessage(String initinalErrorMessage){
        final int ERROR_CODE_LENGTH = 3;
        return initinalErrorMessage.substring(ERROR_CODE_LENGTH + 1);
    }
}
