package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MotorcycleDataDTO {

    @JsonProperty("status")
    private String statusDescription;

    @JsonProperty("alerta")
    private String alerta;

    @JsonProperty("id_status")
    private Integer idStatus;

    @JsonProperty("id_patio")
    private Integer idPatio;

    @JsonProperty("placa_moto")
    private String placaMoto;

    @JsonProperty("chassi_moto")
    private String chassiMoto;

    @JsonProperty("nr_motor_moto")
    private String nrMotorMoto;

    @JsonProperty("modelo_moto")
    private String modeloMoto;

    @JsonProperty("codigo_rfid_tag")
    private String codigoRfidTag;

    @JsonProperty("ssid_wifi_tag")
    private String ssidWifiTag;

    @JsonProperty("x")
    private Double x;

    @JsonProperty("y")
    private Double y;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;
}
