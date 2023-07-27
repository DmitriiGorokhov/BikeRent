package com.gorokhov.util.exceptions;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException() {
        super("Адрес не был найден");
    }
}