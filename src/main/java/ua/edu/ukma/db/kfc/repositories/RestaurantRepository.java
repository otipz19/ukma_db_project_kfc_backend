package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RestaurantRepository extends BaseRepository<RestaurantEntity, Integer> {

    @Override
    public Optional<RestaurantEntity> findById(Integer id) {
        String query = """
                SELECT *
                FROM restaurants
                WHERE id = ? AND is_deleted = false
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

    public List<RestaurantEntity> findAll() {
        String query = """
                SELECT *
                FROM restaurants
                WHERE is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            List<RestaurantEntity> restaurants = new ArrayList<>();
            while (rs.next())
                restaurants.add(map(rs));
            return restaurants;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public Optional<Integer> findIdByAddress(String address) {
        String query = """
                SELECT id
                FROM restaurants
                WHERE address = ? AND is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, address);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Integer save(RestaurantEntity entity) {
        String query = "INSERT INTO restaurants (address) VALUES (?) RETURNING id";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, entity.getAddress());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new DataBaseException("Failed to save restaurant");
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void update(RestaurantEntity entity) {
        String query = """
                UPDATE restaurants
                SET address = ?
                WHERE id = ? AND is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, entity.getAddress());
            stmt.setInt(2, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void delete(int id) {
        String query = """
                UPDATE restaurants
                SET is_deleted = true
                WHERE id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private RestaurantEntity map(ResultSet rs) throws SQLException {
        return new RestaurantEntity(
                rs.getInt("id"),
                rs.getString("address"),
                rs.getBoolean("is_deleted")
        );
    }
}
