package ru.relex.exception;

/**
 * Кастомная ошибка, которая выбрасывается в том случае, если при снятии валюты пришли некорректные данные
 */

public class WithdrawException extends RuntimeException {
    public WithdrawException(String message) {
        super(message);
    }
}