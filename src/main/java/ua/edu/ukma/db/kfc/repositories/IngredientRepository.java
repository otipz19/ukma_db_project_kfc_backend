package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class IngredientRepository extends BaseRepository<IngredientEntity, Integer> {

    public List<IngredientEntity> findAll() {
        String sql = "SELECT * FROM ingredient WHERE is_actual = true";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<IngredientEntity> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Optional<IngredientEntity> findById(Integer id) {
        String sql = "SELECT * FROM ingredient WHERE id = ? AND is_actual = true";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public Optional<IngredientEntity> findByTitle(String title) {
        String sql = "SELECT * FROM ingredient WHERE title = ? AND is_actual = true";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setString(1, title);
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

    public Integer save(IngredientEntity entity) {
        String sql = """
            INSERT INTO ingredient (title, energetic_value, weight, price, is_actual)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setInt(2, entity.getEnergeticValue());
            stmt.setInt(3, entity.getWeight());
            stmt.setBigDecimal(4, entity.getPrice());
            stmt.setBoolean(5, true);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                throw new DataBaseException("Failed to save ingredient");
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void deactivate(int id) {
        String sql = "UPDATE ingredient SET is_actual = false WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM ingredient WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private IngredientEntity map(ResultSet rs) throws SQLException {
        IngredientEntity entity = new IngredientEntity();
        entity.setId(rs.getInt("id"));
        entity.setTitle(rs.getString("title"));
        entity.setEnergeticValue(rs.getInt("energetic_value"));
        entity.setWeight(rs.getInt("weight"));
        entity.setPrice(rs.getBigDecimal("price"));
        entity.setActual(rs.getBoolean("is_actual"));
        return entity;
    }
}