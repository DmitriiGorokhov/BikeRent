package com.gorokhov.util.exceptions;

public class CommentNotUpdatedException extends RuntimeException {
    public CommentNotUpdatedException(String message) {
        super(message);
    }
}