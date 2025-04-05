package ua.edu.ukma.db.kfc.transactions;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public interface Transaction {

    Statement createStatement();

    PreparedStatement prepareStatement(String sql);

    CallableStatement prepareCall(String sql);

    Array createArrayOf(List<?> elements, String typeName);

    <T> Array createArrayOf(List<T> elements, Class<T> type);

    boolean isReadOnly();

    TransactionIsolation getIsolation();
}
