package br.com.fiap.iottu.api;

import br.com.fiap.iottu.dto.MotorcycleRequestDTO;
import br.com.fiap.iottu.motorcycle.Motorcycle;
import br.com.fiap.iottu.motorcycle.MotorcycleService;
import br.com.fiap.iottu.motorcyclestatus.MotorcycleStatus;
import br.com.fiap.iottu.motorcyclestatus.MotorcycleStatusRepository;
import br.com.fiap.iottu.yard.Yard;
import br.com.fiap.iottu.yard.YardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/motorcycles")
@CacheConfig(cacheNames = "motorcycles")
public class MotorcycleRestController {

    private static final Logger log = LoggerFactory.getLogger(MotorcycleRestController.class);

    @Autowired
    private MotorcycleService motorcycleService;
    
    @Autowired
    private MotorcycleStatusRepository motorcycleStatusRepository;
    
    @Autowired
    private YardService yardService;

    @GetMapping
    @Cacheable
    public List<Motorcycle> list(@RequestParam(required = false) Integer userId) {
        List<Motorcycle> list;
        if (userId != null) {
            list = motorcycleService.findByUserId(userId);
        } else {
            list = motorcycleService.findAll();
        }
        log.info("GET /api/v1/motorcycles?userId={} -> {} registros", userId, list.size());
        return list;
    }

    @GetMapping("/{id}")
    public Motorcycle getById(@PathVariable Integer id) {
        return motorcycleService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto não encontrada, id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
        public Motorcycle create(@Valid @RequestBody MotorcycleRequestDTO dto) {
        try {
            Motorcycle motorcycle = new Motorcycle();
            
            MotorcycleStatus status = motorcycleStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status não encontrado"));
            motorcycle.setStatus(status);
            
            Yard yard = yardService.findById(dto.getYardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pátio não encontrado"));
            motorcycle.setYard(yard);
            
            motorcycle.setLicensePlate(dto.getLicensePlate());
            motorcycle.setModel(dto.getModel());
            motorcycle.setChassi(dto.getChassi());
            motorcycle.setEngineNumber(dto.getEngineNumber());
            motorcycle.setSelectedTagId(dto.getSelectedTagId());
            
            return motorcycleService.saveOrUpdateWithTag(motorcycle, dto.getSelectedTagId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @CacheEvict(allEntries = true)
        public Motorcycle update(@PathVariable Integer id, @Valid @RequestBody MotorcycleRequestDTO dto) {
        motorcycleService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto não encontrada, id=" + id));
        try {
            Motorcycle motorcycle = new Motorcycle();
            motorcycle.setId(id);
            
            MotorcycleStatus status = motorcycleStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status não encontrado"));
            motorcycle.setStatus(status);
            
            Yard yard = yardService.findById(dto.getYardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pátio não encontrado"));
            motorcycle.setYard(yard);
            
            motorcycle.setLicensePlate(dto.getLicensePlate());
            motorcycle.setModel(dto.getModel());
            motorcycle.setChassi(dto.getChassi());
            motorcycle.setEngineNumber(dto.getEngineNumber());
            motorcycle.setSelectedTagId(dto.getSelectedTagId());
            
            return motorcycleService.saveOrUpdateWithTag(motorcycle, dto.getSelectedTagId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable Integer id) {
        motorcycleService.deleteByIdWithTagUnbinding(id);
    }
}
