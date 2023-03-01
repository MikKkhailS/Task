package ru.relex.exception;

/**
 * Кастомная ошибка, которая выбрасывается при регистрации нового пользователя, если username уже занято другим
 * пользователем
 */

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}