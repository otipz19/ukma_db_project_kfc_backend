package ua.edu.ukma.db.kfc.transactions.core;

import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.transactions.TransactionIsolation;

import java.sql.*;
import java.util.Collection;

public class DefaultTransaction implements Transaction {

    Connection connection;

    DefaultTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Statement createStatement() {
        try {
            return connection.createStatement();
        }
        catch (NullPointerException e) {
            throw new DataBaseException("Transaction is closed");
        }
        catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) {
        return prepareStatement(sql, false);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, boolean returnGeneratedKeys) {
        try {
            return connection.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        }
        catch (NullPointerException e) {
            throw new DataBaseException("Transaction is closed");
        }
        catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public CallableStatement prepareCall(String sql) {
        try {
            return connection.prepareCall(sql);
        }
        catch (NullPointerException e) {
            throw new DataBaseException("Transaction is closed");
        }
        catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Array createArrayOf(Collection<?> elements, String typeName) {
        try {
            return connection.createArrayOf(typeName, elements.toArray());
        }
        catch (NullPointerException e) {
            throw new DataBaseException("Transaction is closed");
        }
        catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public <T> Array createArrayOf(Collection<T> elements, Class<T> type) {
        return createArrayOf(elements, type.getSimpleName());
    }

    @Override
    public boolean isReadOnly() {
        try {
            return connection.isReadOnly();
        }
        catch (NullPointerException e) {
            throw new DataBaseException("Transaction is closed");
        }
        catch (Exception e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public TransactionIsolation getIsolation() {
        try {
            return TransactionIsolation.valueOf(connection.getTransactionIsolation());
        }
        catch (NullPointerException e) {
            throw new DataBaseException("Transaction is closed");
        }
        catch (Exception e) {
            throw new DataBaseException(e);
        }
    }
}
