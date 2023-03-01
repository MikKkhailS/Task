package ru.relex.exception;

/**
 * Кастомная ошибка. Выбрасывается в том случае, если сумма в какой-то валюте, которую хочет снять пользователь
 * с кошелька, больше суммы, лежащей на балансе этого пользователя
 */

public class WithdrawAmountException extends RuntimeException {
    public WithdrawAmountException(String message) {
        super(message);
    }
}