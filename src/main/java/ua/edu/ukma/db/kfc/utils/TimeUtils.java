package ua.edu.ukma.db.kfc.utils;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@UtilityClass
public class TimeUtils {

    public long getCurrentTimeUTC() {
        return Instant.now().toEpochMilli();
    }

    public LocalDateTime getCurrentDateTimeUTC() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public LocalDateTime mapToUtcTime(@Nullable final OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }

        return offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public OffsetDateTime wrapToUtcDateTime(@Nullable final LocalDateTime localDateTime) {
        return localDateTime == null
                ? null
                : localDateTime.atOffset(ZoneOffset.UTC);
    }
}
