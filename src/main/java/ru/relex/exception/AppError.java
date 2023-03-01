package ru.relex.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс ошибки, который принимает статус ошибки и сообщение об ошибке
 */

@Getter
@Setter
@AllArgsConstructor
public class AppError {
    private int statusCode;
    private String message;
}