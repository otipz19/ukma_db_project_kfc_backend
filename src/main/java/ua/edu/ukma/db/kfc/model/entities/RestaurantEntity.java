package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEntity {

    private int id;

    @NotBlank
    @Size(max = 320)
    private String address;

    private boolean isDeleted;
}
