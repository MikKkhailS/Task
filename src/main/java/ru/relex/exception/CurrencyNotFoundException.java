package ru.relex.exception;

/**
 * Кастомная ошибка, которая выбрасывается в том случае, если запрашиваемая валюта не найдена
 */

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}