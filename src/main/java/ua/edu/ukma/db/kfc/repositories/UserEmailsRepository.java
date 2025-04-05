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
public class UserEmailsRepository {

    @Inject
    private TransactionManager transactionManager;

    public List<String> getUserEmails(int userId) {
        String query = "SELECT email FROM user_emails WHERE user_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> emails = new ArrayList<>();
                while (rs.next())
                    emails.add(rs.getString("email"));
                return emails;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void clearUserEmails(int userId) {
        String query = "DELETE FROM user_emails WHERE user_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void setUserEmails(int userId, List<String> emails) {
        String query = "INSERT INTO user_emails (user_id, email) VALUES (?, ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            for (String email : emails) {
                stmt.setString(2, email);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public boolean existsAnotherUserWithEmail(int userId, List<String> email) {
        String query = "SELECT EXISTS (SELECT * FROM user_emails WHERE user_id <> ? AND email = ANY (?))";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setArray(2, transactionManager.currentTransaction().createArrayOf(email, "varchar"));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }
}
