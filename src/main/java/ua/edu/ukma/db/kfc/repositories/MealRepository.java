package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;

import java.sql.*;
import java.util.*;

@ApplicationScoped
public class MealRepository extends BaseRepository<MealEntity, Integer> {

    public List<MealEntity> findAll() {
        String sql = "SELECT * FROM meals WHERE is_actual = true";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<MealEntity> result = new ArrayList<>();
            while (rs.next())
                result.add(map(rs));
            return result;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Optional<MealEntity> findById(Integer id) {
        String sql = "SELECT * FROM meals WHERE id = ? AND is_actual = true";
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

    public Optional<MealEntity> findByTitle(String title) {
        String sql = "SELECT * FROM meals WHERE title = ? AND is_actual = true";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public List<MealEntity> findByIngredientId(int ingredientId) {
        String sql = """
        SELECT *
        FROM meals
        WHERE is_actual = true AND id IN (
            SELECT meal_id
            FROM meals_ingredients
            WHERE ingredient_id = ?
        )
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, ingredientId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<MealEntity> result = new ArrayList<>();
                while (rs.next())
                    result.add(map(rs));
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    public Integer save(MealEntity entity) {
        String sql = """
        INSERT INTO meals (title, additional_price, description, recipe, energetic_value, weight, price)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setBigDecimal(2, entity.getAdditionalPrice());
            stmt.setString(3, entity.getDescription());
            stmt.setString(4, entity.getRecipe());
            stmt.setInt(5, entity.getEnergeticValue());
            stmt.setInt(6, entity.getWeight());
            stmt.setBigDecimal(7, entity.getPrice());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                throw new DataBaseException("Failed to save meal");
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<Integer> saveAll(Collection<MealEntity> entities) {
        String sql = """
        INSERT INTO meals (title, additional_price, description, recipe, energetic_value, weight, price)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql, true)) {
            for (MealEntity entity : entities) {
                stmt.setString(1, entity.getTitle());
                stmt.setBigDecimal(2, entity.getAdditionalPrice());
                stmt.setString(3, entity.getDescription());
                stmt.setString(4, entity.getRecipe());
                stmt.setInt(5, entity.getEnergeticValue());
                stmt.setInt(6, entity.getWeight());
                stmt.setBigDecimal(7, entity.getPrice());
                stmt.addBatch();
            }
            stmt.executeBatch();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                List<Integer> ids = new ArrayList<>();
                while (rs.next())
                    ids.add(rs.getInt(1));
                return ids;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void delete(int id) {
        String sql = "UPDATE meals SET is_actual = false WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void deleteAll(Collection<Integer> ids) {
        String sql = "UPDATE meals SET is_actual = false WHERE id = ANY (?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private MealEntity map(ResultSet rs) throws SQLException {
        MealEntity entity = new MealEntity();
        entity.setId(rs.getInt("id"));
        entity.setTitle(rs.getString("title"));
        entity.setAdditionalPrice(rs.getBigDecimal("additional_price"));
        entity.setDescription(rs.getString("description"));
        entity.setRecipe(rs.getString("recipe"));
        entity.setEnergeticValue(rs.getInt("energetic_value"));
        entity.setWeight(rs.getInt("weight"));
        entity.setPrice(rs.getBigDecimal("price"));
        entity.setActual(rs.getBoolean("is_actual"));
        return entity;
    }

}