package com.nezxenka.vlesscore.server.api.handler;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.server.api.ApiResponseBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ApiPingHandler implements HttpHandler {

    private final AppConfig config;

    public ApiPingHandler(AppConfig config) {
        this.config = config;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        ApiResponseBuilder.sendJson(ex, 200,
                "{\"status\":\"ok\",\"server\":\"" + config.getServerName() + "\"}");
    }
}
