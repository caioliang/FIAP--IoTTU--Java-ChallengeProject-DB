package br.com.fiap.iottu.user;

import br.com.fiap.iottu.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "TB_USUARIO")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @JsonProperty(value = "id_usuario", access = JsonProperty.Access.READ_ONLY)
    @NotNull(groups = OnUpdate.class, message = "{validation.user.id.notNull}")
    private Integer id;

    @NotBlank(message = "{validation.user.name.notBlank}")
    @Size(min = 3, max = 100, message = "{validation.user.name.size}")
    @Column(name = "nome_usuario")
    @JsonProperty("nome_usuario")
    private String name;

    @NotBlank(message = "{validation.user.email.notBlank}")
    @Email(message = "{validation.user.email.invalid}")
    @Size(min = 5, max = 100, message = "{validation.user.email.size}")
    @Column(name = "email_usuario")
    @JsonProperty("email_usuario")
    private String email;

    @NotBlank(message = "{validation.user.password.notBlank}")
    @Size(min = 6, message = "{validation.user.password.size}")
    @Column(name = "senha_usuario")
    @JsonProperty(value = "senha_usuario", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "{validation.user.role.notBlank}", groups = OnUpdate.class)
    @Column(name = "role")
    private String role;
}