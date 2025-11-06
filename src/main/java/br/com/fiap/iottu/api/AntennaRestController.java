package br.com.fiap.iottu.api;

import br.com.fiap.iottu.antenna.Antenna;
import br.com.fiap.iottu.antenna.AntennaService;
import br.com.fiap.iottu.dto.AntennaRequestDTO;
import br.com.fiap.iottu.yard.Yard;
import br.com.fiap.iottu.yard.YardService;
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
@RequestMapping("/api/v1/antennas")
@CacheConfig(cacheNames = "antennas")
public class AntennaRestController {

    @Autowired
    private AntennaService antennaService;
    
    @Autowired
    private YardService yardService;

    @GetMapping
    @Cacheable
    public List<Antenna> list(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return antennaService.findByUserId(userId);
        }
        return antennaService.findAll();
    }

    @GetMapping("/{id}")
    public Antenna getById(@PathVariable Integer id) {
        return antennaService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Antena não encontrada, id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    public Antenna create(@Valid @RequestBody AntennaRequestDTO dto) {
        try {
            Antenna antenna = new Antenna();
            
            Yard yard = yardService.findById(dto.getYardId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pátio não encontrado"));
            antenna.setYard(yard);
            
            antenna.setCode(dto.getCode());
            antenna.setLatitude(dto.getLatitude());
            antenna.setLongitude(dto.getLongitude());
            
            antennaService.validateDuplicate(antenna);
            antennaService.save(antenna);
            return antenna;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @CacheEvict(allEntries = true)
    public Antenna update(@PathVariable Integer id, @Valid @RequestBody AntennaRequestDTO dto) {
        antennaService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Antena não encontrada, id=" + id));
        try {
            Antenna antenna = new Antenna();
            antenna.setId(id);
            
            Yard yard = yardService.findById(dto.getYardId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pátio não encontrado"));
            antenna.setYard(yard);
            
            antenna.setCode(dto.getCode());
            antenna.setLatitude(dto.getLatitude());
            antenna.setLongitude(dto.getLongitude());
            
            antennaService.validateDuplicate(antenna);
            antennaService.save(antenna);
            return antenna;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable Integer id) {
        antennaService.deleteById(id);
    }
}
