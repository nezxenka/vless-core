package com.nezxenka.vlesscore.server.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ApiAuthFilter {

    private final String apiSecret;

    public ApiAuthFilter(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public boolean check(HttpExchange ex) throws IOException {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.equals("Bearer " + apiSecret)) {
            ApiResponseBuilder.sendJson(ex, 403, "{\"error\":\"forbidden\"}");
            return false;
        }
        return true;
    }
}
