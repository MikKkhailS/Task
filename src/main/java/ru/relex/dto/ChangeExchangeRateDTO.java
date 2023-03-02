package ru.relex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * DTO для изменения курса валют (для администратора)
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeExchangeRateDTO {
    @NotEmpty(message = "Field «secret_key» should be filled")
    @JsonProperty(value = "secret_key")
    private String walletNumber;

    @Pattern(regexp = "(RUB|TON|BTC)", message = "Invalid base currency name. " +
            "Possible values: «RUB», «TON», «BTC»")
    @JsonProperty(value = "base_currency")
    private String currency;

    @JsonProperty(value = "BTC")
    private String btcBalance;

    @JsonProperty(value = "RUB")
    private String rubBalance;

    @JsonProperty(value = "TON")
    private String tonBalance;
}