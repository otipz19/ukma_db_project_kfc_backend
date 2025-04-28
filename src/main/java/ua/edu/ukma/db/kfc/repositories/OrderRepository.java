package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.OrderEntity;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.sql.Types.INTEGER;

@ApplicationScoped
public class OrderRepository extends BaseRepository<OrderEntity, Integer> {

    @Override
    public Optional<OrderEntity> findById(Integer id) {
        String query = """
                SELECT o.id,
                    (SELECT SUM(price * amount_in_order) FROM client_meals WHERE order_id = o.id) as cost,
                    o.date_created, o.is_completed,
                    o.restaurant_id,
                    o.client_id, c.user_id AS client_user_id,
                    o.employee_id, e.user_id AS employee_user_id
                FROM orders o
                    LEFT JOIN clients c ON o.client_id = c.id
                    LEFT JOIN employees e ON o.employee_id = e.id
                WHERE o.id = ?
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
                SELECT o.id,
                    (SELECT SUM(price * amount_in_order) FROM client_meals WHERE order_id = o.id) as cost,
                    o.date_created, o.is_completed,
                    o.restaurant_id,
                    o.client_id, c.user_id AS client_user_id,
                    o.employee_id, e.user_id AS employee_user_id
                FROM orders o
                    LEFT JOIN clients c ON o.client_id = c.id
                    LEFT JOIN employees e ON o.employee_id = e.id
                WHERE o.id = ANY (?)
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

    @Override
    public Integer save(OrderEntity entity) {
        String query = """
            INSERT INTO orders (date_created, is_completed, restaurant_id, client_id, employee_id)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setTimestamp(1, TimeUtils.mapToSqlTimestamp(entity.getDateCreated()));
            stmt.setBoolean(2, entity.isCompleted());
            stmt.setInt(3, entity.getRestaurantId());
            if (entity.getClientId() != null) stmt.setInt(4, entity.getClientId());
            else stmt.setNull(4, INTEGER);
            if (entity.getEmployeeId() != null) stmt.setInt(5, entity.getEmployeeId());
            else stmt.setNull(5, INTEGER);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new DataBaseException("Failed to save order");
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public boolean existsByRestaurantId(int restaurantId) {
        String query = "SELECT EXISTS (SELECT * FROM orders WHERE restaurant_id = ?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
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

    private OrderEntity map(ResultSet resultSet) throws SQLException {
        return new OrderEntity(
                resultSet.getInt("id"),
                resultSet.getBigDecimal("cost"),
                TimeUtils.mapToLocalDateTime(resultSet.getTimestamp("date_created")),
                resultSet.getBoolean("is_completed"),
                resultSet.getInt("restaurant_id"),
                resultSet.getObject("client_id", Integer.class),
                resultSet.getObject("client_user_id", Integer.class),
                resultSet.getObject("employee_id", Integer.class),
                resultSet.getObject("employee_user_id", Integer.class)
        );
    }
}
