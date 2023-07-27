package com.gorokhov.util.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Заказ не был найден");
    }
}