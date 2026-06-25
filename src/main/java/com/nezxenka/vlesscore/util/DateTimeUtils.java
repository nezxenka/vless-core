package com.nezxenka.vlesscore.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private DateTimeUtils() {}

    public static String formatEpochMillis(long epochMillis) {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMillis),
            ZoneId.systemDefault()
        ).format(FORMATTER);
    }

    public static long daysToMs(int days) {
        return (long) days * 24 * 60 * 60 * 1000;
    }

    public static long remainingDays(long expiresAt) {
        long diff = expiresAt - System.currentTimeMillis();
        return Math.max(0, diff / (1000L * 60 * 60 * 24));
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
