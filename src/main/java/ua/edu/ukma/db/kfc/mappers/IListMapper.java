package ua.edu.ukma.db.kfc.mappers;

import java.util.List;

public interface IListMapper<E, R, L, D> extends IMapper<E, R, D> {

    L toResponse(List<E> items, long total);
}
