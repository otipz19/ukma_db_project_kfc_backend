package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.filters.RestaurantsFilter;
import ua.edu.ukma.db.kfc.filters.RestaurantsStatisticFilter;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;
import ua.edu.ukma.db.kfc.model.helper.RestaurantStatistic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class RestaurantRepository extends BaseRepository<RestaurantEntity, Integer> {

    @Override
    public Optional<RestaurantEntity> findById(Integer id) {
        return findById(id, true);
    }

    public Optional<RestaurantEntity> findById(Integer id, boolean requireNotDeleted) {
        String query = "SELECT * FROM restaurants WHERE id = ?" + (requireNotDeleted ? " AND is_deleted = false" : "");
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

    public List<RestaurantEntity> findByFilter(RestaurantsFilter filter) {
        String query = "SELECT * FROM restaurants";
        query = filter.addFilteringAndPagination(query, Map.of(
                "id", "id",
                "address", "address",
                "isDeleted", "is_deleted"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<RestaurantEntity> restaurants = new ArrayList<>();
                while (rs.next())
                    restaurants.add(map(rs));
                return restaurants;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countByFilter(RestaurantsFilter filter) {
        String query = "SELECT COUNT(*) FROM restaurants";
        query = filter.addFiltering(query, Map.of(
                "id", "id",
                "address", "address",
                "isDeleted", "is_deleted"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return 0;
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

    public boolean existsById(int id) {
        String query = "SELECT EXISTS (SELECT * FROM restaurants WHERE id = ? AND is_deleted = false)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
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

    public boolean hasEmployees(int id) {
        String query = """
                SELECT EXISTS (
                    SELECT *
                    FROM employees
                    WHERE restaurant_id = ?
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public boolean hasManager(int id) {
        String query = """
                SELECT EXISTS (
                    SELECT *
                    FROM employees JOIN users ON employees.user_id = users.id
                    WHERE restaurant_id = ? AND role = 'MANAGER'
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void clearDeleted() {
        String query = """
                DELETE FROM restaurants
                WHERE is_deleted = true AND id NOT IN (
                    SELECT restaurant_id
                    FROM orders
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<RestaurantStatistic> findStatisticByFilter(RestaurantsStatisticFilter filter) {
        String query = """
                WITH managers AS (
                    SELECT restaurant_id, user_id AS manager_user_id,
                           passport_number AS manager_passport_number,
                           surname AS manager_surname
                    FROM employees e JOIN users u ON e.user_id = u.id
                    WHERE role = 'MANAGER'
                )
                SELECT r.id, address, manager_user_id, manager_passport_number, manager_surname, is_deleted,
                    COUNT(DISTINCT o.id) AS number_of_orders,
                    COALESCE (SUM(cm.price * cm.amount_in_order), 0) AS total_orders_price
                FROM restaurants r
                    LEFT JOIN managers m ON r.id = m.restaurant_id
                    LEFT JOIN orders o ON r.id = o.restaurant_id AND o.date_created BETWEEN ? AND ?
                    LEFT JOIN client_meals cm ON o.id = cm.order_id
                GROUP BY r.id, address, manager_user_id, manager_passport_number, manager_surname, is_deleted
                """;
        query = filter.addFilteringAndPagination(query, Map.of(
                "id", "r.id",
                "address", "address",
                "managerUserId", "manager_user_id",
                "managerPassportNumber", "manager_passport_number",
                "managerSurname", "manager_surname",
                "numberOfOrders", "COUNT(DISTINCT o.id)",
                "totalOrdersPrice", "COALESCE (SUM(cm.price * cm.amount_in_order), 0)",
                "isDeleted", "is_deleted"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<RestaurantStatistic> restaurants = new ArrayList<>();
                while (rs.next())
                    restaurants.add(mapStatistic(rs));
                return restaurants;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countStatisticByFilter(RestaurantsStatisticFilter filter) {
        String query = """
                WITH managers AS (
                    SELECT restaurant_id, user_id AS manager_user_id,
                           passport_number AS manager_passport_number,
                           surname AS manager_surname
                    FROM employees e JOIN users u ON e.user_id = u.id
                    WHERE role = 'MANAGER'
                )
                SELECT 1
                FROM restaurants r
                    LEFT JOIN managers m ON r.id = m.restaurant_id
                    LEFT JOIN orders o ON r.id = o.restaurant_id AND o.date_created BETWEEN ? AND ?
                    LEFT JOIN client_meals cm ON o.id = cm.order_id
                GROUP BY r.id, address, manager_user_id, manager_passport_number, manager_surname, is_deleted
                """;
        query = filter.addFiltering(query, Map.of(
                "address", "address",
                "managerUserId", "manager_user_id",
                "numberOfOrders", "COUNT(DISTINCT o.id)",
                "totalOrdersPrice", "COALESCE (SUM(cm.price * cm.amount_in_order), 0)",
                "isDeleted", "is_deleted"
            )
        );
        query = "SELECT COUNT(*) FROM (" + query + ")";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return 0;
    }

    private RestaurantEntity map(ResultSet rs) throws SQLException {
        return new RestaurantEntity(
                rs.getInt("id"),
                rs.getString("address"),
                rs.getBoolean("is_deleted")
        );
    }

    private RestaurantStatistic mapStatistic(ResultSet rs) throws SQLException {
        return new RestaurantStatistic(
                rs.getInt("id"),
                rs.getString("address"),
                rs.getObject("manager_user_id", Integer.class),
                rs.getString("manager_passport_number"),
                rs.getString("manager_surname"),
                rs.getInt("number_of_orders"),
                rs.getInt("total_orders_price"),
                rs.getBoolean("is_deleted")
        );
    }
}
