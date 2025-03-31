package ua.edu.ukma.db.kfc.transactions;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

public interface Transaction {

    Statement createStatement();

    PreparedStatement prepareStatement(String sql);

    CallableStatement prepareCall(String sql);

    boolean isReadOnly();

    TransactionIsolation getIsolation();
}
