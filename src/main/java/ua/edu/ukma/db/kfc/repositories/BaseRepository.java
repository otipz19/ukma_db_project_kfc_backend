package ua.edu.ukma.db.kfc.repositories;

import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.transactions.TransactionManager;

import java.util.Optional;

public abstract class BaseRepository<E, I> {

    @Inject
    protected TransactionManager transactionManager;

    public abstract Optional<E> findById(I id);

    public abstract I save(E entity);
}
