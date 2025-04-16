package ua.edu.ukma.db.kfc.transactions;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

public interface Transaction {

    Statement createStatement();

    PreparedStatement prepareStatement(String sql);

    PreparedStatement prepareStatement(String sql, boolean returnGeneratedKeys);

    CallableStatement prepareCall(String sql);

    Array createArrayOf(Collection<?> elements, String typeName);

    <T> Array createArrayOf(Collection<T> elements, Class<T> type);

    boolean isReadOnly();

    TransactionIsolation getIsolation();
}
