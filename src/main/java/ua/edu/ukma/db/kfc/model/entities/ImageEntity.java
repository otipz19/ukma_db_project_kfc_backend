package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.db.kfc.model.enums.ImageType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {

    @NotNull(message = "error.image.type.null")
    private ImageType type;

    @NotBlank(message = "error.image.title.blank")
    @Size(max = 64, message = "error.image.title.size")
    private String title;

    @NotEmpty(message = "error.image.empty")
    private byte[] image;

    @NotBlank(message = "error.image.mime-type.blank")
    @Pattern(regexp = "image/(png|jpeg|jpg|gif)", message = "error.image.mime-type.invalid")
    private String mimeType;

    private LocalDateTime lastModified;
}
