package ru.relex.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relex.exception.AuthorisationException;
import ru.relex.exception.CurrencyNotFoundException;
import ru.relex.exception.WalletNotFoundException;
import ru.relex.model.User;
import ru.relex.repository.ExchangeRateRepository;
import ru.relex.repository.UserRepository;
import ru.relex.service.AdminService;

import java.text.DecimalFormat;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            ExchangeRateRepository exchangeRateRepository) {
        this.userRepository = userRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public String[] getTotalAmount(User admin) {
        var foundAdmin = userRepository.findByWalletNumber(admin.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        admin.getWalletNumber() + "» not found"));

        if (!isAvailable(foundAdmin))
            throw new AuthorisationException("No access");

        var result = new String[2];
        var currency = exchangeRateRepository.findByCurrency(admin.getCurrency());

        if (currency.isPresent()) {
            result[0] = admin.getCurrency();

            List<String> allAmountCurrency;
            double sum = 0;
            DecimalFormat df;

            switch (admin.getCurrency()) {
                case "RUB" -> {
                    allAmountCurrency = userRepository.findAllRub();
                    for (String value : allAmountCurrency) {
                        sum += Double.parseDouble(value);
                    }

                    df = new DecimalFormat("#.##");
                    result[1] = df.format(sum).replaceAll(",", ".");
                }
                case "BTC" -> {
                    allAmountCurrency = userRepository.findAllBtc();
                    for (String value : allAmountCurrency) {
                        sum += Double.parseDouble(value);
                    }

                    df = new DecimalFormat("#.########");
                    result[1] = df.format(sum).replaceAll(",", ".");
                }
                case "TON" -> {
                    allAmountCurrency = userRepository.findAllTon();
                    for (String value : allAmountCurrency) {
                        sum += Double.parseDouble(value);
                    }

                    df = new DecimalFormat("#.########");
                    result[1] = df.format(sum).replaceAll(",", ".");
                }
            }
        } else {
            throw new CurrencyNotFoundException("Currency with currency name «" +
                    admin.getCurrency() + "» not found");
        }

        return result;
    }

    private boolean isAvailable(User foundUser) {
        return (foundUser.getRole().equals("ROLE_ADMIN"));
    }
}