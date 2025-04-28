package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.model.helper.MealIngredientPK;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MealIngredientRepository extends BaseRepository<MealIngredientEntity, MealIngredientPK> {

    @Override
    public Optional<MealIngredientEntity> findById(MealIngredientPK id) {
        String sql = "SELECT * FROM meals_ingredients WHERE meal_id = ? AND ingredient_id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id.mealId());
            stmt.setInt(2, id.ingredientId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public MealIngredientPK save(MealIngredientEntity entity) {
        saveAll(List.of(entity));
        return new MealIngredientPK(entity.getMealId(), entity.getIngredientId());
    }

    public void saveAll(Collection<MealIngredientEntity> entities) {
        String sql = "INSERT INTO meals_ingredients (meal_id, ingredient_id, amount, is_fixated) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            for (MealIngredientEntity entity : entities) {
                stmt.setInt(1, entity.getMealId());
                stmt.setInt(2, entity.getIngredientId());
                stmt.setInt(3, entity.getAmount());
                stmt.setBoolean(4, entity.isFixated());
                stmt.addBatch();
            }
            stmt.executeBatch();
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

    public List<MealIngredientEntity> findByMealIds(Collection<Integer> ids) {
        String sql = "SELECT * FROM meals_ingredients WHERE meal_id = ANY (?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
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

    public boolean existsActualMealByIngredientId(int ingredientId) {
        String sql = """
                SELECT EXISTS (
                    SELECT *
                    FROM meals_ingredients
                    WHERE ingredient_id = ? AND meal_id IN (SELECT id FROM meals WHERE is_actual = true)
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, ingredientId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private MealIngredientEntity map(ResultSet rs) throws SQLException {
        return new MealIngredientEntity(
                rs.getInt("meal_id"),
                rs.getInt("ingredient_id"),
                rs.getInt("amount"),
                rs.getBoolean("is_fixated")
        );
    }
}
