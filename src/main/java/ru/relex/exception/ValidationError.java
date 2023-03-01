package ru.relex.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Класс для ошибок, полученных при валидации. Принимает статус и сообщение ошибки или ошибок
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    private int statusCode;
    private List<String> messages;
}