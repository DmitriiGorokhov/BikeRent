package com.gorokhov.models.enums;

public enum Color {

    WHITE("Белый"),
    GREY("Серый"),
    BLACK("Черный"),
    RED("Красный"),
    ORANGE("Оранжевый"),
    YELLOW("Желтый"),
    GREEN("Зеленый"),
    BLUE("Синий"),
    VIOLET("Фиолетовый");

    final String description;

    Color(String description) {
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