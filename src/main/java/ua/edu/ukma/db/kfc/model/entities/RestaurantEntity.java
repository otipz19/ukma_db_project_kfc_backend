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

    @NotBlank(message = "error.restaurant.title.blank")
    @Size(max = 320, message = "error.restaurant.title.size")
    private String address;

    private boolean isDeleted;
}
