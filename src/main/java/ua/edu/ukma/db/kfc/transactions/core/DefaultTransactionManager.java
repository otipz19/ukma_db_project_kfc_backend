package ua.edu.ukma.db.kfc.transactions.core;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.transactions.TransactionIsolation;
import ua.edu.ukma.db.kfc.transactions.TransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultTransactionManager implements TransactionManager {

    private final DataSource dataSource;
    private final ThreadLocal<DefaultTransaction> currentTransaction = new ThreadLocal<>();

    @Override
    public void beginTransaction(boolean readOnly, TransactionIsolation isolation) {
        throwIfStarted();
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connection.setReadOnly(readOnly);
            connection.setTransactionIsolation(isolation.getValue());
            currentTransaction.set(new DefaultTransaction(connection));
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public boolean isTransactionActive() {
        return currentTransaction.get() != null;
    }

    @Override
    public Transaction currentTransaction() {
        throwIfNotStarted();
        return currentTransaction.get();
    }

    @Override
    public void commitCurrent() {
        throwIfNotStarted();
        try {
            DefaultTransaction transaction = currentTransaction.get();
            transaction.connection.commit();
            transaction.connection.close();
            transaction.connection = null;
            currentTransaction.remove();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public void rollbackCurrent() {
        throwIfNotStarted();
        try {
            DefaultTransaction transaction = currentTransaction.get();
            transaction.connection.rollback();
            transaction.connection.close();
            transaction.connection = null;
            currentTransaction.remove();
        } catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    private void throwIfStarted() {
        if (isTransactionActive())
            throw new DataBaseException("Transaction is already started");
    }

    private void throwIfNotStarted() {
        if (!isTransactionActive())
            throw new DataBaseException("Transaction is not started");
    }
}
