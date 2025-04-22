package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.ClientMealEntity;

import java.sql.*;
import java.util.*;

@ApplicationScoped
public class ClientMealRepository extends BaseRepository<ClientMealEntity, Integer> {

    public List<ClientMealEntity> findAll() {
        String sql = "SELECT * FROM client_meal";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<ClientMealEntity> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Optional<ClientMealEntity> findById(Integer id) {
        String sql = "SELECT * FROM client_meal WHERE id = ?";
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

    public List<ClientMealEntity> findByOrderId(int orderId) {
        String sql = "SELECT * FROM client_meal WHERE order_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
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

    public List<ClientMealEntity> findByMealId(int mealId) {
        String sql = "SELECT * FROM client_meal WHERE meal_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, mealId);
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
    public Integer save(ClientMealEntity entity) {
        String sql = """
            INSERT INTO client_meal
              (energetic_value, price, weight, meal_id, order_id, amount_in_order)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction()
                .prepareStatement(sql)) {
            stmt.setInt(1, entity.getEnergeticValue());
            stmt.setBigDecimal(2, entity.getPrice());
            stmt.setInt(3, entity.getWeight());
            stmt.setInt(4, entity.getMealId());
            stmt.setInt(5, entity.getOrderId());
            stmt.setInt(6, entity.getAmountInOrder());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new DataBaseException("Failed to insert client_meal");
                }
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<Integer> saveAll(Collection<ClientMealEntity> entities) {
        String sql = """
            INSERT INTO client_meal
              (energetic_value, price, weight, meal_id, order_id, amount_in_order)
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

    public void delete(int id) {
        String sql = "DELETE FROM client_meal WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void deleteAll(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String sql = "DELETE FROM client_meal WHERE id = ANY(?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private ClientMealEntity map(ResultSet rs) throws SQLException {
        ClientMealEntity e = new ClientMealEntity();
        e.setId(rs.getInt("id"));
        e.setEnergeticValue(rs.getInt("energetic_value"));
        e.setPrice(rs.getBigDecimal("price"));
        e.setWeight(rs.getInt("weight"));
        e.setMealId(rs.getInt("meal_id"));
        e.setOrderId(rs.getInt("order_id"));
        e.setAmountInOrder(rs.getInt("amount_in_order"));
        return e;
    }
}
