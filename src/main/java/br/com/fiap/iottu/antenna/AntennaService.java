package br.com.fiap.iottu.antenna;

import br.com.fiap.iottu.dto.AntenaDataDTO;
import br.com.fiap.iottu.yard.Yard;
import br.com.fiap.iottu.yard.YardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AntennaService {

    private static final Logger log = LoggerFactory.getLogger(AntennaService.class);

    @Autowired
    private AntennaRepository repository;

    @Autowired
    private YardService yardService;

    public List<Antenna> findAll() {
        return repository.findAll();
    }
    
    public List<Antenna> findByUserId(Integer userId) {
        return repository.findByYardUserId(userId);
    }

    public Optional<Antenna> findById(Integer id) {
        return repository.findById(id);
    }

    public List<Antenna> findByYardId(Integer yardId) {
        return repository.findByYardId(yardId);
    }

    public void save(Antenna antenna) {
        repository.save(antenna);
    }

    public void validateDuplicate(Antenna antenna) {
        if (antenna.getYard() == null) return;

        Integer yardId = antenna.getYard().getId();
        if (yardId == null) return;

        String code = antenna.getCode() != null ? antenna.getCode().trim() : null;
        if (code != null) {
            Optional<Antenna> existing = repository.findByCodeAndYardId(code, yardId);
            if (existing.isPresent() && (antenna.getId() == null || !existing.get().getId().equals(antenna.getId()))) {
                throw new IllegalArgumentException("{service.antenna.error.duplicateCode}");
            }
        }

        java.math.BigDecimal lat = antenna.getLatitude();
        java.math.BigDecimal lon = antenna.getLongitude();
        if (lat != null && lon != null) {
            List<Antenna> matches = repository.findAllByLatitudeAndLongitudeAndYardId(lat, lon, yardId);
            boolean duplicate = matches.stream().anyMatch(a -> antenna.getId() == null || !a.getId().equals(antenna.getId()));
            if (duplicate) {
                throw new IllegalArgumentException("{service.antenna.error.duplicateCoordinates}");
            }
        }
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public void processAntennasData(List<AntenaDataDTO> antenasData) {
        for (AntenaDataDTO dto : antenasData) {
            try {
                Optional<Yard> yardOptional = yardService.findById(dto.getIdPatio());
                if (yardOptional.isEmpty()) {
                    log.warn("Yard with ID {} not found for antenna {}. Antenna will not be processed.", dto.getIdPatio(), dto.getCodigoAntena());
                    continue;
                }
                Yard yard = yardOptional.get();

                Antenna antenna = repository.findByCode(dto.getCodigoAntena())
                        .orElse(new Antenna());

                antenna.setYard(yard);
                antenna.setCode(dto.getCodigoAntena());
                antenna.setLatitude(BigDecimal.valueOf(dto.getLatitudeAntena()));
                antenna.setLongitude(BigDecimal.valueOf(dto.getLongitudeAntena()));

                repository.save(antenna);
                log.info("Antenna {} processed and saved.", dto.getCodigoAntena());
            } catch (Exception e) {
                log.error("Error processing antenna {}: {}", dto.getCodigoAntena(), e.getMessage(), e);
            }
        }
    }
}