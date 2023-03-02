package ru.relex.exception;

/**
 * Кастомная ошибка, которая выбрасывается в том случае, если значение для какой-то валюты введено некорректно
 */

public class AmountException extends RuntimeException {
    public AmountException(String message) {
        super(message);
    }
}