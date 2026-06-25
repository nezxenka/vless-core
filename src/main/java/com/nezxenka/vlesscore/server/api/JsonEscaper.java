package com.nezxenka.vlesscore.server.api;

public final class JsonEscaper {

    private JsonEscaper() {}

    public static String escape(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
