package ru.relex.exception;

/**
 * Кастомная ошибка. Выбрасывается в том случае, если для какой-то валюты были получены неверные поля других
 * валют, для которых необходимо установить обменный курс
 */

public class ExchangeRateException extends RuntimeException {
    public ExchangeRateException(String message) {
        super(message);
    }
}