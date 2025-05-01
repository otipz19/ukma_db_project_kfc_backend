package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.filters.IngredientsFilter;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;

import java.sql.*;
import java.util.*;

@ApplicationScoped
public class IngredientRepository extends BaseRepository<IngredientEntity, Integer> {

    public List<IngredientEntity> findByFilter(IngredientsFilter filter) {
        String query = "SELECT * FROM ingredients";
        query = filter.addFilteringAndPagination(query, Map.of(
                "id", "id",
                "title", "title",
                "energeticValue", "energetic_value",
                "weight", "weight",
                "price", "price",
                "isActual", "is_actual"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setWhereClauseParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<IngredientEntity> result = new ArrayList<>();
                while (rs.next())
                    result.add(map(rs));
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countByFilter(IngredientsFilter filter) {
        String query = "SELECT COUNT(*) FROM ingredients";
        query = filter.addFiltering(query, Map.of(
                "id", "id",
                "title", "title",
                "energeticValue", "energetic_value",
                "weight", "weight",
                "price", "price",
                "isActual", "is_actual"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setWhereClauseParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return 0;
    }

    @Override
    public Optional<IngredientEntity> findById(Integer id) {
        return findById(id, true);
    }

    public Optional<IngredientEntity> findById(Integer id, boolean requireActual) {
        String query = "SELECT * FROM ingredients WHERE id = ?" + (requireActual ? " AND is_actual = true" : "");
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

    public List<IngredientEntity> findByIds(Collection<Integer> ids) {
        String sql = "SELECT * FROM ingredients WHERE id = ANY (?) AND is_actual = true";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
            try (ResultSet rs = stmt.executeQuery()) {
                List<IngredientEntity> result = new ArrayList<>();
                while (rs.next())
                    result.add(map(rs));
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public Optional<IngredientEntity> findByTitle(String title) {
        String sql = "SELECT * FROM ingredients WHERE title = ? AND is_actual = true";
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

    @Override
    public Integer save(IngredientEntity entity) {
        String sql = """
        INSERT INTO ingredients (title, energetic_value, weight, price)
        VALUES (?, ?, ?, ?)
        RETURNING id
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setInt(2, entity.getEnergeticValue());
            stmt.setInt(3, entity.getWeight());
            stmt.setBigDecimal(4, entity.getPrice());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                throw new DataBaseException("Failed to save ingredient");
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void delete(int id) {
        String sql = "UPDATE ingredients SET is_actual = false WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public boolean checkAllAreActualByIds(Collection<Integer> ids) {
        String sql = """
                SELECT NOT EXISTS (
                    SELECT *
                    FROM ingredients
                    WHERE id = ANY (?) AND is_actual = false
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void clearNotActual() {
        String query = """
                DELETE FROM ingredients
                WHERE is_actual = false AND id NOT IN (
                    SELECT ingredient_id
                    FROM meals_ingredients
                ) AND id NOT IN (
                    SELECT ingredient_id
                    FROM client_meals_ingredients
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
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