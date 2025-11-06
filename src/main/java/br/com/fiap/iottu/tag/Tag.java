package br.com.fiap.iottu.tag;

import br.com.fiap.iottu.motorcycle.Motorcycle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "TB_TAG", uniqueConstraints = @UniqueConstraint(columnNames = {"codigo_rfid_tag"}))
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tag")
    @JsonProperty(value = "id_tag", access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "{validation.tag.rfidCode.notBlank}")
    @Size(min = 5, max = 20, message = "{validation.tag.rfidCode.size}")
    @Column(name = "codigo_rfid_tag")
    @JsonProperty("codigo_rfid_tag")
    private String rfidCode;

    @NotBlank(message = "{validation.tag.wifiSsid.notBlank}")
    @Size(min = 2, max = 32, message = "{validation.tag.wifiSsid.size}")
    @Column(name = "ssid_wifi_tag")
    @JsonProperty("ssid_wifi_tag")
    private String wifiSsid;

    @NotNull(message = "{validation.tag.latitude.notNull}")
    @DecimalMin(value = "-90.0", message = "{validation.tag.latitude.min}")
    @DecimalMax(value = "90.0", message = "{validation.tag.latitude.max}")
    @Column(name = "latitude_tag", precision = 10, scale = 6)
    @JsonProperty("latitude_tag")
    private BigDecimal latitude;

    @NotNull(message = "{validation.tag.longitude.notNull}")
    @DecimalMin(value = "-180.0", message = "{validation.tag.longitude.min}")
    @DecimalMax(value = "180.0", message = "{validation.tag.longitude.max}")
    @Column(name = "longitude_tag", precision = 10, scale = 6)
    @JsonProperty("longitude_tag")
    private BigDecimal longitude;

    @Column(name = "data_hora_tag")
    @JsonProperty("data_hora_tag")
    private LocalDateTime timestamp;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Motorcycle> motorcycles;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        timestamp = LocalDateTime.now();
    }
}