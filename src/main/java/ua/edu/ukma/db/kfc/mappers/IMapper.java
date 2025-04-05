package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

public interface IMapper<E, R, D> {

    R toResponse(E entity);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<R> toResponse(List<E> entity);

    @Mapping(target = "id", ignore = true)
    void toEntity(D dto, @MappingTarget E entity);
}
