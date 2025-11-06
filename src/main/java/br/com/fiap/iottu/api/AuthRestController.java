package br.com.fiap.iottu.api;

import br.com.fiap.iottu.user.User;
import br.com.fiap.iottu.user.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static class LoginRequest {
        @NotBlank
        @Email
        @JsonProperty("email_usuario")
        public String email;

        @NotBlank
        @JsonProperty("senha_usuario")
        public String password;
    }

    public static class LoginResponse {
        @JsonProperty("id_usuario")
        public Integer id;
        @JsonProperty("nome_usuario")
        public String name;
        @JsonProperty("email_usuario")
        public String email;
        public String role;
        public String message;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        LoginResponse resp = new LoginResponse();
        resp.id = user.getId();
        resp.name = user.getName();
        resp.email = user.getEmail();
        resp.role = user.getRole();
        resp.message = "Login efetuado com sucesso";
        return resp;
    }
}
