package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO‐style entity for returning id + name statistics results.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamedEntity {

    private Integer id;

    @NotBlank(message = "error.named-entity.name.blank")
    @Size(max = 128, message = "error.named-entity.name.size")
    private String name;
}
