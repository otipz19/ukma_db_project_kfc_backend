package ua.edu.ukma.db.kfc.transactions.core;

import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.transactions.TransactionIsolation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

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
        try {
            return connection.prepareStatement(sql);
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
