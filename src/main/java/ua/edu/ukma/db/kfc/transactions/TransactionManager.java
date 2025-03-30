package ua.edu.ukma.db.kfc.transactions;

public interface TransactionManager {

    void beginTransaction(boolean readOnly, TransactionIsolation isolation);

    boolean isTransactionActive();

    Transaction currentTransaction();

    void commitCurrent();

    void rollbackCurrent();
}
