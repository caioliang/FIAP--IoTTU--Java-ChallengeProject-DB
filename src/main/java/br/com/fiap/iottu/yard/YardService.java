package br.com.fiap.iottu.yard;

import br.com.fiap.iottu.antenna.Antenna;
import br.com.fiap.iottu.antenna.AntennaService;
import br.com.fiap.iottu.motorcycle.Motorcycle;
import br.com.fiap.iottu.motorcycle.MotorcycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class YardService {

    @Autowired
    private YardRepository repository;

    @Lazy
    @Autowired
    private AntennaService antennaService;

    @Autowired
    private MotorcycleService motorcycleService;

    @Autowired
    private YardMapService yardMapService;

    public List<Yard> findAll() {
        return repository.findAll();
    }
    
    public List<Yard> findByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }

    public Optional<Yard> findById(Integer id) {
        return repository.findById(id);
    }

    public void validateDuplicate(Yard yard) {
        if (yard.getZipCode() == null || yard.getNumber() == null) return;
        Optional<Yard> existing = repository.findByZipCodeAndNumber(yard.getZipCode(), yard.getNumber());
        if (existing.isPresent()) {
            Yard found = existing.get();
            if (yard.getId() == null || !found.getId().equals(yard.getId())) {
                throw new IllegalArgumentException("{service.yard.error.duplicate}" );
            }
        }
    }

    public void save(Yard yard) {
        repository.save(yard);
    }

    public void deleteById(Integer id) {
        List<Motorcycle> motorcycles = motorcycleService.findByYardId(id);
        if (motorcycles != null && !motorcycles.isEmpty()) {
            throw new IllegalStateException("{message.error.yard.deleteHasMotorcycles}" + id);
        }
        repository.deleteById(id);
    }

    public YardMapDTO prepareYardMapData(Integer yardId) {
        findById(yardId)
                .orElseThrow(() -> new IllegalArgumentException("{service.yard.error.invalidId}" + yardId));

        List<Antenna> antennas = antennaService.findByYardId(yardId);
        List<Motorcycle> motorcycles = motorcycleService.findByYardId(yardId);

        return yardMapService.createMap(antennas, motorcycles);
    }
}