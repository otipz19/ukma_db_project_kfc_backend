package ua.edu.ukma.db.kfc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private int id;

    @NonNull
    private String surname;

    @NonNull
    private String firstName;

    private String middleName;

    @NonNull
    private int bonuses;

    private LocalDate birthDate;

    private boolean isDeleted = false;

    @NonNull
    private int userId;
}
