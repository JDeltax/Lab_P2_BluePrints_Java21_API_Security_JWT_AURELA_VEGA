package co.edu.eci.blueprints.security;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InMemoryUserService {

    public record UserRecord(String passwordHash, String scopes) {}

    private final Map<String, UserRecord> users;
    private final PasswordEncoder encoder;

    public InMemoryUserService(PasswordEncoder encoder) {
        this.encoder = encoder;
        this.users = Map.of(
            "student", new UserRecord(encoder.encode("student123"), "blueprints.read"),
            "assistant", new UserRecord(encoder.encode("assistant123"), "blueprints.read blueprints.write")
        );
    }

    public boolean isValid(String username, String rawPassword) {
        UserRecord u = users.get(username);
        return u != null && encoder.matches(rawPassword, u.passwordHash());
    }

    public String scopesFor(String username) {
        UserRecord u = users.get(username);
        return u != null ? u.scopes() : "";
    }
}
