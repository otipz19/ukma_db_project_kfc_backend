package ua.edu.ukma.db.kfc.utils;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.sql.Date;
import java.sql.Timestamp;
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

    public LocalDateTime mapToCurrentTimeZone(@Nullable final LocalDateTime localDateTime) {
        return localDateTime == null
                ? null
                : wrapToUtcTimeZone(localDateTime).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public OffsetDateTime wrapToUtcTimeZone(@Nullable final LocalDateTime localDateTime) {
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

    public LocalDateTime mapToLocalDateTime(@Nullable final Timestamp sqlTimestamp) {
        return sqlTimestamp == null
                ? null
                : sqlTimestamp.toLocalDateTime();
    }

    public Timestamp mapToSqlTimestamp(@Nullable final LocalDateTime localDateTime) {
        return localDateTime == null
                ? null
                : Timestamp.valueOf(localDateTime);
    }

    public LocalDate minDate() {
        return LocalDate.of(1900, 1, 1);
    }

    public LocalDate maxDate() {
        return LocalDate.of(9999, 12, 31);
    }
}
