package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;

import java.sql.*;
import java.util.Optional;

@ApplicationScoped
public class UserRepository extends BaseRepository<UserEntity, Integer> {

    @Inject
    private EnumsMapper enumsMapper;

    @Override
    public Optional<UserEntity> findById(Integer id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        final String query = "SELECT exists(SELECT * FROM users WHERE username = ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public Optional<UserEntity> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Integer save(UserEntity user) {
        String query = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, enumsMapper.mapToSting(user.getRole()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new DataBaseException("Failed to save user");
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void disableUser(int id) {
        String query = "UPDATE users SET is_active = false WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private UserEntity map(ResultSet result) throws SQLException {
        return new UserEntity(
                result.getInt("id"),
                result.getString("username"),
                result.getString("password_hash"),
                enumsMapper.mapToRole(result.getString("role")),
                result.getBoolean("is_active")
        );
    }
}
