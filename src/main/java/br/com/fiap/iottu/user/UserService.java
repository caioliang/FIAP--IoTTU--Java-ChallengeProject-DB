package br.com.fiap.iottu.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("{service.user.error.notFoundByEmail}" + username));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(Integer id) {
        return repository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        repository.save(user);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }

    public Map.Entry<User, Boolean> registerOAuth2User(OAuth2User oauth2User) {
        log.info("OAuth2User Attributes: {}", oauth2User.getAttributes());

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String login = oauth2User.getAttribute("login");

        log.info("Extracted - Email: {}, Name: {}, Login: {}", email, name, login);

        if (email == null || email.trim().isEmpty()) {
            if (login != null && !login.trim().isEmpty()) {
                email = login + "@github.com";
                log.info("Using fallback email: {}", email);
            } else {
                throw new IllegalArgumentException("{service.user.error.oauth2EmailInvalid}");
            }
        }

        String finalName = name;
        if (finalName == null || finalName.trim().isEmpty()) {
            finalName = login;
            log.info("Using fallback name (from login): {}", finalName);
        }
        if (finalName == null || finalName.trim().isEmpty()) {
            finalName = email.split("@")[0];
            log.info("Using fallback name (from email part): {}", finalName);
        }
        if (finalName.length() < 3) {
            finalName = finalName + "User";
            log.info("Adjusted name for min length: {}", finalName);
        }

        log.info("Final Email: {}, Final Name: {}", email, finalName);

        Optional<User> existingUser = repository.findByEmail(email);

        if (existingUser.isPresent()) {
            return new SimpleEntry<>(existingUser.get(), false);
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(finalName);
            newUser.setRole("USER");
            newUser.setPassword(passwordEncoder.encode("oauth2_dummy_password"));
            repository.save(newUser);
            return new SimpleEntry<>(newUser, true);
        }
    }

    public User updateWithPasswordPreservation(Integer id, User userDetails) {
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("{service.user.error.notFoundById}" + id));

        userDetails.setPassword(existingUser.getPassword());
        userDetails.setId(id);
        return repository.save(userDetails);
    }

    public User promoteToAdmin(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("{service.user.error.notFoundById}" + id));

        user.setRole("ADMIN");
        return repository.save(user);
    }
}