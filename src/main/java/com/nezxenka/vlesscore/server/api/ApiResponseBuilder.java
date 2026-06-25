package com.nezxenka.vlesscore.server.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ApiResponseBuilder {

    private ApiResponseBuilder() {}

    public static void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendMethodNotAllowed(HttpExchange ex) throws IOException {
        sendJson(ex, 405, "{\"error\":\"method not allowed\"}");
    }

    public static void sendNotFound(HttpExchange ex) throws IOException {
        sendJson(ex, 404, "{\"error\":\"not found\"}");
    }
}
