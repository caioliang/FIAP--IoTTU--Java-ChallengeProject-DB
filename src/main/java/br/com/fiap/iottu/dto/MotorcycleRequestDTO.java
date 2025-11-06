package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MotorcycleRequestDTO {
    @NotNull(message = "{validation.motorcycle.status.notNull}")
    @JsonProperty("id_status")
    private Integer statusId;

    @NotNull(message = "{validation.motorcycle.yard.notNull}")
    @JsonProperty("id_patio")
    private Integer yardId;

    @NotBlank(message = "{validation.motorcycle.licensePlate.notBlank}")
    @Size(min = 7, max = 7, message = "{validation.motorcycle.licensePlate.size}")
    @JsonProperty("placa_moto")
    private String licensePlate;

    @NotBlank(message = "{validation.motorcycle.chassi.notBlank}")
    @Size(min = 17, max = 17, message = "{validation.motorcycle.chassi.size}")
    @JsonProperty("chassi_moto")
    private String chassi;

    @NotBlank(message = "{validation.motorcycle.engineNumber.notBlank}")
    @Size(min = 5, max = 20, message = "{validation.motorcycle.engineNumber.size}")
    @JsonProperty("nr_motor_moto")
    private String engineNumber;

    @NotBlank(message = "{validation.motorcycle.model.notBlank}")
    @Size(min = 2, max = 50, message = "{validation.motorcycle.model.size}")
    @JsonProperty("modelo_moto")
    private String model;

    @NotNull(message = "{validation.motorcycle.selectedTag.notNull}")
    @JsonProperty("selected_tag_id")
    private Integer selectedTagId;
}
