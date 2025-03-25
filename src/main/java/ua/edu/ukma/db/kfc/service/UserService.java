package ua.edu.ukma.db.kfc.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.repository.UserRepository;
import ua.edu.ukma.db.kfc.dto.UserDTO;
import ua.edu.ukma.db.kfc.entity.AppUser;
import ua.edu.ukma.db.kfc.mapper.UserMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    public void registerUser(UserDTO userDTO) {
        if (userRepository.emailExists(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        String hashedPassword = this.hashPassword(userDTO.getPassword());
        AppUser user = UserMapper.toEntity(userDTO, hashedPassword);
        userRepository.saveUser(user);
    }

    public UserDTO getUserByEmail(String email) {
        AppUser user = userRepository.findByEmail(email);
        return user != null ? UserMapper.toDTO(user) : null;
    }

    public UserDTO getUserById(Long id) {
        AppUser user = userRepository.findById(id);
        return user != null ? UserMapper.toDTO(user) : null;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void updateUser(Long id, UserDTO userDTO) {
        AppUser existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!existingUser.getEmail().equals(userDTO.getEmail()) && userRepository.emailExists(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        String hashedPassword = (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
                ? this.hashPassword(userDTO.getPassword())
                : existingUser.getPasswordHash();

        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPasswordHash(hashedPassword);

        userRepository.updateUser(existingUser);
    }

    public void deleteUser(Long id) {
        AppUser existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteUser(id);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] digest = messageDigest.digest();
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
