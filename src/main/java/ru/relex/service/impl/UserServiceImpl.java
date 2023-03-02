package ru.relex.service.impl;

import ru.relex.exception.*;
import ru.relex.model.ExchangeRate;
import ru.relex.model.Operation;
import ru.relex.model.User;
import ru.relex.model.enums.OperationType;
import ru.relex.repository.ExchangeRateRepository;
import ru.relex.repository.OperationRepository;
import ru.relex.repository.UserRepository;
import ru.relex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OperationRepository operationRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           OperationRepository operationRepository,
                           ExchangeRateRepository exchangeRateRepository) {
        this.userRepository = userRepository;
        this.operationRepository = operationRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Transactional
    @Override
    public String register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UsernameAlreadyExistsException("User with username «" +
                    user.getUsername() + "» already exists");

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EmailAlreadyExistsException("User with username «" +
                    user.getUsername() + "» already exists");

        String walletNumber;
        do {
            walletNumber = UUID.randomUUID().toString();
        } while (userRepository.findByWalletNumber(walletNumber).isPresent());

        user.setWalletNumber(walletNumber);
        user.setRole("ROLE_USER");
        user.setRubBalance("0");
        user.setBtcBalance("0");
        user.setTonBalance("0");
        userRepository.save(user);

        return walletNumber;
    }

    @Override
    public String[] getOverallBalance(User user) {
        var foundUser = userRepository.findByWalletNumber(user.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        user.getWalletNumber() + "» not found"));

        if (!isAvailable(foundUser))
            throw new AuthorisationException("No access");

        return new String[]{
                    foundUser.getBtcBalance(),
                    foundUser.getTonBalance(),
                    foundUser.getRubBalance()
        };
    }

    @Transactional
    @Override
    public String topUpBalance(User user) {
        var foundUser = userRepository.findByWalletNumber(user.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        user.getWalletNumber() + "» not found"));

        if (!isAvailable(foundUser))
            throw new AuthorisationException("No access");

        var df = new DecimalFormat("#.##");
        double balance;
        var operation = new Operation();

        balance = Double.parseDouble(foundUser.getRubBalance());
        balance += Double.parseDouble(user.getRubBalance());
        foundUser.setRubBalance(df.format(balance).replaceAll(",", "."));

        operation.setCreatedAt(new Date());
        operation.setOperationType(OperationType.TOP_UP_BALANCE.toString());
        operation.setAmount(user.getRubBalance());

        foundUser.addOperation(operation);
        operationRepository.save(operation);

        return foundUser.getRubBalance();
    }

    @Transactional
    @Override
    public String[] withdrawCurrency(User user) {
        var foundUser = userRepository.findByWalletNumber(user.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        user.getWalletNumber() + "» not found"));

        if (!isAvailable(foundUser))
            throw new AuthorisationException("No access");

        if (user.getWithdrawToCard() == null & user.getWithdrawToWallet() == null) {
            throw new WithdrawException("Field for withdrawing the amount " +
                    "in some currency must be filled");
        }

        var operation = new Operation();

        switch (user.getCurrency()) {
            case "RUB" -> {
                if (user.getAmount() != null & user.getWithdrawToCard() != null) {
                    if (!user.getWithdrawToCard().matches("\\d{4} \\d{4} \\d{4} \\d{4}")) {
                        throw new WithdrawAmountException("Invalid credit card number. " +
                                "Example of a valid credit card number: «1234 5678 9012 3456». " +
                                "Don't forget the spaces");
                    }

                    if (user.getAmount().matches("(([1-9]\\d{1,5})\\.((0[1-9])|([1-9]\\d?)))|" +
                                    "([1-9]\\d{1,5}(\\.0{1,2})?)")) {
                        operation.setOperationType(OperationType.WITHDRAW_RUB_TO_CREDIT_CARD.toString());
                        user.setRubBalance(user.getAmount());
                    } else {
                        throw new WithdrawAmountException("Incorrect value. For one operation " +
                                "you can withdraw a minimum: 10 RUB and a maximum: 999999.99 RUB");
                    }
                } else {
                    throw new WithdrawException("Error. You can withdraw rubles only " +
                            "to the credit card using a credit card number");
                }
            }
            case "TON" -> {
                if (user.getAmount() != null & user.getWithdrawToWallet() != null) {
                    if (user.getAmount().matches("((0|([1-9][0-9]{0,5}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,5}(\\.0{1,8})?)")) {
                        operation.setOperationType(OperationType.WITHDRAW_TON_TO_WALLET.toString());
                        user.setTonBalance(user.getAmount());
                    } else {
                        throw new WithdrawAmountException("Incorrect value. For one operation you can " +
                                "withdraw a minimum: 0.00000001 TON and a maximum: 999999.99999999 TON");
                    }
                } else {
                    throw new WithdrawException("Error. You can withdraw cryptocurrency only " +
                            "to the wallet using the wallet address");
                }
            }
            case "BTC" -> {
                if (user.getAmount() != null & user.getWithdrawToWallet() != null) {
                    if (user.getAmount().matches("((0|([1-9][0-9]{0,5}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                    "([1-9]\\d{0,5}(\\.0{1,8})?)")) {
                        operation.setOperationType(OperationType.WITHDRAW_BTC_TO_WALLET.toString());
                        user.setBtcBalance(user.getAmount());
                    } else {
                        throw new WithdrawAmountException("Incorrect value. For one operation you can " +
                                "withdraw a minimum: 0.00000001 BTC and a maximum: 999999.99999999 BTC");
                    }
                } else {
                    throw new WithdrawException("Error. You can withdraw cryptocurrency only " +
                            "to the wallet using the wallet address");
                }
            }
        }

        var result = new String[2];
        result[0] = user.getCurrency();

        DecimalFormat df;
        double balance;

        switch (user.getCurrency()) {
            case "RUB" -> {
                operation.setWithdrawalTo(user.getWithdrawToCard());

                balance = Double.parseDouble(foundUser.getRubBalance());
                balance -= Double.parseDouble(user.getRubBalance());

                if (balance < 0)
                    throw new WithdrawAmountException("Error. There are not enough funds on the balance");

                df = new DecimalFormat("#.##");
                foundUser.setRubBalance(df.format(balance).replaceAll(",", "."));
                result[1] = foundUser.getRubBalance();
            }
            case "TON" -> {
                operation.setWithdrawalTo(user.getWithdrawToWallet());

                balance = Double.parseDouble(foundUser.getTonBalance());
                balance -= Double.parseDouble(user.getTonBalance());

                if (balance < 0)
                    throw new WithdrawAmountException("Error. There are not enough funds on the balance");

                df = new DecimalFormat("#.########");
                foundUser.setTonBalance(df.format(balance).replaceAll(",", "."));
                result[1] = foundUser.getTonBalance();
            }
            case "BTC" -> {
                operation.setWithdrawalTo(user.getWithdrawToWallet());

                balance = Double.parseDouble(foundUser.getBtcBalance());
                balance -= Double.parseDouble(user.getBtcBalance());

                if (balance < 0)
                    throw new WithdrawAmountException("Error. There are not enough funds on the balance");

                df = new DecimalFormat("#.########");
                foundUser.setBtcBalance(df.format(balance).replaceAll(",", "."));
                result[1] = foundUser.getBtcBalance();
            }
        }

        operation.setCreatedAt(new Date());
        operation.setAmount(user.getAmount());

        foundUser.addOperation(operation);
        operationRepository.save(operation);

        return result;
    }

    @Override
    public String[] showExchangeRate(User user) {
        var foundUser = userRepository.findByWalletNumber(user.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        user.getWalletNumber() + "» not found"));

        var result = new String[3];
        result[0] = user.getCurrency();

        var currency = exchangeRateRepository.findByCurrency(user.getCurrency());
        if (currency.isPresent()) {
            switch (user.getCurrency()) {
                case "RUB" -> {
                    result[1] = currency.get().getToBtc();
                    result[2] = currency.get().getToTon();
                }
                case "BTC" -> {
                    result[1] = currency.get().getToTon();
                    result[2] = currency.get().getToRub();
                }
                case "TON" -> {
                    result[1] = currency.get().getToBtc();
                    result[2] = currency.get().getToRub();
                }
            }
        } else {
            throw new CurrencyNotFoundException("Currency with currency name «" +
                    user.getCurrency() + "» not found");
        }

        return result;
    }

    @Transactional
    @Override
    public String[] exchangeCurrency(User user) {
        var foundUser = userRepository.findByWalletNumber(user.getWalletNumber())
                .orElseThrow(() -> new WalletNotFoundException("Wallet with secret key «" +
                        user.getWalletNumber() + "» not found"));

        if (!isAvailable(foundUser))
            throw new AuthorisationException("No access");

        var result = new String[4];
        result[0] = user.getCurrency();
        result[2] = user.getAmount();

        DecimalFormat df;
        double balance;
        var amount = Double.parseDouble(user.getAmount());
        Operation operation = new Operation();

        switch (user.getCurrency()) {
            case "RUB" -> {
                if (!user.getCurrencyTo().equals("TON") & !user.getCurrencyTo().equals("BTC"))
                    throw new ExchangeRateException("Currency exchange error");

                if (!user.getAmount().matches(
                        "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                    throw new AmountException("Incorrect value. You can exchange a minimum: 0.00000001 RUB " +
                            "and a maximum: 999999999999.99999999 RUB");
                }

                balance = Double.parseDouble(foundUser.getRubBalance());

                if (balance < amount) {
                    throw new AmountException("Error. There are not enough funds on the balance");
                }

                ExchangeRate exchangeRate = exchangeRateRepository.findByCurrency("RUB").get();

                balance -= amount;
                switch (user.getCurrencyTo()) {
                    case "TON" -> {
                        amount *= Double.parseDouble(exchangeRate.getToTon());
                        df = new DecimalFormat("#.########");
                        foundUser.setRubBalance(df.format(balance).replaceAll(",", "."));
                        foundUser.setTonBalance(df.format(amount).replaceAll(",", "."));
                        operation.setOperationType(OperationType.EXCHANGE_RUB_TO_TON.toString());
                        result[3] = foundUser.getTonBalance();
                    }
                    case "BTC" -> {
                        amount *= Double.parseDouble(exchangeRate.getToBtc());
                        df = new DecimalFormat("#.########");
                        foundUser.setRubBalance(df.format(balance).replaceAll(",", "."));
                        foundUser.setBtcBalance(df.format(amount).replaceAll(",", "."));
                        operation.setOperationType(OperationType.EXCHANGE_RUB_TO_BTC.toString());
                        result[3] = foundUser.getBtcBalance();
                    }
                }
            }
            case "TON" -> {
                if (!user.getCurrencyTo().equals("BTC") & !user.getCurrencyTo().equals("RUB"))
                    throw new ExchangeRateException("Currency exchange error");

                if (!user.getAmount().matches(
                        "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                    throw new AmountException("Incorrect value. You can exchange a minimum: 0.00000001 TON " +
                            "and a maximum: 999999999999.99999999 TON");
                }

                balance = Double.parseDouble(foundUser.getTonBalance());

                if (balance < amount) {
                    throw new AmountException("Error. There are not enough funds on the balance");
                }

                ExchangeRate exchangeRate = exchangeRateRepository.findByCurrency("TON").get();

                balance -= amount;
                switch (user.getCurrencyTo()) {
                    case "BTC" -> {
                        amount *= Double.parseDouble(exchangeRate.getToBtc());
                        df = new DecimalFormat("#.########");
                        foundUser.setTonBalance(df.format(balance).replaceAll(",", "."));
                        foundUser.setBtcBalance(df.format(amount).replaceAll(",", "."));
                        operation.setOperationType(OperationType.EXCHANGE_TON_TO_BTC.toString());
                        result[3] = foundUser.getBtcBalance();
                    }
                    case "RUB" -> {
                        amount *= Double.parseDouble(exchangeRate.getToRub());
                        df = new DecimalFormat("#.########");
                        foundUser.setTonBalance(df.format(balance).replaceAll(",", "."));
                        foundUser.setRubBalance(df.format(amount).replaceAll(",", "."));
                        operation.setOperationType(OperationType.EXCHANGE_TON_TO_RUB.toString());
                        result[3] = foundUser.getRubBalance();
                    }
                }
            }
            case "BTC" -> {
                if (!user.getCurrencyTo().equals("TON") & !user.getCurrencyTo().equals("RUB"))
                    throw new ExchangeRateException("Currency exchange error");

                if (!user.getAmount().matches(
                        "((0|([1-9][0-9]{0,11}))\\.((0\\d{0,6}[1-9])|([1-9]\\d{0,7})))|" +
                                "([1-9]\\d{0,11}(\\.0{1,8})?)")) {
                    throw new AmountException("Incorrect value. You can exchange a minimum: 0.00000001 BTC " +
                            "and a maximum: 999999999999.99999999 BTC");
                }

                balance = Double.parseDouble(foundUser.getBtcBalance());

                if (balance < amount) {
                    throw new AmountException("Error. There are not enough funds on the balance");
                }

                ExchangeRate exchangeRate = exchangeRateRepository.findByCurrency("BTC").get();

                balance -= amount;
                switch (user.getCurrencyTo()) {
                    case "TON" -> {
                        amount *= Double.parseDouble(exchangeRate.getToTon());
                        df = new DecimalFormat("#.########");
                        foundUser.setBtcBalance(df.format(balance).replaceAll(",", "."));
                        foundUser.setTonBalance(df.format(amount).replaceAll(",", "."));
                        operation.setOperationType(OperationType.EXCHANGE_BTC_TO_TON.toString());
                        result[3] = foundUser.getTonBalance();
                    }
                    case "RUB" -> {
                        amount *= Double.parseDouble(exchangeRate.getToRub());
                        df = new DecimalFormat("#.########");
                        foundUser.setBtcBalance(df.format(balance).replaceAll(",", "."));
                        foundUser.setRubBalance(df.format(amount).replaceAll(",", "."));
                        operation.setOperationType(OperationType.EXCHANGE_BTC_TO_RUB.toString());
                        result[3] = foundUser.getBtcBalance();
                    }
                }
            }
        }

        operation.setCreatedAt(new Date());
        operation.setAmount(user.getAmount());

        foundUser.addOperation(operation);
        operationRepository.save(operation);

        result[1] = user.getCurrencyTo();
        return result;
    }

    private boolean isAvailable(User foundUser) {
        return (foundUser.getRole().equals("ROLE_USER"));
    }
}