package ru.relex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * DTO для регистрации нового пользователя
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterDTO {
    @NotEmpty(message = "Field «username» should be filled")
    @JsonProperty("username")
    private String username;

    @NotEmpty(message = "Field «email» should be filled")
    @Email(message = "«email» should be in email format (example: «test@gmail.com»)")
    @JsonProperty("email")
    private String email;
}