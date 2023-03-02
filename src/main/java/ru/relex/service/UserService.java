package ru.relex.service;

import ru.relex.model.User;

public interface UserService {
    String register(User user);
    String[] getOverallBalance(User user);
    String topUpBalance(User user);
    String[] withdrawCurrency(User user);
    String[] showExchangeRate(User user);
    String[] exchangeCurrency(User user);
}