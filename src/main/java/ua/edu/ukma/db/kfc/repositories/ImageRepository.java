package ua.edu.ukma.db.kfc.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.DataBaseException;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.model.entities.ImageEntity;
import ua.edu.ukma.db.kfc.model.helper.ImagePK;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@ApplicationScoped
public class ImageRepository extends BaseRepository<ImageEntity, ImagePK> {

    @Inject
    private EnumsMapper enumsMapper;

    @Override
    public Optional<ImageEntity> findById(ImagePK id) {
        String query = "SELECT * FROM images WHERE type = ? AND title = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, enumsMapper.mapToSting(id.type()));
            stmt.setString(2, id.title());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public ImagePK save(ImageEntity entity) {
        String query = """
                INSERT INTO images (type, title, image, mime_type, last_modified)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (type, title) DO UPDATE SET
                    image = EXCLUDED.image,
                    mime_type = EXCLUDED.mime_type,
                    last_modified = EXCLUDED.last_modified
                """;
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, enumsMapper.mapToSting(entity.getType()));
            stmt.setString(2, entity.getTitle());
            stmt.setBytes(3, entity.getImage());
            stmt.setString(4, entity.getMimeType());
            stmt.setTimestamp(5, TimeUtils.mapToSqlTimestamp(entity.getLastModified()));
            stmt.executeUpdate();
            return new ImagePK(entity.getType(), entity.getTitle());
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    public void delete(ImagePK id) {
        String query = "DELETE FROM images WHERE type = ? AND title = ?";
        try (PreparedStatement stmt = transactionManager.currentTransaction().prepareStatement(query)) {
            stmt.setString(1, enumsMapper.mapToSting(id.type()));
            stmt.setString(2, id.title());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    private ImageEntity map(ResultSet rs) throws SQLException {
        return new ImageEntity(
                enumsMapper.map(rs.getString("type")),
                rs.getString("title"),
                rs.getBytes("image"),
                rs.getString("mime_type"),
                TimeUtils.mapToLocalDateTime(rs.getTimestamp("last_modified"))
        );
    }
}
