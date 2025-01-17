package com.dan.sd.monitoring.controllers.handlers.exceptions.model;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class AuthIncorrectCredentialsException extends CustomException {
    private static final String MESSAGE = "Username or password incorrect!";
    private static final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public AuthIncorrectCredentialsException(String resource) {
        super(MESSAGE, httpStatus, resource, new ArrayList<>());
    }
}
