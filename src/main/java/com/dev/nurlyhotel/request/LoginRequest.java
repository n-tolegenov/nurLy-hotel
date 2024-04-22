package com.dev.nurlyhotel.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    /*
        @NotBlank - это аннотация валидации
        Она проверяет, что строка не равна null,
        и после удаления пробельных символов из строки длина оставшейся строки больше нуля.
     */
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
