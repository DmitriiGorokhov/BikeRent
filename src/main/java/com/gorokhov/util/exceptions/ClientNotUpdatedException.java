package com.gorokhov.util.exceptions;

public class ClientNotUpdatedException extends RuntimeException {
    public ClientNotUpdatedException(String message) {
        super(message);
    }
}