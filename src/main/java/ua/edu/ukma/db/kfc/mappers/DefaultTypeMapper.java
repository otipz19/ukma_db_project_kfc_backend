package ua.edu.ukma.db.kfc.mappers;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@ApplicationScoped
public class DefaultTypeMapper {

    @Nullable
    public LocalDateTime mapToUtcTime(@Nullable final OffsetDateTime offsetDateTime) {
        return TimeUtils.mapToUtcDateTime(offsetDateTime);
    }

    @Nullable
    public OffsetDateTime wrapToUtcDateTime(@Nullable final LocalDateTime localDateTime) {
        return TimeUtils.wrapToUtcDateTime(localDateTime);
    }
}
