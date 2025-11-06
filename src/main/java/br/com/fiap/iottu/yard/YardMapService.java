package br.com.fiap.iottu.yard;

import br.com.fiap.iottu.antenna.Antenna;
import br.com.fiap.iottu.motorcycle.Motorcycle;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YardMapService {

    private static final double MAX_MAP_WIDTH = 800.0;
    private static final double MAX_MAP_HEIGHT = 600.0;
    private static final double PADDING = 40.0;

    public YardMapDTO createMap(List<Antenna> antennas, List<Motorcycle> motorcycles) {
        if (antennas == null || antennas.isEmpty()) {
            YardMapDTO emptyDto = new YardMapDTO();
            emptyDto.setAntennas(Collections.emptyList());
            emptyDto.setMotorcycles(Collections.emptyList());
            emptyDto.setOutOfBoundsMotorcycles(Collections.emptyList());
            return emptyDto;
        }

        double minLat = antennas.stream().mapToDouble(a -> a.getLatitude().doubleValue()).min().orElse(0);
        double maxLat = antennas.stream().mapToDouble(a -> a.getLatitude().doubleValue()).max().orElse(0);
        double minLon = antennas.stream().mapToDouble(a -> a.getLongitude().doubleValue()).min().orElse(0);
        double maxLon = antennas.stream().mapToDouble(a -> a.getLongitude().doubleValue()).max().orElse(0);

        double worldWidth = maxLon - minLon;
        double worldHeight = maxLat - minLat;

        if (worldWidth == 0) worldWidth = 1;
        if (worldHeight == 0) worldHeight = 1;

        double scaleX = (MAX_MAP_WIDTH - 2 * PADDING) / worldWidth;
        double scaleY = (MAX_MAP_HEIGHT - 2 * PADDING) / worldHeight;
        double scale = Math.min(scaleX, scaleY);

        double mapWidth = worldWidth * scale + 2 * PADDING;
        double mapHeight = worldHeight * scale + 2 * PADDING;

        YardMapDTO dto = new YardMapDTO();
        dto.setMapWidth(mapWidth);
        dto.setMapHeight(mapHeight);

        dto.setAntennas(antennas.stream().map(antenna -> {
            double x = ((antenna.getLongitude().doubleValue() - minLon) * scale) + PADDING;
            double y = ((maxLat - antenna.getLatitude().doubleValue()) * scale) + PADDING;
            return new YardMapDTO.DrawableAntenna(antenna, x, y);
        }).collect(Collectors.toList()));

        Map<Boolean, List<Motorcycle>> partitionedMotorcycles = motorcycles.stream()
                .filter(moto -> moto.getTags() != null && !moto.getTags().isEmpty())
                .collect(Collectors.partitioningBy(moto -> {
                    br.com.fiap.iottu.tag.Tag firstTag = moto.getTags().get(0);
                    double lat = firstTag.getLatitude().doubleValue();
                    double lon = firstTag.getLongitude().doubleValue();
                    return lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon;
                }));

        List<Motorcycle> inBounds = partitionedMotorcycles.get(true);
        List<Motorcycle> outOfBounds = partitionedMotorcycles.get(false);

        dto.setMotorcycles(inBounds.stream().map(moto -> {
            br.com.fiap.iottu.tag.Tag firstTag = moto.getTags().get(0);
            double x = ((firstTag.getLongitude().doubleValue() - minLon) * scale) + PADDING;
            double y = ((maxLat - firstTag.getLatitude().doubleValue()) * scale) + PADDING;
            return new YardMapDTO.DrawableMotorcycle(moto, x, y);
        }).collect(Collectors.toList()));

        dto.setOutOfBoundsMotorcycles(outOfBounds);

        return dto;
    }
}
