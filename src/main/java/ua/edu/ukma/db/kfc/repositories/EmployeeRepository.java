package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Types.*;

@ApplicationScoped
public class EmployeeRepository extends BaseRepository<EmployeeEntity, Integer> {

    @Inject
    private EnumsMapper enumsMapper;

    @Override
    public Optional<EmployeeEntity> findById(Integer id) {
        String query = """
                SELECT e1.id, e1.user_id, u.username, e1.passport_number, e1.surname, e1.first_name, e1.middle_name,
                    e1.salary, e1.birth_date, u.role AS position, e1.manager_id, e2.user_id AS manager_user_id,
                    e1.restaurant_id, e1.is_deleted
                FROM employees e1
                    LEFT JOIN users u ON e1.user_id = u.id
                    LEFT JOIN employees e2 ON e1.manager_id = e2.id
                WHERE e1.id = ? AND e1.is_deleted = false
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

    public Optional<EmployeeEntity> findByUserId(int userId) {
        String query = """
                SELECT e1.id, e1.user_id, u.username, e1.passport_number, e1.surname, e1.first_name, e1.middle_name,
                    e1.salary, e1.birth_date, u.role AS position, e1.manager_id, e2.user_id AS manager_user_id,
                    e1.restaurant_id, e1.is_deleted
                FROM employees e1
                    LEFT JOIN users u ON e1.user_id = u.id
                    LEFT JOIN employees e2 ON e1.manager_id = e2.id
                WHERE e1.user_id = ? AND e1.is_deleted = false
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
                SELECT e1.id, e1.user_id, u.username, e1.passport_number, e1.surname, e1.first_name, e1.middle_name,
                    e1.salary, e1.birth_date, u.role AS position, e1.manager_id, e2.user_id AS manager_user_id,
                    e1.restaurant_id, e1.is_deleted
                FROM employees e1
                    LEFT JOIN users u ON e1.user_id = u.id
                    LEFT JOIN employees e2 ON e1.manager_id = e2.id
                WHERE u.username = ? AND e1.is_deleted = false
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

    public Optional<Integer> findIdByUserId(int userId) {
        String query = """
                SELECT id
                FROM employees
                WHERE user_id = ? AND is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public Optional<Integer> findIdByPassportNumber(String passportNumber) {
        String query = """
                SELECT id
                FROM employees
                WHERE passport_number = ? AND is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, passportNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    public List<EmployeeEntity> findAll(Integer restaurantId) {
        String query = """
                SELECT e1.id, e1.user_id, u.username, e1.passport_number, e1.surname, e1.first_name, e1.middle_name,
                    e1.salary, e1.birth_date, u.role AS position, e1.manager_id, e2.user_id AS manager_user_id,
                    e1.restaurant_id, e1.is_deleted
                FROM employees e1
                    LEFT JOIN users u ON e1.user_id = u.id
                    LEFT JOIN employees e2 ON e1.manager_id = e2.id
                WHERE (? IS NULL OR e1.restaurant_id = ?) AND e1.is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            if (restaurantId != null) {
                stmt.setInt(1, restaurantId);
                stmt.setInt(2, restaurantId);
            }
            else {
                stmt.setNull(1, INTEGER);
                stmt.setNull(2, INTEGER);
            }
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

    @Override
    public Integer save(EmployeeEntity entity) {
        String query = """
            INSERT INTO employees (user_id, passport_number, surname, first_name, middle_name, salary, birth_date, manager_id, restaurant_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, entity.getUserId());
            stmt.setString(2, entity.getPassportNumber());
            stmt.setString(3, entity.getSurname());
            stmt.setString(4, entity.getFirstName());
            stmt.setString(5, entity.getMiddleName());
            stmt.setBigDecimal(6, entity.getSalary());
            stmt.setDate(7, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            stmt.setInt(8, entity.getManagerId());
            stmt.setInt(9, entity.getRestaurantId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new DataBaseException("Failed to save employee");
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void update(EmployeeEntity entity) {
        String query = """
                UPDATE employees
                SET passport_number = ?, surname = ?, first_name = ?, middle_name = ?, salary = ?, birth_date = ?
                WHERE id = ? AND is_deleted = false
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, entity.getPassportNumber());
            stmt.setString(2, entity.getSurname());
            stmt.setString(3, entity.getFirstName());
            stmt.setString(4, entity.getMiddleName());
            stmt.setBigDecimal(5, entity.getSalary());
            stmt.setDate(6, TimeUtils.mapToSqlDate(entity.getBirthDate()));
            stmt.setInt(7, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void deleteByUserId(int userId) {
        String query = """
                UPDATE employees
                SET is_deleted = true, user_id = null
                WHERE user_id = ?
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public boolean hasSubordinates(int id) {
        String query = """
                SELECT EXISTS (
                    SELECT *
                    FROM employees
                    WHERE manager_id = ? AND is_deleted = false
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

    private EmployeeEntity map(ResultSet rs) throws SQLException {
        return new EmployeeEntity(
                rs.getInt("id"),
                rs.getObject("user_id", Integer.class),
                rs.getString("username"),
                rs.getString("passport_number"),
                rs.getString("surname"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getBigDecimal("salary"),
                TimeUtils.mapToLocalDate(rs.getDate("birth_date")),
                enumsMapper.mapToPosition(rs.getString("position")),
                rs.getObject("manager_id", Integer.class),
                rs.getObject("manager_user_id", Integer.class),
                rs.getObject("restaurant_id", Integer.class),
                rs.getBoolean("is_deleted")
        );
    }
}
