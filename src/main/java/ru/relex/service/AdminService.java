package ru.relex.service;

import ru.relex.model.User;

public interface AdminService {
    String[] changeExchangeRate(User admin);
    String[] getTotalAmount(User admin);
    String showOperationsCount(User admin);
}