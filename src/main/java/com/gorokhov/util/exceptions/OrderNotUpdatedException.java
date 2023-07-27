package com.gorokhov.util.exceptions;

public class OrderNotUpdatedException extends RuntimeException {
    public OrderNotUpdatedException(String message) {
        super(message);
    }
}