package ua.edu.ukma.db.kfc.mapper;

import ua.edu.ukma.db.kfc.dto.UserDTO;
import ua.edu.ukma.db.kfc.entity.AppUser;

public class UserMapper {

    public static UserDTO toDTO(AppUser user) {
        return new UserDTO(user.getId(), user.getEmail(), null);
    }

    public static AppUser toEntity(UserDTO userDTO, String hashedPassword) {
        return new AppUser(userDTO.getId(), userDTO.getEmail(), hashedPassword);
    }
}
