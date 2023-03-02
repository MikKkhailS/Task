package ru.relex.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relex.exception.*;
import ru.relex.model.ExchangeRate;
import ru.relex.model.User;
import ru.relex.repository.ExchangeRateRepository;
import ru.relex.repository.OperationRepository;
import ru.relex.repository.UserRepository;
import ru.relex.service.AdminService;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final OperationRepository operationRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
                            OperationRepository operationRepository,
                            ExchangeRateRepository exchangeRateRepository) {
        this.userRepository = userRepository;
        this.operationRepository = operationRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Transactional
    @Override
    public String[] changeExchangeRate(User admin) {
        var foundAdmin = userRepository.findByWalletNumber(admin.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        admin.getWalletNumber() + "» not found"));

        if (!isAvailable(foundAdmin))
            throw new AuthorisationException("No access");

        var result = new String[3];
        ExchangeRate exchangeRate;

        switch (admin.getCurrency()) {
            case "RUB" -> {
                if (admin.getBtcBalance() != null & admin.getTonBalance() != null) {
                    if (!admin.getBtcBalance().matches(
                            "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                        throw new AmountException("Incorrect value. You can set a minimum: 0.00000001 BTC " +
                                "and a maximum: 999999999999.99999999 BTC");
                    }

                    if (!admin.getTonBalance().matches(
                            "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                        throw new AmountException("Incorrect value. You can set a minimum: 0.00000001 TON " +
                                "and a maximum: 999999999999.99999999 TON");
                    }

                    exchangeRate = exchangeRateRepository.findByCurrency(admin.getCurrency()).get();
                    exchangeRate.setToBtc(admin.getBtcBalance());
                    exchangeRate.setToTon(admin.getTonBalance());
                    exchangeRate.setUpdatedAt(new Date());

                    result[1] = exchangeRate.getToBtc();
                    result[2] = exchangeRate.getToTon();
                } else {
                    throw new ExchangeRateException("Error changing the exchange rate");
                }
            }
            case "TON" -> {
                if (admin.getBtcBalance() != null & admin.getRubBalance() != null) {
                    if (!admin.getBtcBalance().matches(
                            "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                        throw new AmountException("Incorrect value. You can set a minimum: 0.00000001 BTC " +
                                "and a maximum: 999999999999.99999999 BTC");
                    }

                    if (!admin.getRubBalance().matches(
                            "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                        throw new AmountException("Incorrect value. You can set a minimum: 0.00000001 RUB " +
                                "and a maximum: 999999999999.99999999 RUB");
                    }

                    exchangeRate = exchangeRateRepository.findByCurrency(admin.getCurrency()).get();
                    exchangeRate.setToBtc(admin.getBtcBalance());
                    exchangeRate.setToRub(admin.getRubBalance());
                    exchangeRate.setUpdatedAt(new Date());

                    result[1] = exchangeRate.getToBtc();
                    result[2] = exchangeRate.getToRub();
                } else {
                    throw new ExchangeRateException("Error changing the exchange rate");
                }
            }
            case "BTC" -> {
                if (admin.getTonBalance() != null & admin.getRubBalance() != null) {
                    if (!admin.getTonBalance().matches(
                            "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                        throw new AmountException("Incorrect value. You can set a minimum: 0.00000001 TON " +
                                "and a maximum: 999999999999.99999999 TON");
                    }

                    if (!admin.getRubBalance().matches(
                            "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                        throw new AmountException("Incorrect value. You can set a minimum: 0.00000001 RUB " +
                                "and a maximum: 999999999999.99999999 RUB");
                    }

                    exchangeRate = exchangeRateRepository.findByCurrency(admin.getCurrency()).get();
                    exchangeRate.setToTon(admin.getTonBalance());
                    exchangeRate.setToRub(admin.getRubBalance());
                    exchangeRate.setUpdatedAt(new Date());

                    result[1] = exchangeRate.getToTon();
                    result[2] = exchangeRate.getToRub();
                } else {
                    throw new ExchangeRateException("Error changing the exchange rate");
                }
            }
        }

        result[0] = admin.getCurrency();
        return result;
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

    @Override
    public String showOperationsCount(User admin) {
        var foundAdmin = userRepository.findByWalletNumber(admin.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        admin.getWalletNumber() + "» not found"));

        if (!isAvailable(foundAdmin))
            throw new AuthorisationException("No access");

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date dateFrom, dateTo;
        try {
            dateFrom = formatter.parse(admin.getDateFrom());
            dateTo = formatter.parse(admin.getDateTo());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (!dateFrom.before(dateTo))
            throw new DateRangeException("The start date must be strictly less than the end date");

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        int count = operationRepository.operationsNumberByDate(formatter.format(dateFrom),
                formatter.format(dateTo));

        return Integer.toString(count);
    }

    private boolean isAvailable(User foundAdmin) {
        return (foundAdmin.getRole().equals("ROLE_ADMIN"));
    }
}