package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MealIngredientRepository extends BaseRepository<MealIngredientEntity, String> {

    public List<MealIngredientEntity> findAll() {
        String sql = "SELECT * FROM meals_ingredients";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<MealIngredientEntity> result = new ArrayList<>();
            while (rs.next())
                result.add(map(rs));
            return result;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public Optional<MealIngredientEntity> findByMealIdAndIngredientId(int mealId, int ingredientId) {
        String sql = "SELECT * FROM meals_ingredients WHERE meal_id = ? AND ingredient_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, mealId);
            stmt.setInt(2, ingredientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<MealIngredientEntity> findById(String id) {
        return Optional.empty();
    }

    @Override
    public String save(MealIngredientEntity entity) {
        String sql = "INSERT INTO meals_ingredients (meal_id, ingredient_id, amount, is_fixated) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, entity.getMealId());
            stmt.setInt(2, entity.getIngredientId());
            stmt.setInt(3, entity.getAmount());
            stmt.setBoolean(4, entity.isFixated());
            stmt.executeUpdate();
            return entity.getMealId() + "-" + entity.getIngredientId();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void update(MealIngredientEntity entity) {
        String sql = "UPDATE meals_ingredients SET amount = ?, is_fixated = ? WHERE meal_id = ? AND ingredient_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, entity.getAmount());
            stmt.setBoolean(2, entity.isFixated());
            stmt.setInt(3, entity.getMealId());
            stmt.setInt(4, entity.getIngredientId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void delete(int mealId, int ingredientId) {
        String sql = "DELETE FROM meals_ingredients WHERE meal_id = ? AND ingredient_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, mealId);
            stmt.setInt(2, ingredientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private MealIngredientEntity map(ResultSet rs) throws SQLException {
        MealIngredientEntity entity = new MealIngredientEntity();
        entity.setMealId(rs.getInt("meal_id"));
        entity.setIngredientId(rs.getInt("ingredient_id"));
        entity.setAmount(rs.getInt("amount"));
        entity.setFixated(rs.getBoolean("is_fixated"));
        return entity;
    }

    public List<MealIngredientEntity> findByIngredientId(int id) {
        String sql = "SELECT * FROM meals_ingredients WHERE ingredient_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                List<MealIngredientEntity> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<MealIngredientEntity> findByMealId(int id) {
        String sql = "SELECT * FROM meals_ingredients WHERE meal_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                List<MealIngredientEntity> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }
}
