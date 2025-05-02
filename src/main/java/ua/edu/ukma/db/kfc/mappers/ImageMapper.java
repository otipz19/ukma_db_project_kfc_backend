package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.db.kfc.model.entities.ImageEntity;
import ua.edu.ukma.db.kfc.model.helper.ImagePK;
import ua.edu.ukma.db.kfc.rest.model.ImageDto;
import ua.edu.ukma.db.kfc.rest.model.ImageTypeDto;
import ua.edu.ukma.db.kfc.services.ImageService;

@Mapper(config = MapperConfiguration.class)
public interface ImageMapper {

    ImagePK toPK(ImageTypeDto type, String title);

    @Mapping(target = "image", expression = "java( dto.getImage() )")
    ImageEntity toEntity(ImageTypeDto type, String title, ImageDto dto);

    @Mapping(target = "image", expression = "java( entity.getImage() )")
    ImageService.ImageInfo toInfo(ImageEntity entity);
}
