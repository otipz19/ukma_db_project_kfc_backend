package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.MappingTarget;

public interface IMapper<E, D> {

    D toDto(E entity);

    void toEntity(D dto, @MappingTarget E entity);
}
