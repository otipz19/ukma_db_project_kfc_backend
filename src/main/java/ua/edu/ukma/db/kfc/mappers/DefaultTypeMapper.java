package ua.edu.ukma.db.kfc.mappers;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.Named;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@ApplicationScoped
public class DefaultTypeMapper {

    public static final String MAP_TO_UTC_TIME = "MAP_TO_UTC_TIME";
    public static final String WRAP_TO_UTC_OFFSET_DATE_TIME = "WRAP_TO_UTC_OFFSET_DATE_TIME";

    @Nullable
    @Named(MAP_TO_UTC_TIME)
    public LocalDateTime mapToUtcTime(@Nullable final OffsetDateTime offsetDateTime) {
        return TimeUtils.mapToUtcDateTime(offsetDateTime);
    }

    @Nullable
    @Named(WRAP_TO_UTC_OFFSET_DATE_TIME)
    public OffsetDateTime wrapToUtcDateTime(@Nullable final LocalDateTime localDateTime) {
        return TimeUtils.wrapToUtcDateTime(localDateTime);
    }
}
