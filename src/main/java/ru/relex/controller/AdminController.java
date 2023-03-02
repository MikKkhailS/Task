package ru.relex.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.relex.dto.ShowExchangeRateDTO;
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

    @GetMapping("/get-total-amount")
    public ResponseEntity<?> getTotalAmount(@RequestBody @Valid ShowExchangeRateDTO dto) {
        var result = adminService.getTotalAmount(convertToUser(dto));
        switch (result[0]) {
            case "BTC" -> response = Collections.singletonMap("BTC", result[1]);
            case "TON" -> response = Collections.singletonMap("TON", result[1]);
            case "RUB" -> response = Collections.singletonMap("RUB", result[1]);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private User convertToUser(Object dto) {
        return modelMapper.map(dto, User.class);
    }
}