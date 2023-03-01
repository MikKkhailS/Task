package ru.relex.model.enums;

/**
 * Виды операций, которые может совершить пользователь
 */

public enum OperationType {
    TOP_UP_BALANCE("Balance replenishment"),
    WITHDRAW_RUB_TO_CREDIT_CARD("Withdrawal of rubles to a credit card"),
    WITHDRAW_TON_TO_WALLET("Withdrawal toncoins to a wallet address"),
    WITHDRAW_BTC_TO_WALLET("Withdrawal bitcoins to a wallet address"),
    EXCHANGE_RUB_TO_TON("Exchange rubles for toncoins"),
    EXCHANGE_RUB_TO_BTC("Exchange rubles for bitcoins"),
    EXCHANGE_BTC_TO_RUB("Exchange bitcoins for rubles"),
    EXCHANGE_BTC_TO_TON("Exchange bitcoins for toncoins"),
    EXCHANGE_TON_TO_RUB("Exchange toncoins for rubles"),
    EXCHANGE_TON_TO_BTC("Exchange toncoins for bitcoins");

    private final String operationName;

    OperationType(String operationName) {
        this.operationName = operationName;
    }

    @Override
    public String toString() {
        return operationName;
    }
}