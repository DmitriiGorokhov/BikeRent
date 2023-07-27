package com.gorokhov.util.exceptions;

public class StorageNotFoundException extends RuntimeException {
    public StorageNotFoundException() {
        super("Хранилище не было найдено");
    }
}