package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.filters.EmployeesFilter;
import ua.edu.ukma.db.kfc.filters.EmployeesStatisticFilter;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.model.helper.EmployeeStatistic;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

@ApplicationScoped
public class EmployeeRepository extends BaseRepository<EmployeeEntity, Integer> {

    @Inject
    private EnumsMapper enumsMapper;

    @Override
    public Optional<EmployeeEntity> findById(Integer id) {
        return findByUserId(id);
    }

    public Optional<EmployeeEntity> findByUserId(int userId) {
        String query = """
                SELECT user_id, username, passport_number, surname, first_name, middle_name,
                    salary, birth_date, role AS position, manager_user_id, restaurant_id
                FROM employees JOIN users ON employees.user_id = users.id
                WHERE user_id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public Optional<EmployeeEntity> findByUsername(String username) {
        String query = """
                SELECT user_id, username, passport_number, surname, first_name, middle_name,
                    salary, birth_date, role AS position, manager_user_id, restaurant_id
                FROM employees JOIN users ON employees.user_id = users.id
                WHERE username = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public Optional<Integer> findUserIdByPassportNumber(String passportNumber) {
        String query = "SELECT user_id FROM employees WHERE passport_number = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, passportNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public List<EmployeeEntity> findByFilter(EmployeesFilter filter) {
        String query = """
                SELECT DISTINCT e.user_id, username, passport_number, surname, first_name, middle_name,
                    salary, birth_date, role AS position, manager_user_id, restaurant_id
                FROM employees e
                    JOIN users u ON e.user_id = u.id
                    LEFT JOIN user_emails ON e.user_id = user_emails.user_id
                    LEFT JOIN user_phones ON e.user_id = user_phones.user_id
                """;
        query = filter.addFilteringAndPagination(query, Map.ofEntries(
                    entry("id", "e.user_id"),
                    entry("userId", "e.user_id"),
                    entry("username", "username"),
                    entry("passportNumber", "passport_number"),
                    entry("surname", "surname"),
                    entry("firstName", "first_name"),
                    entry("middleName", "middle_name"),
                    entry("phone", "phone"),
                    entry("email", "email"),
                    entry("salary", "salary"),
                    entry("birthDate", "birth_date"),
                    entry("position", "role"),
                    entry("managerUserId", "manager_user_id"),
                    entry("restaurantId", "restaurant_id")
                )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<EmployeeEntity> employees = new ArrayList<>();
                while (rs.next())
                    employees.add(map(rs));
                return employees;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countByFilter(EmployeesFilter filter) {
        String query = """
                SELECT COUNT (DISTINCT e.user_id)
                FROM employees e
                    JOIN users u ON e.user_id = u.id
                    LEFT JOIN user_emails ON e.user_id = user_emails.user_id
                    LEFT JOIN user_phones ON e.user_id = user_phones.user_id
                """;
        query = filter.addFiltering(query, Map.ofEntries(
                    entry("username", "username"),
                    entry("passportNumber", "passport_number"),
                    entry("surname", "surname"),
                    entry("firstName", "first_name"),
                    entry("middleName", "middle_name"),
                    entry("phone", "phone"),
                    entry("email", "email"),
                    entry("salary", "salary"),
                    entry("birthDate", "birth_date"),
                    entry("position", "role"),
                    entry("managerUserId", "manager_user_id"),
                    entry("restaurantId", "restaurant_id")
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
    public Integer save(EmployeeEntity entity) {
        String query = """
            INSERT INTO employees (user_id, passport_number, surname, first_name, middle_name, salary, birth_date, manager_user_id, restaurant_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, entity.getUserId());
            stmt.setString(2, entity.getPassportNumber());
            stmt.setString(3, entity.getSurname());
            stmt.setString(4, entity.getFirstName());
            stmt.setString(5, entity.getMiddleName());
            stmt.setBigDecimal(6, entity.getSalary());
            stmt.setDate(7, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            stmt.setInt(8, entity.getManagerUserId());
            stmt.setInt(9, entity.getRestaurantId());
            stmt.executeUpdate();
            return entity.getUserId();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void update(EmployeeEntity entity) {
        String query = """
                UPDATE employees
                SET passport_number = ?, surname = ?, first_name = ?, middle_name = ?, salary = ?, birth_date = ?
                WHERE user_id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, entity.getPassportNumber());
            stmt.setString(2, entity.getSurname());
            stmt.setString(3, entity.getFirstName());
            stmt.setString(4, entity.getMiddleName());
            stmt.setBigDecimal(5, entity.getSalary());
            stmt.setDate(6, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            stmt.setInt(7, entity.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public boolean hasSubordinates(int userId) {
        String query = """
                SELECT EXISTS (
                    SELECT *
                    FROM employees
                    WHERE manager_user_id = ?
                )
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<EmployeeStatistic> findStatisticByFilter(EmployeesStatisticFilter filter) {
        String query = """
                SELECT user_id, passport_number, surname, role AS position,
                    e.restaurant_id, r.address AS restaurant_address,
                    COUNT(DISTINCT o.id) AS number_of_orders,
                    COALESCE (SUM(cm.price * cm.amount_in_order), 0) AS total_orders_price
                FROM employees e
                    JOIN users u ON e.user_id = u.id
                    LEFT JOIN restaurants r ON e.restaurant_id = r.id
                    LEFT JOIN orders o ON e.user_id = o.employee_user_id AND o.date_created BETWEEN ? AND ?
                    LEFT JOIN client_meals cm ON o.id = cm.order_id
                GROUP BY user_id, passport_number, surname, role, e.restaurant_id, r.address
                """;
        query = filter.addFilteringAndPagination(query, Map.of(
                "id", "user_id",
                "userId", "user_id",
                "passportNumber", "passport_number",
                "surname", "surname",
                "position", "role",
                "restaurantId", "e.restaurant_id",
                "restaurantAddress", "r.address",
                "numberOfOrders", "COUNT(DISTINCT o.id)",
                "totalOrdersPrice", "COALESCE (SUM(cm.price * cm.amount_in_order), 0)"
            )
        );
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query))  {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                List<EmployeeStatistic> statistics = new ArrayList<>();
                while (rs.next())
                    statistics.add(mapStatistic(rs));
                return statistics;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public long countStatisticByFilter(EmployeesStatisticFilter filter) {
        String query = """
                SELECT 1
                FROM employees e
                    JOIN users u ON e.user_id = u.id
                    LEFT JOIN restaurants r ON e.restaurant_id = r.id
                    LEFT JOIN orders o ON e.user_id = o.employee_user_id AND o.date_created BETWEEN ? AND ?
                    LEFT JOIN client_meals cm ON o.id = cm.order_id
                GROUP BY user_id, passport_number, surname, role, e.restaurant_id, r.address
                """;
        query = filter.addFiltering(query, Map.of(
                "passportNumber", "passport_number",
                "position", "role",
                "restaurantId", "e.restaurant_id",
                "numberOfOrders", "COUNT(DISTINCT o.id)",
                "totalOrdersPrice", "COALESCE (SUM(cm.price * cm.amount_in_order), 0)"
            )
        );
        query = "SELECT COUNT(*) FROM (" + query + ")";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query))  {
            filter.setParameters(stmt, transactionManager.currentTransaction());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return 0;
    }

    private EmployeeEntity map(ResultSet rs) throws SQLException {
        return new EmployeeEntity(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("passport_number"),
                rs.getString("surname"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getBigDecimal("salary"),
                TimeUtils.mapToLocalDate(rs.getDate("birth_date")),
                enumsMapper.mapToPosition(rs.getString("position")),
                rs.getObject("manager_user_id", Integer.class),
                rs.getObject("restaurant_id", Integer.class)
        );
    }

    private EmployeeStatistic mapStatistic(ResultSet rs) throws SQLException {
        return new EmployeeStatistic(
                rs.getInt("user_id"),
                rs.getString("passport_number"),
                rs.getString("surname"),
                enumsMapper.mapToPosition(rs.getString("position")),
                rs.getObject("restaurant_id", Integer.class),
                rs.getString("restaurant_address"),
                rs.getInt("number_of_orders"),
                rs.getBigDecimal("total_orders_price")
        );
    }
}
