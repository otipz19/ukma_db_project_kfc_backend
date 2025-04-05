package ua.edu.ukma.db.kfc.utils;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.sql.Date;
import java.time.*;

@UtilityClass
public class TimeUtils {

    public long getCurrentTimeUTC() {
        return Instant.now().toEpochMilli();
    }

    public LocalDateTime getCurrentDateTimeUTC() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public LocalDateTime mapToUtcDateTime(@Nullable final OffsetDateTime offsetDateTime) {
        return offsetDateTime == null
                ? null
                : offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public OffsetDateTime wrapToUtcDateTime(@Nullable final LocalDateTime localDateTime) {
        return localDateTime == null
                ? null
                : localDateTime.atOffset(ZoneOffset.UTC);
    }

    public LocalDate mapToLocalDate(@Nullable final Date sqlDate) {
        return sqlDate == null
                ? null
                : sqlDate.toLocalDate();
    }

    public Date mapToSqlDate(@Nullable final LocalDate localDate) {
        return localDate == null
                ? null
                : Date.valueOf(localDate);
    }
}
