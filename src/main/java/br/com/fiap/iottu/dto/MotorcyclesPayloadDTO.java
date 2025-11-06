package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MotorcyclesPayloadDTO {

    @JsonProperty("motos")
    private List<MotorcycleDataDTO> motos;
}
