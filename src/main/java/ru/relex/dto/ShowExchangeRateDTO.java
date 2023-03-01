package ru.relex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * DTO для просмотра курса валют
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowExchangeRateDTO {
    @NotEmpty(message = "Field «secret_key» should be filled")
    @JsonProperty(value = "secret_key")
    private String walletNumber;

    @Pattern(regexp = "(RUB|TON|BTC)", message = "Invalid currency name. " +
            "Possible values: «RUB», «TON», «BTC»")
    @JsonProperty("currency")
    private String currency;
}