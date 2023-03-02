package ru.relex.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.relex.dto.*;
import ru.relex.model.User;
import ru.relex.service.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    private Map<String, String> response;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid RegisterDTO dto) {
        var user = convertToUser(dto);
        response = Collections.singletonMap("secret_key", userService.register(user));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/show-wallet-balance")
    public ResponseEntity<?> getOverallBalance(@RequestBody @Valid OverallBalanceDTO dto) {
        String[] result = userService.getOverallBalance(convertToUser(dto));
        response = Map.of(
                "BTC_wallet", result[0],
                "TON_wallet", result[1],
                "RUB_wallet", result[2]
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/top-up-balance")
    public ResponseEntity<?> topUpBalance(@RequestBody @Valid TopUpBalanceDTO dto) {
        var user = convertToUser(dto);
        response = Collections.singletonMap("RUB_wallet", userService.topUpBalance(user));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/withdraw-currency")
    public ResponseEntity<?> withdrawCurrency(@RequestBody @Valid WithdrawCurrencyDTO dto) {
        String[] result = userService.withdrawCurrency(convertToUser(dto));
        switch (result[0]) {
            case "BTC" -> response = Collections.singletonMap("BTC_wallet", result[1]);
            case "TON" -> response = Collections.singletonMap("TON_wallet", result[1]);
            case "RUB" -> response = Collections.singletonMap("RUB_wallet", result[1]);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/show-exchange-rate")
    public ResponseEntity<?> showExchangeRate(@RequestBody @Valid ShowExchangeRateDTO dto) {
        String[] result = userService.showExchangeRate(convertToUser(dto));
        switch (result[0]) {
            case "BTC" -> response = Map.of("TON", result[1], "RUB", result[2]);
            case "TON" -> response = Map.of("BTC", result[1], "RUB", result[2]);
            case "RUB" -> response = Map.of("BTC", result[1], "TON", result[2]);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/exchange-currency")
    public ResponseEntity<?> exchangeCurrency(@RequestBody @Valid ExchangeCurrencyDTO dto) {
        String[] result = userService.exchangeCurrency(convertToUser(dto));
        response = Map.of(
                    "currency_from", result[0],
                    "currency_to", result[1],
                    "amount_from", result[2],
                    "amount_to", result[3]
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private User convertToUser(Object dto) {
        return modelMapper.map(dto, User.class);
    }
}