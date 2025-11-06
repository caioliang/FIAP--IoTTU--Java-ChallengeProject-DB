package br.com.fiap.iottu.antenna;

import br.com.fiap.iottu.yard.Yard;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "TB_ANTENA", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"codigo_antena", "id_patio"}),
        @UniqueConstraint(columnNames = {"latitude_antena", "longitude_antena", "id_patio"})
})
public class Antenna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antena")
    @JsonProperty(value = "id_antena", access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotNull(message = "{validation.antenna.yard.notNull}")
    @ManyToOne
    @JoinColumn(name = "id_patio")
    @JsonProperty("id_patio")
    private Yard yard;

    @NotBlank(message = "{validation.antenna.code.notBlank}")
    @Size(min = 3, max = 50, message = "{validation.antenna.code.size}")
    @Column(name = "codigo_antena")
    @JsonProperty("codigo_antena")
    private String code;

    @NotNull(message = "{validation.antenna.latitude.notNull}")
    @DecimalMin(value = "-90.0", message = "{validation.antenna.latitude.min}")
    @DecimalMax(value = "90.0", message = "{validation.antenna.latitude.max}")
    @Column(name = "latitude_antena", precision = 10, scale = 6)
    @JsonProperty("latitude_antena")
    private BigDecimal latitude;

    @NotNull(message = "{validation.antenna.longitude.notNull}")
    @DecimalMin(value = "-180.0", message = "{validation.antenna.longitude.min}")
    @DecimalMax(value = "180.0", message = "{validation.antenna.longitude.max}")
    @Column(name = "longitude_antena", precision = 10, scale = 6)
    @JsonProperty("longitude_antena")
    private BigDecimal longitude;
}