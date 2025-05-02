package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.filters.ClientsFilter;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ClientRepository extends BaseRepository<ClientEntity, Integer> {

    @Override
    public Optional<ClientEntity> findById(Integer id) {
        return findByUserId(id);
    }

    public Optional<ClientEntity> findByUserId(int userId) {
        String query = """
                SELECT user_id, username, surname, first_name, middle_name, bonuses, birth_date
                FROM clients JOIN users ON clients.user_id = users.id
                WHERE user_id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public Optional<ClientEntity> findByUsername(String username) {
        String query = """
                SELECT user_id, username, surname, first_name, middle_name, bonuses, birth_date
                FROM clients JOIN users ON clients.user_id = users.id
                WHERE username = ?
                """;
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

    public List<ClientEntity> findByFilter(ClientsFilter filter) {
        String query = """
                SELECT DISTINCT clients.user_id, username, surname, first_name, middle_name, bonuses, birth_date
                FROM clients
                    JOIN users ON clients.user_id = users.id
                    LEFT JOIN user_emails ON clients.user_id = user_emails.user_id
                    LEFT JOIN user_phones ON clients.user_id = user_phones.user_id
                """;
        query =  filter.addFilteringAndPagination(query, Map.of(
                "id", "clients.user_id",
                "userId", "clients.user_id",
                "username", "username",
                "surname", "surname",
                "firstName", "first_name",
                "middleName", "middle_name",
                "phone", "phone",
                "email", "email",
                "bonuses", "bonuses",
                "birthDate", "birth_date"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<ClientEntity> clients = new ArrayList<>();
                while (rs.next())
                    clients.add(map(rs));
                return clients;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countByFilter(ClientsFilter filter) {
        String query = """
                SELECT COUNT (DISTINCT clients.user_id)
                FROM clients
                    JOIN users ON clients.user_id = users.id
                    LEFT JOIN user_emails ON clients.user_id = user_emails.user_id
                    LEFT JOIN user_phones ON clients.user_id = user_phones.user_id
                """;
        query =  filter.addFiltering(query, Map.of(
                        "username", "username",
                        "surname", "surname",
                        "firstName", "first_name",
                        "middleName", "middle_name",
                        "phone", "phone",
                        "email", "email",
                        "bonuses", "bonuses",
                        "birthDate", "birth_date"
                )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return 0;
    }

    public boolean existsByUserId(int userId) {
        String query = "SELECT EXISTS (SELECT * FROM clients WHERE user_id = ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Integer save(ClientEntity entity) {
        String query = "INSERT INTO clients (user_id, surname, first_name, middle_name, bonuses, birth_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, entity.getUserId());
            stmt.setString(2, entity.getSurname());
            stmt.setString(3, entity.getFirstName());
            stmt.setString(4, entity.getMiddleName());
            stmt.setInt(5, entity.getBonuses());
            stmt.setDate(6, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            stmt.executeUpdate();
            return entity.getUserId();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void update(ClientEntity entity) {
        String query = """
                UPDATE clients
                SET surname = ?, first_name = ?, middle_name = ?, bonuses = ?, birth_date = ?
                WHERE user_id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, entity.getSurname());
            stmt.setString(2, entity.getFirstName());
            stmt.setString(3, entity.getMiddleName());
            stmt.setInt(4, entity.getBonuses());
            stmt.setDate(5, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            stmt.setInt(6, entity.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private ClientEntity map(ResultSet rs) throws SQLException {
        return new ClientEntity(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("surname"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getInt("bonuses"),
                TimeUtils.mapToLocalDate(rs.getDate("birth_date"))
        );
    }
}
