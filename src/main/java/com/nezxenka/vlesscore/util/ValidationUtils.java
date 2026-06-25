package com.nezxenka.vlesscore.util;

public final class ValidationUtils {

    private ValidationUtils() {}

    public static boolean nonBlank(String s) {
        return s != null && !s.isBlank();
    }

    public static boolean positiveInt(int value) {
        return value > 0;
    }

    public static boolean validPort(int port) {
        return port >= 1 && port <= 65535;
    }

    public static String requireNonBlank(String s, String label) {
        if (!nonBlank(s)) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return s;
    }
}
