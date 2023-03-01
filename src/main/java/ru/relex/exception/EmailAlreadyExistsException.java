package ru.relex.exception;

/**
 * Кастомная ошибка, которая выбрасывается при регистрации нового пользователя, если email уже занят другим
 * пользователем
 */

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}