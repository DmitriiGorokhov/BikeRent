package com.gorokhov.util.exceptions;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException() {
        super("Клиент не был найден");
    }
}