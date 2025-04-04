package ua.edu.ukma.db.kfc.validators;

import java.util.List;

public interface IValidator<E> {

    void validForView(E E);

    void validForView(List<E> entities);

    void validForCreate(E E);

    void validForUpdate(E E);

    void validForDelete(E E);

}
