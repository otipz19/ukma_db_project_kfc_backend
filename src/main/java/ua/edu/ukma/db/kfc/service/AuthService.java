package ua.edu.ukma.db.kfc.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.repository.UserRepository;
import ua.edu.ukma.db.kfc.entity.AppUser;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@ApplicationScoped
public class AuthService {

    private static final String SECRET_KEY = "e8a7fb24726994b6f2941cbde5bce3ccb3212f45f266eae05a6f39105d9be029";
    private static final long EXPIRATION_TIME = 86400000;

    @Inject
    private UserRepository userRepository;

    public String login(String email, String password) {
        AppUser user = userRepository.findByEmail(email);
        if (user == null || !hashPassword(password).equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
