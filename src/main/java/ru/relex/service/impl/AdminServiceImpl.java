package ru.relex.service.impl;

import ru.relex.model.User;
import ru.relex.repository.AdminRepository;
import ru.relex.repository.ExchangeRateRepository;
import ru.relex.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public AdminServiceImpl(AdminRepository adminRepository,
                            ExchangeRateRepository exchangeRateRepository) {
        this.adminRepository = adminRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public String getTotalAmount(User admin) {
        return null;
    }
}