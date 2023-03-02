package ru.relex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * DTO для просмотра количества операций за указанный период (для администратора)
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowOperationsCountDTO {
    @NotEmpty(message = "Field «secret_key» should be filled")
    @JsonProperty(value = "secret_key")
    private String walletNumber;

    @Pattern.List({
            @Pattern(regexp = "[0-3][0-9]\\.[0-1][0-9]\\.[1-9][0-9]{3}",
                    message = "Invalid date_from value"),
            @Pattern(regexp = "[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}",
                    message = "Invalid date_from format. Correct value example: «01.01.2023»")
    })
    @JsonProperty("date_from")
    private String dateFrom;

    @Pattern.List({
            @Pattern(regexp = "[0-3][0-9]\\.[0-1][0-9]\\.[1-9][0-9]{3}",
                    message = "Invalid date_to value"),
            @Pattern(regexp = "[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}",
                    message = "Invalid date_to format. Correct value example: «01.01.2023»")
    })
    @JsonProperty("date_to")
    private String dateTo;
}