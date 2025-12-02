package net.ryzen.paylinksystem.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Date convertStringToDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        LocalDateTime localDateTime;
        
        try {
            localDateTime = LocalDateTime.parse(dateString, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, DATE_FORMATTER);
                localDateTime = localDate.atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Unable to parse date string: " + dateString + 
                    ". Expected formats: yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd", ex);
            }
        }

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String convertDateToString(Date date) {
        if (date == null) {
            return null;
        }

        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DATETIME_FORMATTER);
    }

    public static String convertDateToStringDate(Date date) {
        if (date == null) {
            return null;
        }

        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DATE_FORMATTER);
    }

    public static Date addMinutes(Date date, int minutes) {
        Instant instant = date.toInstant();
        Instant newInstant = instant.plus(Duration.ofMinutes(minutes));
        return Date.from(newInstant);
    }


}
