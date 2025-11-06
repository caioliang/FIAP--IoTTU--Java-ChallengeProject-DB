package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AntennaRequestDTO {
    @NotNull(message = "{validation.antenna.yard.notNull}")
    @JsonProperty("id_patio")
    private Integer yardId;

    @NotBlank(message = "{validation.antenna.code.notBlank}")
    @Size(min = 3, max = 50, message = "{validation.antenna.code.size}")
    @JsonProperty("codigo_antena")
    private String code;

    @NotNull(message = "{validation.antenna.latitude.notNull}")
    @DecimalMin(value = "-90.0", message = "{validation.antenna.latitude.min}")
    @DecimalMax(value = "90.0", message = "{validation.antenna.latitude.max}")
    @JsonProperty("latitude_antena")
    private BigDecimal latitude;

    @NotNull(message = "{validation.antenna.longitude.notNull}")
    @DecimalMin(value = "-180.0", message = "{validation.antenna.longitude.min}")
    @DecimalMax(value = "180.0", message = "{validation.antenna.longitude.max}")
    @JsonProperty("longitude_antena")
    private BigDecimal longitude;
}
