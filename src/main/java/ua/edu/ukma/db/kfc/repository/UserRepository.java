package ua.edu.ukma.db.kfc.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.entity.AppUser;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UserRepository {

    @Inject
    private DataSource dataSource;

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM appuser WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public void saveUser(AppUser user) {
        String query = "INSERT INTO appuser (email, password_hash) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public AppUser findByEmail(String email) {
        String query = "SELECT id, email, password_hash FROM appuser WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AppUser(rs.getInt("id"), rs.getString("email"), rs.getString("password_hash"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    public AppUser findById(Long id) {
        String query = "SELECT id, email, password_hash FROM appuser WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AppUser(rs.getInt("id"), rs.getString("email"), rs.getString("password_hash"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    public List<AppUser> findAll() {
        String query = "SELECT id, email, password_hash FROM appuser";
        List<AppUser> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new AppUser(rs.getInt("id"), rs.getString("email"), rs.getString("password_hash")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    public void updateUser(AppUser user) {
        String query = "UPDATE appuser SET email = ?, password_hash = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setLong(3, user.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("User not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public void deleteUser(Long id) {
        String query = "DELETE FROM appuser WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new IllegalArgumentException("User not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
