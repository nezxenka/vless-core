package com.nezxenka.vlesscore.server.api.handler;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.server.api.ApiAuthFilter;
import com.nezxenka.vlesscore.server.api.ApiQueryParser;
import com.nezxenka.vlesscore.server.api.ApiResponseBuilder;
import com.nezxenka.vlesscore.server.api.JsonEscaper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class ApiKeyInfoHandler implements HttpHandler {

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final ApiAuthFilter auth;

    public ApiKeyInfoHandler(AppConfig config, TokenDao tokenDao) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.auth = new ApiAuthFilter(config.getApiSecret());
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!auth.check(ex)) return;

        try {
            Map<String, String> params = ApiQueryParser.parse(ex.getRequestURI().getQuery());
            String tokenStr = params.getOrDefault("token", "");

            Token t = tokenDao.findByToken(tokenStr);
            if (t == null) {
                ApiResponseBuilder.sendNotFound(ex);
                return;
            }

            ApiResponseBuilder.sendJson(ex, 200, String.format(
                    "{\"token\":\"%s\",\"status\":\"%s\",\"expires_at\":%d,"
                            + "\"remaining_days\":%d,\"connections\":%d,"
                            + "\"bytes_up\":%d,\"bytes_down\":%d}",
                    t.getToken(), t.getStatus().name(), t.getExpiresAt(),
                    t.getRemainingDays(), t.getConnections(),
                    t.getBytesUp(), t.getBytesDown()
            ));

        } catch (Exception e) {
            ApiResponseBuilder.sendJson(ex, 500,
                    "{\"error\":\"" + JsonEscaper.escape(e.getMessage()) + "\"}");
        }
    }
}
