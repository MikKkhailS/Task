package ru.relex.exception;

public class AuthorisationException extends RuntimeException {
    public AuthorisationException(String message) {
        super(message);
    }
}