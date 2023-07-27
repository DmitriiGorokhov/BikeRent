package com.gorokhov.util.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Комментарий не был найден");
    }
}