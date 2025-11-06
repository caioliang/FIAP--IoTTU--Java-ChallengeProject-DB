package br.com.fiap.iottu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AntenaDataDTO {
    @JsonProperty("id_antena")
    private Integer idAntena;
    @JsonProperty("id_patio")
    private Integer idPatio;
    @JsonProperty("codigo_antena")
    private String codigoAntena;
    @JsonProperty("latitude_antena")
    private Double latitudeAntena;
    @JsonProperty("longitude_antena")
    private Double longitudeAntena;
    private Double x;
    private Double y;

    public AntenaDataDTO() {}

    public Integer getIdAntena() {
        return idAntena;
    }

    public void setIdAntena(Integer idAntena) {
        this.idAntena = idAntena;
    }

    public Integer getIdPatio() {
        return idPatio;
    }

    public void setIdPatio(Integer idPatio) {
        this.idPatio = idPatio;
    }

    public String getCodigoAntena() {
        return codigoAntena;
    }

    public void setCodigoAntena(String codigoAntena) {
        this.codigoAntena = codigoAntena;
    }

    public Double getLatitudeAntena() {
        return latitudeAntena;
    }

    public void setLatitudeAntena(Double latitudeAntena) {
        this.latitudeAntena = latitudeAntena;
    }

    public Double getLongitudeAntena() {
        return longitudeAntena;
    }

    public void setLongitudeAntena(Double longitudeAntena) {
        this.longitudeAntena = longitudeAntena;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
