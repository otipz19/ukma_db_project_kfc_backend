package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.ImageMapper;
import ua.edu.ukma.db.kfc.model.entities.ImageEntity;
import ua.edu.ukma.db.kfc.model.helper.ImagePK;
import ua.edu.ukma.db.kfc.repositories.ImageRepository;
import ua.edu.ukma.db.kfc.rest.model.ImageDto;
import ua.edu.ukma.db.kfc.rest.model.ImageTypeDto;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.utils.TimeUtils;
import ua.edu.ukma.db.kfc.validators.ImageValidator;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class ImageService {

    @Inject
    private ImageRepository repository;
    @Inject
    private ImageValidator validator;
    @Inject
    private ImageMapper mapper;

    public ImageInfo getImage(ImageTypeDto type, String title) {
        ImageEntity imageEntity = repository.findById(mapper.toPK(type, title)).orElseThrow(NotFoundException::new);
        validator.validForView(imageEntity);
        return mapper.toInfo(imageEntity);
    }

    public void setImage(ImageTypeDto type, String title, ImageDto imageDto) {
        ImageEntity imageEntity = mapper.toEntity(type, title, imageDto);
        imageEntity.setLastModified(TimeUtils.getCurrentDateTimeUTC());
        validator.validForCreate(imageEntity);
        repository.save(imageEntity);
    }

    public void deleteImage(ImageTypeDto type, String title) {
        ImagePK pk = mapper.toPK(type, title);
        ImageEntity imageEntity = repository.findById(pk).orElseThrow(NotFoundException::new);
        validator.validForDelete(imageEntity);
        repository.delete(pk);
    }

    public record ImageInfo(byte[] image, String mimeType, LocalDateTime lastModified) {}
}
