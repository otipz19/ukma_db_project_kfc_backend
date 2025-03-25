package ua.edu.ukma.db.kfc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {
    private int id;

    @NonNull
    private String email;

    @NonNull
    private String passwordHash;
}
