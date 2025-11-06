package br.com.fiap.iottu.api;

import br.com.fiap.iottu.dto.YardRequestDTO;
import br.com.fiap.iottu.user.User;
import br.com.fiap.iottu.user.UserService;
import br.com.fiap.iottu.yard.Yard;
import br.com.fiap.iottu.yard.YardMapDTO;
import org.springframework.http.HttpStatus;
import br.com.fiap.iottu.yard.YardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/yards")
@CacheConfig(cacheNames = "yards")
public class YardRestController {

    @Autowired
    private YardService yardService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public List<Yard> list(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return yardService.findByUserId(userId);
        }
        return yardService.findAll();
    }

    @GetMapping("/{id}")
    public Yard getById(@PathVariable Integer id) {
        return yardService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pátio não encontrado, id=" + id));
    }

    @GetMapping("/{id}/map")
    public YardMapDTO getYardMap(@PathVariable Integer id) {
        try {
            return yardService.prepareYardMapData(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Yard create(@Valid @RequestBody YardRequestDTO dto) {
        try {
            Yard yard = new Yard();
            
            User user = userService.findById(dto.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não encontrado"));
            yard.setUser(user);
            
            yard.setZipCode(dto.getZipCode());
            yard.setNumber(dto.getNumber());
            yard.setCity(dto.getCity());
            yard.setState(dto.getState());
            yard.setCapacity(dto.getCapacity());
            
            yardService.validateDuplicate(yard);
            yardService.save(yard);
            return yard;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Yard update(@PathVariable Integer id, @Valid @RequestBody YardRequestDTO dto) {
        try {
            Yard yard = new Yard();
            yard.setId(id);
            
            User user = userService.findById(dto.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não encontrado"));
            yard.setUser(user);
            
            yard.setZipCode(dto.getZipCode());
            yard.setNumber(dto.getNumber());
            yard.setCity(dto.getCity());
            yard.setState(dto.getState());
            yard.setCapacity(dto.getCapacity());
            
            yardService.validateDuplicate(yard);
            yardService.save(yard);
            return yard;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        yardService.deleteById(id);
    }
}
