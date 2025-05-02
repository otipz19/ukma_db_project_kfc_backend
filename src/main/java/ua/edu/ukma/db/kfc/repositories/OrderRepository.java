package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.filters.OrdersFilter;
import ua.edu.ukma.db.kfc.model.entities.OrderEntity;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static java.sql.Types.INTEGER;

@ApplicationScoped
public class OrderRepository extends BaseRepository<OrderEntity, Integer> {

    @Override
    public Optional<OrderEntity> findById(Integer id) {
        String query = """
                SELECT id,
                    (SELECT SUM(price * amount_in_order) FROM client_meals WHERE order_id = orders.id) as cost,
                    date_created, is_completed,
                    restaurant_id, client_user_id, employee_user_id
                FROM orders
                WHERE id = ?
                """;
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

    public List<OrderEntity> findByIds(Collection<Integer> ids) {
        String query = """
                SELECT id,
                    (SELECT SUM(price * amount_in_order) FROM client_meals WHERE order_id = orders.id) as cost,
                    date_created, is_completed,
                    restaurant_id, client_user_id, employee_user_id
                FROM orders
                WHERE id = ANY (?)
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
            try (ResultSet rs = stmt.executeQuery()) {
                List<OrderEntity> result = new ArrayList<>();
                while (rs.next())
                    result.add(map(rs));
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<OrderEntity> findByFilter(OrdersFilter filter) {
        String query = """
                SELECT id,
                    (SELECT SUM(price * amount_in_order) FROM client_meals WHERE order_id = orders.id) as cost,
                    date_created, is_completed,
                    restaurant_id, client_user_id, employee_user_id
                FROM orders
                """;
        query = filter.addFilteringAndPagination(query, Map.of(
                    "id", "id",
                    "restaurantId", "restaurant_id",
                    "employeeUserId", "employee_user_id",
                    "clientUserId", "client_user_id",
                    "cost", "(SELECT SUM(price * amount_in_order) FROM client_meals WHERE order_id = orders.id)",
                    "dateCreated", "date_created",
                    "isCompleted", "is_completed"
                )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<OrderEntity> result = new ArrayList<>();
                while (rs.next())
                    result.add(map(rs));
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countByFilter(OrdersFilter filter) {
        String query = "SELECT COUNT(*) FROM orders";
        query = filter.addFiltering(query, Map.of(
                    "id", "id",
                    "restaurantId", "restaurant_id",
                    "employeeUserId", "employee_user_id",
                    "clientUserId", "client_user_id",
                    "cost", "(SELECT SUM(price * amount_in_order) FROM client_meals WHERE order_id = orders.id)",
                    "dateCreated", "date_created",
                    "isCompleted", "is_completed"
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

    @Override
    public Integer save(OrderEntity entity) {
        String query = """
            INSERT INTO orders (date_created, is_completed, restaurant_id, client_user_id, employee_user_id)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setTimestamp(1, TimeUtils.mapToSqlTimestamp(entity.getDateCreated()));
            stmt.setBoolean(2, entity.isCompleted());
            stmt.setInt(3, entity.getRestaurantId());
            if (entity.getClientUserId() != null) stmt.setInt(4, entity.getClientUserId());
            else stmt.setNull(4, INTEGER);
            if (entity.getEmployeeUserId() != null) stmt.setInt(5, entity.getEmployeeUserId());
            else stmt.setNull(5, INTEGER);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new DataBaseException("Failed to save order");
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void complete(int orderId) {
        String query = "UPDATE orders SET is_completed = true WHERE id = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void addOrderBonuses(int orderId, int clientUserId) {
        String query = """
                UPDATE clients
                SET bonuses = bonuses + (SELECT CEIL(SUM(price * amount_in_order) * 0.01) FROM client_meals WHERE order_id = ?)
                WHERE user_id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, clientUserId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public int deleteBefore(LocalDateTime dateTime) {
        String query = "DELETE FROM orders WHERE date_created < ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setTimestamp(1, TimeUtils.mapToSqlTimestamp(dateTime));
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private OrderEntity map(ResultSet resultSet) throws SQLException {
        return new OrderEntity(
                resultSet.getInt("id"),
                resultSet.getBigDecimal("cost"),
                TimeUtils.mapToLocalDateTime(resultSet.getTimestamp("date_created")),
                resultSet.getBoolean("is_completed"),
                resultSet.getInt("restaurant_id"),
                resultSet.getObject("client_user_id", Integer.class),
                resultSet.getObject("employee_user_id", Integer.class)
        );
    }
}
