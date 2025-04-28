package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.ClientMealEntity;

import java.sql.*;
import java.util.*;

import static java.sql.Types.INTEGER;

@ApplicationScoped
public class ClientMealRepository extends BaseRepository<ClientMealEntity, Integer> {

    public List<ClientMealEntity> findAll(Integer orderId) {
        String sql = "SELECT * FROM client_meals WHERE ? IS NULL OR order_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            if (orderId != null) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, orderId);
            } else {
                stmt.setNull(1, INTEGER);
                stmt.setNull(2, INTEGER);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<ClientMealEntity> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Optional<ClientMealEntity> findById(Integer id) {
        String sql = "SELECT * FROM client_meals WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Integer save(ClientMealEntity entity) {
        List<Integer> id = saveAll(List.of(entity));
        if (id.isEmpty())
            throw new DataBaseException("Failed to save client meal");
        return id.getFirst();
    }

    public List<Integer> saveAll(Collection<ClientMealEntity> entities) {
        String sql = """
            INSERT INTO client_meals (energetic_value, price, weight, meal_id, order_id, amount_in_order)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql, true)) {
            for (ClientMealEntity e : entities) {
                stmt.setInt(1, e.getEnergeticValue());
                stmt.setBigDecimal(2, e.getPrice());
                stmt.setInt(3, e.getWeight());
                stmt.setInt(4, e.getMealId());
                stmt.setInt(5, e.getOrderId());
                stmt.setInt(6, e.getAmountInOrder());
                stmt.addBatch();
            }
            stmt.executeBatch();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                List<Integer> ids = new ArrayList<>();
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
                return ids;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private ClientMealEntity map(ResultSet rs) throws SQLException {
        return new ClientMealEntity(
                rs.getInt("id"),
                rs.getInt("energetic_value"),
                rs.getInt("weight"),
                rs.getBigDecimal("price"),
                rs.getInt("order_id"),
                rs.getInt("meal_id"),
                rs.getInt("amount_in_order")
        );
    }
}
