package ua.edu.ukma.db.kfc.repositories;

import lombok.RequiredArgsConstructor;
import ua.edu.ukma.db.kfc.transactions.TransactionManager;

@RequiredArgsConstructor(onConstructor_ = @jakarta.inject.Inject)
public abstract class BaseRepository {

    protected final TransactionManager transactionManager;

}
