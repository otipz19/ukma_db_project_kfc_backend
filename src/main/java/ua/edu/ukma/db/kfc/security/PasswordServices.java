package ua.edu.ukma.db.kfc.security;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;

import java.security.MessageDigest;

@ApplicationScoped
public class PasswordServices {

    @SneakyThrows
    public String hash(String password) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public boolean check(String password, String hash) {
        return hash(password).equals(hash);
    }
}
