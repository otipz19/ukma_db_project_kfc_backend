package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.EmployeeStatsEntity;
import ua.edu.ukma.db.kfc.model.entities.NamedEntity;
import ua.edu.ukma.db.kfc.model.entities.RestaurantStatsEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@ApplicationScoped
public class StatisticsRepository extends BaseRepository<NamedEntity, Integer> {

    // 1) Top 10 popular meals (ordered at least average times)
    public List<NamedEntity> getMealPopular() {
        String sql = """
                    SELECT
                      m.id,
                      m.title AS name
                    FROM meals m
                    JOIN client_meals cm ON m.id = cm.meal_id
                    JOIN orders o ON cm.order_id = o.id AND o.is_completed = TRUE
                    GROUP BY m.id, m.title
                    ORDER BY COUNT(cm.id) DESC
                    LIMIT 10
        """;
        return executeQuery(sql);
    }

    public List<NamedEntity> getMealLeastPopular() {
        String sql = """
                            SELECT
                              m.id,
                              m.title AS name
                            FROM meals m
                            JOIN client_meals cm ON m.id = cm.meal_id
                            JOIN orders o ON cm.order_id = o.id AND o.is_completed = TRUE
                            GROUP BY m.id, m.title
                            ORDER BY COUNT(cm.id) ASC
                            LIMIT 10
                """;
        return executeQuery(sql);
    }

    // 2) Top managers in last quarter
    public List<NamedEntity> getTopManagersLastQuarter() {
        String sql = """
        SELECT
            e.id,
            e.surname || ' ' || e.first_name AS name,
            SUM(cm.price * cm.amount_in_order) AS total_order_value
        FROM employees e
        JOIN users u ON e.user_id = u.id
        JOIN orders o ON e.restaurant_id = o.restaurant_id
        JOIN client_meals cm ON o.id = cm.order_id
        WHERE u.role = 'Manager'
          AND o.is_completed = TRUE
          AND o.date_created >= DATE_TRUNC('quarter', CURRENT_DATE) - INTERVAL '3 months'
          AND o.date_created < DATE_TRUNC('quarter', CURRENT_DATE)
        GROUP BY e.id, e.surname, e.first_name
        ORDER BY total_order_value DESC
        LIMIT 5
    """;
        return executeQuery(sql);
    }

    // 3) Client-specific meal average stats
    public List<RestaurantStatsEntity> getClientMealAveragesByRestaurant(int clientId) {
        String sql = """
            SELECT
              r.id AS restaurant_id,
              AVG(cm.price) AS avg_price,
              AVG(cm.weight) AS avg_weight,
              AVG(cm.energetic_value) AS avg_energetic_value
            FROM client_meals cm
            JOIN orders o ON cm.order_id = o.id
            JOIN restaurants r ON o.restaurant_id = r.id
            WHERE o.client_id = ?
            GROUP BY r.id
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<RestaurantStatsEntity> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new RestaurantStatsEntity(
                            rs.getInt("restaurant_id"),
                            rs.getDouble("avg_price"),
                            rs.getDouble("avg_weight"),
                            rs.getDouble("avg_energetic_value")
                    ));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    // 4) Employee-specific meal average stats
    public List<EmployeeStatsEntity> getEmployeeMealAveragesByRestaurant(int restaurantId) {
        String sql = """
            SELECT
              e.id AS employee_id,
              AVG(cm.price) AS avg_price,
              AVG(cm.weight) AS avg_weight,
              AVG(cm.energetic_value) AS avg_energetic_value
            FROM employees e
            JOIN orders o ON e.id = o.employee_id
            JOIN client_meals cm ON cm.order_id = o.id
            WHERE o.restaurant_id = ?
            GROUP BY e.id
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<EmployeeStatsEntity> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new EmployeeStatsEntity(
                            rs.getInt("employee_id"),
                            rs.getDouble("avg_price"),
                            rs.getDouble("avg_weight"),
                            rs.getDouble("avg_energetic_value")
                    ));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    // 5) Ingredients only used in ordered meals
    public List<NamedEntity> getIngredientsOnlyInOrderedMeals() {
        String sql = """
            SELECT i.id, i.title AS name
            FROM ingredients i
            WHERE NOT EXISTS (
              SELECT *
              FROM meal_ingredients mi
              WHERE mi.ingredient_id = i.id
                AND NOT EXISTS (
                  SELECT *
                  FROM client_meals cm
                  JOIN orders o ON cm.order_id = o.id
                  WHERE cm.meal_id = mi.meal_id
                    AND o.is_completed = TRUE
                )
            )
        """;
        return executeQuery(sql);
    }

    // 6) Meals never ordered by bonus-holding clients
    public List<NamedEntity> getMealsNotOrderedByBonusClients() {
        String sql = """
            SELECT m.id, m.title AS name
            FROM meals m
            WHERE NOT EXISTS (
              SELECT *
              FROM clients c
              WHERE c.bonuses > 0
                AND NOT EXISTS (
                  SELECT *
                  FROM orders o
                  JOIN client_meals cm ON cm.order_id = o.id
                  WHERE o.client_id = c.id
                    AND cm.meal_id = m.id
                )
            )
        """;
        return executeQuery(sql);
    }

    // 7) Cashiers who always sell at least one high-energy meal
    public List<NamedEntity> getCashiersAlwaysHighEnergy(int energyThreshold) {
        String sql = """
                    SELECT e.id, e.surname || ' ' || e.first_name AS name
                    FROM employees e
                    JOIN users u ON e.user_id = u.id
                    WHERE u.role = 'Cashier'
                      AND NOT EXISTS (
                        SELECT *
                        FROM orders o
                        WHERE o.employee_id = e.id
                          AND o.is_completed = TRUE
                          AND NOT EXISTS (
                            SELECT *
                            FROM client_meals cm
                            JOIN meals m ON cm.meal_id = m.id
                            WHERE cm.order_id = o.id
                              AND m.energetic_value > ?
                          )
                      )
        """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setInt(1, energyThreshold);
            return executeQuery(stmt);
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private List<NamedEntity> executeQuery(String sql) {
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<NamedEntity> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private List<NamedEntity> executeQuery(PreparedStatement stmt) {
        try (ResultSet rs = stmt.executeQuery()) {
            List<NamedEntity> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private NamedEntity map(ResultSet rs) throws SQLException {
        NamedEntity entity = new NamedEntity();
        entity.setId(rs.getInt("id"));
        entity.setName(rs.getString("name"));
        return entity;
    }

    @Override
    public Optional<NamedEntity> findById(Integer id) {
        throw new UnsupportedOperationException("Statistics are not fetched by ID.");
    }

    @Override
    public Integer save(NamedEntity entity) {
        throw new UnsupportedOperationException("Statistics cannot be saved.");
    }
}