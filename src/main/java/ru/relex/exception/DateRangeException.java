package ru.relex.exception;

/**
 * Кастомная ошибка, которая выбрасывается в том случае, если начальная дата больше или равна даты окончания
 */

public class DateRangeException extends RuntimeException {
    public DateRangeException(String message) {
        super(message);
    }
}