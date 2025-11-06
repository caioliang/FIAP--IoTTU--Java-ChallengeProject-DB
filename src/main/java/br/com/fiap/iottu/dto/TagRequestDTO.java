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
public class TagRequestDTO {
    @NotBlank(message = "{validation.tag.rfidCode.notBlank}")
    @Size(min = 5, max = 20, message = "{validation.tag.rfidCode.size}")
    @JsonProperty("codigo_rfid_tag")
    private String rfidCode;

    @NotBlank(message = "{validation.tag.wifiSsid.notBlank}")
    @Size(min = 2, max = 32, message = "{validation.tag.wifiSsid.size}")
    @JsonProperty("ssid_wifi_tag")
    private String wifiSsid;

    @NotNull(message = "{validation.tag.latitude.notNull}")
    @DecimalMin(value = "-90.0", message = "{validation.tag.latitude.min}")
    @DecimalMax(value = "90.0", message = "{validation.tag.latitude.max}")
    @JsonProperty("latitude_tag")
    private BigDecimal latitude;

    @NotNull(message = "{validation.tag.longitude.notNull}")
    @DecimalMin(value = "-180.0", message = "{validation.tag.longitude.min}")
    @DecimalMax(value = "180.0", message = "{validation.tag.longitude.max}")
    @JsonProperty("longitude_tag")
    private BigDecimal longitude;
}
