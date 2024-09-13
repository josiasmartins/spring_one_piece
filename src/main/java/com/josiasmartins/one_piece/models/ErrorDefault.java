package com.josiasmartins.one_piece.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDefault {

    private String message;
    private int statusCode;
    private String code;

}
