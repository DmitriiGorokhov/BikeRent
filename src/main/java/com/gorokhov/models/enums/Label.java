package com.gorokhov.models.enums;

public enum Label {

    GIANT_BICYCLE("Giant Bicycle"),
    TREK_BICYCLE("Trek Bicycle"),
    SCOTT("Scott"),
    SALSA("Salsa"),
    CANNONDALE("Cannondale"),
    FUJI("Fuji"),
    MONGOOSE("Mongoose"),
    STELS("Stels"),
    FORWARD("Forward");

    final String description;

    Label(String description) {
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