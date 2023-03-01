package ru.relex.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.relex.dto.RegisterDTO;
import ru.relex.model.User;
import ru.relex.service.impl.AdminServiceImpl;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminController {
    private final AdminServiceImpl adminService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(AdminServiceImpl adminService, ModelMapper modelMapper) {
        this.adminService = adminService;
        this.modelMapper = modelMapper;
    }

    private Map<String, String> response;

    @GetMapping("/get-total-amount")
    public ResponseEntity<?> signUp(@RequestBody @Valid RegisterDTO dto) {
        var user = convertToUser(dto);
        response = new HashMap<>();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private User convertToUser(Object dto) {
        return modelMapper.map(dto, User.class);
    }
}