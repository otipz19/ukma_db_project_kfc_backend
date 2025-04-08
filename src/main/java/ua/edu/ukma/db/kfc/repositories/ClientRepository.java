package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClientRepository extends BaseRepository<ClientEntity, Integer> {

    @Override
    public Optional<ClientEntity> findById(Integer id) {
        String query = """
                SELECT clients.id AS id, user_id, username, surname, first_name, middle_name, bonuses, birth_date, is_deleted
                FROM clients LEFT JOIN users ON clients.user_id = users.id
                WHERE clients.id = ? AND is_deleted = false
                """;
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

    public Optional<ClientEntity> findByUserId(int user_id) {
        String query = """
                SELECT clients.id AS id, user_id, username, surname, first_name, middle_name, bonuses, birth_date, is_deleted
                FROM clients LEFT JOIN users ON clients.user_id = users.id
                WHERE user_id = ? AND is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, user_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public List<ClientEntity> findAll() {
        String query = """
                SELECT clients.id AS id, user_id, username, surname, first_name, middle_name, bonuses, birth_date, is_deleted
                FROM clients LEFT JOIN users ON clients.user_id = users.id
                WHERE is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            List<ClientEntity> clients = new ArrayList<>();
            while (rs.next())
                clients.add(map(rs));
            return clients;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Integer save(ClientEntity entity) {
        String query = "INSERT INTO clients (user_id, surname, first_name, middle_name, bonuses, birth_date) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, entity.getUserId());
            stmt.setString(2, entity.getSurname());
            stmt.setString(3, entity.getFirstName());
            stmt.setString(4, entity.getMiddleName());
            stmt.setInt(5, entity.getBonuses());
            stmt.setDate(6, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new DataBaseException("Failed to save client");
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void update(ClientEntity entity) {
        String query = """
                UPDATE clients
                SET surname = ?, first_name = ?, middle_name = ?, bonuses = ?, birth_date = ?
                WHERE id = ? AND is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, entity.getSurname());
            stmt.setString(2, entity.getFirstName());
            stmt.setString(3, entity.getMiddleName());
            stmt.setInt(4, entity.getBonuses());
            stmt.setDate(5, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            stmt.setInt(6, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void deleteByUserId(int userId) {
        String query = """
                UPDATE clients
                SET is_deleted = true, user_id = null
                WHERE user_id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private ClientEntity map(ResultSet rs) throws SQLException {
        return new ClientEntity(
                rs.getInt("id"),
                rs.getObject("user_id", Integer.class),
                rs.getString("username"),
                rs.getString("surname"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getInt("bonuses"),
                TimeUtils.mapToLocalDate(rs.getDate("birth_date")),
                rs.getBoolean("is_deleted")
        );
    }
}
