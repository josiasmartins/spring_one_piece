package com.josiasmartins.one_piece.exceptions;

import com.josiasmartins.one_piece.domain.models.ErrorDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDefault badRequest(BadRequestException ex) {

        return new ErrorDefault(ex.getMessage(), ex.getStatusCode(), "200");
    }

}
