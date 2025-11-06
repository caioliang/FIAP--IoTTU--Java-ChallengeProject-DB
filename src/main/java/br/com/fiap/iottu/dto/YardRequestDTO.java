package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class YardRequestDTO {
    @NotNull(message = "{validation.yard.user.notNull}")
    @JsonProperty("id_usuario")
    private Integer userId;

    @NotBlank(message = "{validation.yard.zipCode.notBlank}")
    @Size(min = 8, max = 8, message = "{validation.yard.zipCode.size}")
    @JsonProperty("cep_patio")
    private String zipCode;

    @NotBlank(message = "{validation.yard.number.notBlank}")
    @Size(min = 1, max = 10, message = "{validation.yard.number.size}")
    @JsonProperty("numero_patio")
    private String number;

    @NotBlank(message = "{validation.yard.city.notBlank}")
    @Size(min = 2, max = 50, message = "{validation.yard.city.size}")
    @JsonProperty("cidade_patio")
    private String city;

    @NotBlank(message = "{validation.yard.state.notBlank}")
    @Size(min = 2, max = 2, message = "{validation.yard.state.size}")
    @JsonProperty("estado_patio")
    private String state;

    @NotNull(message = "{validation.yard.capacity.notNull}")
    @Min(value = 0, message = "{validation.yard.capacity.min}")
    @JsonProperty("capacidade_patio")
    private Integer capacity;
}
