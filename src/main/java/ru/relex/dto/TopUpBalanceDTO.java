package ru.relex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * DTO для пополнения кошелька пользователя
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopUpBalanceDTO {
    @NotEmpty(message = "Field «secret_key» should be filled")
    @JsonProperty(value = "secret_key")
    private String walletNumber;

    @Pattern(regexp = "[1-9]\\d{1,5}",
            message = "Incorrect value. For one operation you can replenish " +
                    "a minimum: 10 RUB and a maximum: 999999 RUB (Integer value only)")
    @JsonProperty("RUB_wallet")
    private String rubBalance;
}