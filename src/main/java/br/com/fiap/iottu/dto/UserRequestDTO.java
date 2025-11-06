package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "{validation.user.name.notBlank}")
    @Size(min = 3, max = 100, message = "{validation.user.name.size}")
    @JsonProperty("nome_usuario")
    private String name;

    @NotBlank(message = "{validation.user.email.notBlank}")
    @Email(message = "{validation.user.email.invalid}")
    @Size(min = 5, max = 100, message = "{validation.user.email.size}")
    @JsonProperty("email_usuario")
    private String email;

    @NotBlank(message = "{validation.user.password.notBlank}")
    @Size(min = 6, message = "{validation.user.password.size}")
    @JsonProperty("senha_usuario")
    private String password;

    @JsonProperty("role")
    private String role;
}
