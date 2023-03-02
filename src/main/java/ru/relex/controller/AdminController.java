package ru.relex.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.relex.dto.ChangeExchangeRateDTO;
import ru.relex.dto.ShowExchangeRateDTO;
import ru.relex.dto.ShowOperationsCountDTO;
import ru.relex.model.User;
import ru.relex.service.AdminService;
import ru.relex.service.impl.AdminServiceImpl;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(AdminServiceImpl adminService, ModelMapper modelMapper) {
        this.adminService = adminService;
        this.modelMapper = modelMapper;
    }

    private Map<String, String> response;

    @PostMapping("/change-exchange-rate")
    public ResponseEntity<?> changeExchangeRate(@RequestBody @Valid ChangeExchangeRateDTO dto) {
        String[] result = adminService.changeExchangeRate(convertToUser(dto));
        switch (result[0]) {
            case "BTC" -> response = Map.of("TON", result[1], "RUB", result[2]);
            case "TON" -> response = Map.of("BTC", result[1], "RUB", result[2]);
            case "RUB" -> response = Map.of("BTC", result[1], "TON", result[2]);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-total-amount")
    public ResponseEntity<?> getTotalAmount(@RequestBody @Valid ShowExchangeRateDTO dto) {
        String[] result = adminService.getTotalAmount(convertToUser(dto));
        switch (result[0]) {
            case "BTC" -> response = Collections.singletonMap("BTC", result[1]);
            case "TON" -> response = Collections.singletonMap("TON", result[1]);
            case "RUB" -> response = Collections.singletonMap("RUB", result[1]);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/show-operations-count")
    public ResponseEntity<?> showOperationsCount(@RequestBody @Valid ShowOperationsCountDTO dto) {
        String result = adminService.showOperationsCount(convertToUser(dto));
        response = Collections.singletonMap("transaction_count", result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private User convertToUser(Object dto) {
        return modelMapper.map(dto, User.class);
    }
}