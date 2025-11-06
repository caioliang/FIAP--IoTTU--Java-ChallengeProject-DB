package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AntenasPayloadDTO {
    @JsonProperty("antenas")
    private List<AntenaDataDTO> antenas;

    public AntenasPayloadDTO() {}

    public List<AntenaDataDTO> getAntenas() {
        return antenas;
    }

    public void setAntenas(List<AntenaDataDTO> antenas) {
        this.antenas = antenas;
    }
}
