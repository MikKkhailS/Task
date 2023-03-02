package ru.relex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * DTO для обмена валюты на другую валюту
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeCurrencyDTO {
    @NotEmpty(message = "Field «secret_key» should be filled")
    @JsonProperty(value = "secret_key")
    private String walletNumber;

    @Pattern(regexp = "(RUB|TON|BTC)", message = "Invalid currency_from name. " +
            "Possible values: «RUB», «TON», «BTC»")
    @JsonProperty(value = "currency_from")
    private String currency;

    @Pattern(regexp = "(RUB|TON|BTC)", message = "Invalid currency_to name. " +
            "Possible values: «RUB», «TON», «BTC»")
    @JsonProperty(value = "currency_to")
    private String currencyTo;

    @NotEmpty(message = "Field «amount» should be filled")
    @JsonProperty(value = "amount")
    private String amount;
}