package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.filters.MealsFilter;
import ua.edu.ukma.db.kfc.filters.MealsStatisticFilter;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.helper.MealStatistic;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.*;
import java.util.*;

@ApplicationScoped
public class MealRepository extends BaseRepository<MealEntity, Integer> {

    public List<MealEntity> findByFilter(MealsFilter filter) {
        String query = "SELECT * FROM meals";
        query = filter.addFilteringAndPagination(query, Map.of(
                "id", "id",
                "title", "title",
                "description", "description",
                "recipe", "recipe",
                "energeticValue", "energetic_value",
                "weight", "weight",
                "price", "price",
                "additionalPrice", "additional_price",
                "isActual", "is_actual"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setWhereClauseParameters(stmt, transactionManager.currentTransaction());
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

    public long countByFilter(MealsFilter filter) {
        String query = "SELECT COUNT(*) FROM meals";
        query = filter.addFiltering(query, Map.of(
                "id", "id",
                "title", "title",
                "description", "description",
                "recipe", "recipe",
                "energeticValue", "energetic_value",
                "weight", "weight",
                "price", "price",
                "additionalPrice", "additional_price",
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
    public Optional<MealEntity> findById(Integer id) {
        return findById(id, true);
    }

    public Optional<MealEntity> findById(Integer id, boolean requireActual) {
        String sql = "SELECT * FROM meals WHERE id = ?" + (requireActual ? " AND is_actual = true" : "");
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

    public boolean existsByTitle(String title) {
        String sql = "SELECT EXISTS (SELECT * FROM meals WHERE title = ? AND is_actual = true)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
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

    public List<MealEntity> findByIds(Collection<Integer> ids) {
        String sql = "SELECT * FROM meals WHERE id = ANY (?) AND is_actual = true";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
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
        List<Integer> id = saveAll(List.of(entity));
        if (id.isEmpty())
            throw new DataBaseException("Failed to save meal");
        return id.getFirst();
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

    public void clearNotActual() {
        String query = """
                DELETE FROM meals
                WHERE is_actual = false AND id NOT IN (
                    SELECT meal_id
                    FROM client_meals
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<MealStatistic> findStatisticByFilter(MealsStatisticFilter filter) {
        String query = """
               SELECT m.id, m.title, m.is_actual,
                      COUNT(cm.id) AS client_meals_count,
                      MAX(o.date_created) AS last_ordered_date
               FROM meals m
                    LEFT JOIN client_meals cm ON m.id = cm.meal_id
                    LEFT JOIN orders o ON cm.order_id = o.id
               GROUP BY m.id, m.title, m.is_actual
               """;
        query = filter.addFilteringAndPagination(query, Map.of(
                "id", "m.id",
                "title", "m.title",
                "clientMealsCount", "COUNT(cm.id)",
                "lastOrderedDate", "MAX(o.date_created)",
                "isActual", "m.is_actual"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setWhereClauseParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<MealStatistic> result = new ArrayList<>();
                while (rs.next())
                    result.add(mapStatistic(rs));
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countStatisticByFilter(MealsStatisticFilter filter) {
        String query = """
               SELECT 1
               FROM meals m
                    LEFT JOIN client_meals cm ON m.id = cm.meal_id
                    LEFT JOIN orders o ON cm.order_id = o.id
               GROUP BY m.id, m.title, m.is_actual
               """;
        query = filter.addFiltering(query, Map.of(
                "id", "m.id",
                "title", "m.title",
                "clientMealsCount", "COUNT(cm.id)",
                "lastOrderedDate", "MAX(o.date_created)",
                "isActual", "m.is_actual"
            )
        );
        query = "SELECT COUNT(*) FROM (" + query + ")";
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

    private MealStatistic mapStatistic(ResultSet rs) throws SQLException {
        return new MealStatistic(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("client_meals_count"),
                TimeUtils.mapToLocalDateTime(rs.getTimestamp("last_ordered_date")),
                rs.getBoolean("is_actual")
        );
    }
}