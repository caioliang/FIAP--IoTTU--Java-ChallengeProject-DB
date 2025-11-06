package br.com.fiap.iottu.api;

import br.com.fiap.iottu.user.User;
import br.com.fiap.iottu.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado, id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já está em uso");
        }
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }
        
        userService.save(user);
        return user;
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @Valid @RequestBody User user) {
        userService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado, id=" + id));
        try {
            return userService.updateWithPasswordPreservation(id, user);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    @PostMapping("/{id}/promote")
    public User promote(@PathVariable Integer id) {
        try {
            return userService.promoteToAdmin(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
