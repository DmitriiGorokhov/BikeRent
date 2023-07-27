package com.gorokhov.models.enums;

public enum City {

    MOSCOW("Москва"),
    SAINT_PETERSBURG("Санкт-Петербург"),
    EKATERINBURG("Екатеринбург"),
    NIZHNY_NOVGOROD("Нижний Новгород"),
    KRASNODAR("Краснодар"),
    NOVOSIBIRSK("Новосибирск");

    final String description;

    City(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}