package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.transactions.TransactionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UserPhonesRepository {

    @Inject
    private TransactionManager transactionManager;

    public List<String> getUserPhones(int userId) {
        String query = "SELECT phone FROM user_phones WHERE user_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> emails = new ArrayList<>();
                while (rs.next())
                    emails.add(rs.getString("phone"));
                return emails;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void clearUserPhones(int userId) {
        String query = "DELETE FROM user_phones WHERE user_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void setUserPhones(int userId, List<String> phones) {
        String query = "INSERT INTO user_phones (user_id, phone) VALUES (?, ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            for (String phone : phones) {
                stmt.setString(2, phone);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public boolean existsAnotherUserWithPhone(int userId, List<String> phones) {
        String query = "SELECT EXISTS (SELECT * FROM user_phones WHERE user_id <> ? AND phone = ANY (?))";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setArray(2, transactionManager.currentTransaction().createArrayOf(phones, "varchar"));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }
}
