package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.*;
import ua.edu.ukma.db.kfc.rest.api.ImageControllerApi;
import ua.edu.ukma.db.kfc.rest.model.ImageDto;
import ua.edu.ukma.db.kfc.rest.model.ImageTypeDto;
import ua.edu.ukma.db.kfc.services.ImageService;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.Timestamp;

@ApplicationScoped
public class ImageController implements ImageControllerApi {

    private static final CacheControl CACHE_CONTROL;
    static {
        CACHE_CONTROL = new CacheControl();
        CACHE_CONTROL.setNoCache(true);
        CACHE_CONTROL.setMaxAge(5 * 24 * 60 * 60);
    }

    @Inject
    private ImageService imageService;
    @Context
    private Request request;

    @Override
    public Response getImage(ImageTypeDto type, String title) {
        ImageService.ImageInfo imageInfo = imageService.getImage(type, title);
        Timestamp lastModifiedLocal = Timestamp.valueOf(TimeUtils.mapToCurrentTimeZone(imageInfo.lastModified()));
        Response.ResponseBuilder response = request.evaluatePreconditions(lastModifiedLocal);
        if (response == null)
            response = Response.ok(imageInfo.image())
                    .type(imageInfo.mimeType())
                    .cacheControl(CACHE_CONTROL)
                    .lastModified(lastModifiedLocal);
        return response.build();
    }

    @Override
    public Response setImage(ImageTypeDto type, String title, ImageDto imageDto) {
        imageService.setImage(type, title, imageDto);
        return Response.noContent().build();
    }

    @Override
    public Response deleteImage(ImageTypeDto type, String title) {
        imageService.deleteImage(type, title);
        return Response.noContent().build();
    }
}
