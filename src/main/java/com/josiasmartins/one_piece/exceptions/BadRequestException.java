package com.josiasmartins.one_piece.exceptions;

import lombok.Getter;


@Getter
public class BadRequestException extends RuntimeException {
    private String message;
    private int statusCode;

    public BadRequestException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

}
