package br.com.fiap.iottu.yard;

import br.com.fiap.iottu.antenna.Antenna;
import br.com.fiap.iottu.motorcycle.Motorcycle;
import lombok.Data;

import java.util.List;

@Data
public class YardMapDTO {

    private double mapWidth;
    private double mapHeight;
    private List<DrawableAntenna> antennas;
    private List<DrawableMotorcycle> motorcycles;
    private List<Motorcycle> outOfBoundsMotorcycles;

    @Data
    public static class DrawableAntenna {
        private final Antenna antenna;
        private final double x;
        private final double y;
    }

    @Data
    public static class DrawableMotorcycle {
        private final Motorcycle motorcycle;
        private final double x;
        private final double y;
    }
}
