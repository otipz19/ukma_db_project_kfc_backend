package ua.edu.ukma.db.kfc.repositories;


import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.model.entities.ClientMealIngredientEntity;
import ua.edu.ukma.db.kfc.model.helper.ClientMealIngredientPK;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClientMealIngredientRepository extends BaseRepository<ClientMealIngredientEntity, ClientMealIngredientPK> {

    @Override
    public Optional<ClientMealIngredientEntity> findById(ClientMealIngredientPK id) {
        String sql = "SELECT * FROM client_meal_ingredient " +
                "WHERE client_meal_id = ? AND ingredient_id = ?";
        try (PreparedStatement stmt = transactionManager
                .currentTransaction()
                .prepareStatement(sql)) {
            stmt.setInt(1, id.clientMealId());
            stmt.setInt(2, id.ingredientId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public ClientMealIngredientPK save(ClientMealIngredientEntity entity) {
        String sql = "INSERT INTO client_meal_ingredient " +
                "(client_meal_id, ingredient_id, amount) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = transactionManager
                .currentTransaction()
                .prepareStatement(sql)) {
            stmt.setInt(1, entity.getClientMealId());
            stmt.setInt(2, entity.getIngredientId());
            stmt.setInt(3, entity.getAmount());
            stmt.executeUpdate();
            return new ClientMealIngredientPK(
                    entity.getClientMealId(),
                    entity.getIngredientId()
            );
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void saveAll(Collection<ClientMealIngredientEntity> entities) {
        String sql = "INSERT INTO client_meal_ingredient " +
                "(client_meal_id, ingredient_id, amount) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = transactionManager
                .currentTransaction()
                .prepareStatement(sql)) {
            for (ClientMealIngredientEntity e : entities) {
                stmt.setInt(1, e.getClientMealId());
                stmt.setInt(2, e.getIngredientId());
                stmt.setInt(3, e.getAmount());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException ex) {
            throw new DataBaseException(ex);
        }
    }

    public List<ClientMealIngredientEntity> findByClientMealId(int clientMealId) {
        String sql = "SELECT * FROM client_meal_ingredient WHERE client_meal_id = ?";
        try (PreparedStatement stmt = transactionManager
                .currentTransaction()
                .prepareStatement(sql)) {
            stmt.setInt(1, clientMealId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<ClientMealIngredientEntity> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public List<ClientMealIngredientEntity> findByClientMealIds(Collection<Integer> ids) {
        String sql = "SELECT * FROM client_meal_ingredient WHERE client_meal_id = ANY (?)";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(sql)) {
            stmt.setArray(1, transactionManager.currentTransaction().createArrayOf(ids, Integer.class));
            try (ResultSet rs = stmt.executeQuery()) {
                List<ClientMealIngredientEntity> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private ClientMealIngredientEntity map(ResultSet rs) throws SQLException {
        return new ClientMealIngredientEntity(
                rs.getInt("client_meal_id"),
                rs.getInt("ingredient_id"),
                rs.getInt("amount")
        );
    }
}

