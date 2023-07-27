package com.gorokhov.models.enums;

public enum Size {

    XS("Размер рамы - 13\"-14\""),
    S("Размер рамы - 15\"-16\""),
    M("Размер рамы - 17\"-18\""),
    L("Размер рамы - 19\"-20\""),
    XL("Размер рамы - 21\"-22\""),
    XXL("Размер рамы - 23\"-24\"");

    final String description;

    Size(String description) {
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