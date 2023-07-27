package com.gorokhov.util.exceptions;

public class BikeNotFoundException extends RuntimeException {
    public BikeNotFoundException() {
        super("Велосипед не был найден");
    }
}