package ru.relex.exception;

/**
 * Кастомная ошибка, которая выбрасывается в том случае, если запрашиваемый номер кошелька не найден
 */

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}