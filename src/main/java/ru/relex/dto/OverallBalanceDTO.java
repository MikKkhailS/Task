package ru.relex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * DTO для регистрации нового пользователя
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverallBalanceDTO {
    @NotEmpty(message = "Field «secret_key» should be filled")
    @JsonProperty(value = "secret_key")
    private String walletNumber;
}